package tk.fishfish.cas.server.authentication;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证，查询授权服务
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Slf4j
public class AccountAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private final String sql;
    private final String fieldPassword;
    private final String fieldExpired;
    private final String fieldDisabled;
    private final Map<String, Object> principalAttributeMap;

    public AccountAuthenticationHandler(final String name,
                                        final ServicesManager servicesManager,
                                        final PrincipalFactory principalFactory,
                                        final Integer order,
                                        final DataSource dataSource,
                                        final String sql,
                                        final String fieldPassword,
                                        final String fieldExpired,
                                        final String fieldDisabled,
                                        final Map<String, Object> attributes) {
        super(name, servicesManager, principalFactory, order, dataSource);
        this.sql = sql;
        this.fieldPassword = fieldPassword;
        this.fieldExpired = fieldExpired;
        this.fieldDisabled = fieldDisabled;
        this.principalAttributeMap = attributes;

        if (StringUtils.isBlank(this.fieldPassword)) {
            log.warn("When the password field is left undefined, CAS will skip comparing database and user passwords for equality," +
                    " (specially if the query results do not contain the password field), and will instead only rely on a successful " +
                    "query execution with returned results in order to verify credentials");
        }
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential,
                                                                                        final String originalPassword)
            throws GeneralSecurityException, PreventedException {
        val attributes = Maps.<String, List<Object>>newHashMapWithExpectedSize(this.principalAttributeMap.size());
        val username = credential.getUsername();
        val password = credential.getPassword();
        try {
            val dbFields = query(credential);
            if (dbFields.containsKey(this.fieldPassword)) {
                val dbPassword = (String) dbFields.get(this.fieldPassword);

                val originalPasswordMatchFails = StringUtils.isNotBlank(originalPassword) && !matches(originalPassword, dbPassword);
                val originalPasswordEquals = StringUtils.isBlank(originalPassword) && !StringUtils.equals(password, dbPassword);
                if (originalPasswordMatchFails || originalPasswordEquals) {
                    throw new FailedLoginException("Password does not match value on record.");
                }
            } else {
                log.debug("Password field is not found in the query results. Checking for result count...");
                if (!dbFields.containsKey("total")) {
                    throw new FailedLoginException("Missing field 'total' from the query results for " + username);
                }

                val count = dbFields.get("total");
                if (count == null || !NumberUtils.isCreatable(count.toString())) {
                    throw new FailedLoginException("Missing field value 'total' from the query results for " + username + " or value not parseable as a number");
                }

                val number = NumberUtils.createNumber(count.toString());
                if (number.longValue() != 1) {
                    throw new FailedLoginException("No records found for user " + username);
                }
            }

            if (StringUtils.isNotBlank(this.fieldDisabled) && dbFields.containsKey(this.fieldDisabled)) {
                val dbDisabled = dbFields.get(this.fieldDisabled).toString();
                if (BooleanUtils.toBoolean(dbDisabled) || "1".equals(dbDisabled)) {
                    throw new AccountDisabledException("Account has been disabled");
                }
            }
            if (StringUtils.isNotBlank(this.fieldExpired) && dbFields.containsKey(this.fieldExpired)) {
                val dbExpired = dbFields.get(this.fieldExpired).toString();
                if (BooleanUtils.toBoolean(dbExpired) || "1".equals(dbExpired)) {
                    throw new AccountPasswordMustChangeException("Password has expired");
                }
            }
            collectPrincipalAttributes(attributes, dbFields);
            collectServicesPrincipalAttributes(attributes, username);
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            }
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException(e);
        }
        val principal = this.principalFactory.createPrincipal(username, attributes);
        return createHandlerResult(credential, principal, new ArrayList<>(0));
    }

    private Map<String, Object> query(final UsernamePasswordCredential credential) {
        if (this.sql.contains("?")) {
            return getJdbcTemplate().queryForMap(this.sql, credential.getUsername());
        }
        val parameters = new LinkedHashMap<String, Object>();
        parameters.put("username", credential.getUsername());
        parameters.put("password", credential.getPassword());
        return getNamedParameterJdbcTemplate().queryForMap(this.sql, parameters);
    }

    @SuppressWarnings("unchecked")
    private void collectPrincipalAttributes(final Map<String, List<Object>> attributes, final Map<String, Object> dbFields) {
        this.principalAttributeMap.forEach((key, names) -> {
            val attribute = dbFields.get(key);
            if (attribute != null) {
                log.debug("Found attribute [{}] from the query results", key);
                val attributeNames = (Collection<String>) names;
                attributeNames.forEach(s -> {
                    log.debug("Principal attribute [{}] is virtually remapped/renamed to [{}]", key, s);
                    attributes.put(s, CollectionUtils.wrap(attribute.toString()));
                });
            } else {
                log.warn("Requested attribute [{}] could not be found in the query results", key);
            }
        });
    }

    private void collectServicesPrincipalAttributes(final Map<String, List<Object>> attributes, final String username) {
        // 查询授权服务
        List<Object> serviceIds = getJdbcTemplate().queryForList(
                "SELECT s.service_Id FROM account_grant_service ags LEFT JOIN regex_registered_service s ON ags.serviceId = s.id where ags.username = ?",
                new Object[]{username},
                Object.class
        );
        attributes.put("serviceIds", serviceIds);
    }

}

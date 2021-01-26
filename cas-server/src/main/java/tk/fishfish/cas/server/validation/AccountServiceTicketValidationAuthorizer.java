package tk.fishfish.cas.server.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.validation.Assertion;
import org.apereo.cas.validation.ServiceTicketValidationAuthorizer;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 验证账号是否授权服务
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class AccountServiceTicketValidationAuthorizer implements ServiceTicketValidationAuthorizer {

    private final ServicesManager servicesManager;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void authorize(HttpServletRequest request, Service service, Assertion assertion) {
        RegisteredService registeredService = servicesManager.findServiceBy(service);
        if (registeredService == null) {
            log.warn("Service is not found in service registry");
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, "Service is not found in service registry.");
        }
        String username = assertion.getPrimaryAuthentication().getPrincipal().getId();
        log.debug("检测账号 {} 是否授权服务: {}", username, service.getId());
        List<String> serviceIds;
        try {
            serviceIds = queryGrantServiceIds(username);
        } catch (Exception e) {
            log.warn("Query grant services failed", e);
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, "Query grant services failed.");
        }
        for (String serviceId : serviceIds) {
            if ("*".equals(serviceId)) {
                return;
            }
            if (registeredService.matches(serviceId)) {
                return;
            }
        }
        log.warn("Service [{}] is not grant to account: {}", service.getId(), username);
        throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, "Service [" + service.getId() + "] is not grant to account: " + username);
    }

    private List<String> queryGrantServiceIds(final String username) {
        // 查询授权服务
        return jdbcTemplate.queryForList(
                "SELECT s.service_Id FROM account_grant_service ags LEFT JOIN regex_registered_service s ON ags.serviceId = s.id where ags.username = ?",
                new Object[]{username},
                String.class
        );
    }

}

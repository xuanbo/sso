package tk.fishfish.cas.server.validation;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.validation.ServiceTicketValidationAuthorizerConfigurer;
import org.apereo.cas.validation.ServiceTicketValidationAuthorizersExecutionPlan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 注册自定义账号服务Ticket认证
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Configuration("accountServiceTicketValidationAuthorizerConfigurer")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class AccountServiceTicketValidationAuthorizerConfigurer implements ServiceTicketValidationAuthorizerConfigurer {

    @Autowired
    @Qualifier("servicesManager")
    private ObjectProvider<ServicesManager> servicesManager;

    @Autowired
    @Qualifier("authJdbcDataSource")
    private DataSource authJdbcDataSource;

    @Override
    public void configureAuthorizersExecutionPlan(ServiceTicketValidationAuthorizersExecutionPlan plan) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(authJdbcDataSource);
        plan.registerAuthorizer(new AccountServiceTicketValidationAuthorizer(servicesManager.getObject(), jdbcTemplate));
    }

}

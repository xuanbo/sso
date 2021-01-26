package tk.fishfish.cas.server.action;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

/**
 * Actions配置
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Configuration("actionsConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class ActionsConfiguration {

    @Autowired
    @Qualifier("webApplicationServiceFactory")
    private ObjectProvider<ServiceFactory<?>> webApplicationServiceFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ObjectProvider<ServicesManager> servicesManager;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private ObjectProvider<CentralAuthenticationService> centralAuthenticationService;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    @RefreshScope
    public Action genericSuccessViewAction() {
        return new SuccessViewAction(centralAuthenticationService.getObject(),
                servicesManager.getObject(),
                webApplicationServiceFactory.getObject(),
                casProperties.getView().getDefaultRedirectUrl());
    }

}

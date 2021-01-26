package tk.fishfish.cas.server.webflow;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * 自定义登录Webflow
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
public class LoginWebflowConfigurer extends AbstractCasWebflowConfigurer {

    public LoginWebflowConfigurer(FlowBuilderServices flowBuilderServices,
                                  FlowDefinitionRegistry flowDefinitionRegistry,
                                  ApplicationContext applicationContext,
                                  CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, (ConfigurableApplicationContext) applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        final Flow flow = super.getLoginFlow();
        createViewState(flow, CasWebflowConstants.VIEW_ID_ACCOUNT_DISABLED, CasWebflowConstants.VIEW_ID_ACCOUNT_DISABLED);
        createViewState(flow, CasWebflowConstants.VIEW_ID_ACCOUNT_LOCKED, CasWebflowConstants.VIEW_ID_ACCOUNT_LOCKED);
        createViewState(flow, CasWebflowConstants.VIEW_ID_MUST_CHANGE_PASSWORD, CasWebflowConstants.VIEW_ID_MUST_CHANGE_PASSWORD);
        createViewState(flow, CasWebflowConstants.VIEW_ID_EXPIRED_PASSWORD, CasWebflowConstants.VIEW_ID_EXPIRED_PASSWORD);
        createViewState(flow, CasWebflowConstants.VIEW_ID_INVALID_WORKSTATION, CasWebflowConstants.VIEW_ID_INVALID_WORKSTATION);
        createViewState(flow, CasWebflowConstants.VIEW_ID_INVALID_AUTHENTICATION_HOURS, CasWebflowConstants.VIEW_ID_INVALID_AUTHENTICATION_HOURS);
        createViewState(flow, CasWebflowConstants.VIEW_ID_AUTHENTICATION_BLOCKED, CasWebflowConstants.VIEW_ID_AUTHENTICATION_BLOCKED);
    }

}

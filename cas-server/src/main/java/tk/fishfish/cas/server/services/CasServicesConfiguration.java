package tk.fishfish.cas.server.services;

import org.apereo.cas.authentication.principal.ServiceMatchingStrategy;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务配置
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Configuration("casServicesConfiguration")
@AutoConfigureBefore(CasCoreConfiguration.class)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasServicesConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "serviceMatchingStrategy")
    public ServiceMatchingStrategy serviceMatchingStrategy() {
        return new StartWithServiceMatchingStrategy();
    }

}

package tk.fishfish.cas.client.config;

import org.jasig.cas.client.boot.configuration.CasClientConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Configuration
public class CasConfiguration implements CasClientConfigurer {

    @Value("${cas.server-login-url}")
    private String serverLoginUrl;

    @Override
    @SuppressWarnings("unchecked")
    public void configureValidationFilter(FilterRegistrationBean validationFilter) {
        // 让 validationFilter 抛出异常供 casExceptionHandlerFilter 捕获跳转
        validationFilter.getInitParameters().put("exceptionOnValidationFailure", "true");
    }

    @Bean
    public FilterRegistrationBean<CasExceptionHandlerFilter> casExceptionHandlerFilter() {
        FilterRegistrationBean<CasExceptionHandlerFilter> casExceptionHandlerFilter = new FilterRegistrationBean<>();
        casExceptionHandlerFilter.setFilter(new CasExceptionHandlerFilter(serverLoginUrl));
        casExceptionHandlerFilter.setName("casExceptionHandlerFilter");
        casExceptionHandlerFilter.addUrlPatterns("/*");
        casExceptionHandlerFilter.setOrder(-5);
        return casExceptionHandlerFilter;
    }

}

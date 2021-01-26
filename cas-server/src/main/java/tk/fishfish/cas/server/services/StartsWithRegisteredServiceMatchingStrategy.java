package tk.fishfish.cas.server.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceMatchingStrategy;

/**
 * 前缀匹配
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class StartsWithRegisteredServiceMatchingStrategy implements RegisteredServiceMatchingStrategy {

    @Override
    public boolean matches(RegisteredService registeredService, String serviceId) {
        return StringUtils.startsWith(serviceId, registeredService.getServiceId());
    }

}

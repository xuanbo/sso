package tk.fishfish.cas.server.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceMatchingStrategy;
import org.apereo.cas.util.LoggingUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 前缀匹配
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Slf4j
public class StartWithServiceMatchingStrategy implements ServiceMatchingStrategy {

    @Override
    public boolean matches(Service service, Service serviceToMatch) {
        try {
            val thisUrl = URLDecoder.decode(service.getId(), StandardCharsets.UTF_8.name());
            val serviceUrl = URLDecoder.decode(serviceToMatch.getId(), StandardCharsets.UTF_8.name());
            log.debug("Decoded urls and comparing [{}] with [{}]", thisUrl, serviceUrl);
            return StringUtils.startsWithIgnoreCase(serviceUrl, thisUrl);
        } catch (final Exception e) {
            LoggingUtils.error(log, e);
        }
        return false;
    }

}

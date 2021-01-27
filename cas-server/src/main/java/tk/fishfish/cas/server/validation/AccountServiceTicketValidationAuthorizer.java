package tk.fishfish.cas.server.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.validation.Assertion;
import org.apereo.cas.validation.ServiceTicketValidationAuthorizer;

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

    @Override
    public void authorize(HttpServletRequest request, Service service, Assertion assertion) {
        RegisteredService registeredService = servicesManager.findServiceBy(service);
        if (registeredService == null) {
            log.warn("Service is not found in service registry");
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, "Service is not found in service registry.");
        }
        String username = assertion.getPrimaryAuthentication().getPrincipal().getId();
        List<Object> serviceIds = assertion.getPrimaryAuthentication().getPrincipal().getAttributes().get("serviceIds");
        log.debug("检测账号 {}-[{}] 是否授权服务: {}", username, ArrayUtils.toString(serviceIds), service.getId());
        for (Object serviceId : serviceIds) {
            if ("*".equals(serviceId.toString())) {
                return;
            }
            if (registeredService.matches(serviceId.toString())) {
                return;
            }
        }
        log.warn("Service [{}] is not grant to account: {}", service.getId(), username);
        throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, "Service [" + service.getId() + "] is not grant to account: " + username);
    }

}

package tk.fishfish.cas.server.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.services.RegisteredServiceAccessStrategyUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Action that should execute prior to rendering the generic-success login view.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class SuccessViewAction extends AbstractAction {

    private final CentralAuthenticationService centralAuthenticationService;

    private final ServicesManager servicesManager;

    private final ServiceFactory<?> serviceFactory;

    private final String redirectUrl;

    /**
     * Gets authentication principal.
     *
     * @param ticketGrantingTicketId the ticket granting ticket id
     * @return the authentication principal, or {@link org.apereo.cas.authentication.principal.NullPrincipal}
     * if none was available.
     */
    public Optional<Authentication> getAuthentication(final String ticketGrantingTicketId) {
        try {
            val ticketGrantingTicket = centralAuthenticationService.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
            return Optional.of(ticketGrantingTicket.getAuthentication());
        } catch (final InvalidTicketException e) {
            logger.debug(e.getMessage(), e);
        }
        logger.warn("In the absence of valid ticket-granting ticket, the authentication cannot be determined");
        return Optional.empty();
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        if (StringUtils.isNotBlank(this.redirectUrl)) {
            val service = this.serviceFactory.createService(this.redirectUrl);
            val registeredService = this.servicesManager.findServiceBy(service);
            RegisteredServiceAccessStrategyUtils.ensureServiceAccessIsAllowed(service, registeredService);
            requestContext.getExternalContext().requestExternalRedirect(service.getId());
        } else {
            val tgt = WebUtils.getTicketGrantingTicketId(requestContext);
            getAuthentication(tgt).ifPresent(authn -> {
                WebUtils.putAuthentication(authn, requestContext);
                List<Object> serviceIds = authn.getPrincipal().getAttributes().get("serviceIds");
                val service = WebUtils.getService(requestContext);
                val authorizedServices = servicesManager.getAllServices()
                        .stream()
                        .filter(registeredService -> {
                            try {
                                return RegisteredServiceAccessStrategyUtils.ensurePrincipalAccessIsAllowedForService(service, registeredService, authn);
                            } catch (final Exception e) {
                                logger.error(e.getMessage(), e);
                                return false;
                            }
                        })
                        // 过滤授权的服务
                        .filter(registeredService -> {
                            if (serviceIds == null) {
                                return true;
                            }
                            for (Object serviceId : serviceIds) {
                                String serviceUrl = serviceId.toString();
                                if ("*".equals(serviceUrl)) {
                                    return true;
                                }
                                if (registeredService.matches(serviceUrl)) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                // 再次过滤授权的服务
                if (log.isDebugEnabled()) {
                    log.debug("authorizedServices: {}", authorizedServices);
                }
                requestContext.getFlowScope().put("authorizedServices", authorizedServices);
            });
        }
        return success();
    }

}

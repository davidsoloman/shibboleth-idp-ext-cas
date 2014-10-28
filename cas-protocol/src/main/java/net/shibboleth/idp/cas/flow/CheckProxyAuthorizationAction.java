/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import net.shibboleth.idp.cas.protocol.TicketValidationResponse;
import net.shibboleth.idp.cas.service.ServiceContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Checks the current {@link ServiceContext} to determine whether the service is authorized to proxy.
 * Raises {@link Events#Failure failure}</li> if not authorized.
 *
 * @author Marvin S. Addison
 */
public class CheckProxyAuthorizationAction
    extends AbstractProfileAction<TicketValidationRequest, TicketValidationResponse> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(CheckProxyAuthorizationAction.class);

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) {

        final ServiceContext serviceContext = profileRequestContext.getSubcontext(ServiceContext.class);
        if (serviceContext == null) {
            log.info("ServiceContext not found in profile request context.");
            return ProtocolError.IllegalState.event(this);
        }
        if (!serviceContext.getService().isAuthorizedToProxy()) {
            log.info("{} is not authorized to proxy", serviceContext.getService().getName());
            return Events.Failure.event(this);
        }
        return ActionSupport.buildProceedEvent(this);
    }
}

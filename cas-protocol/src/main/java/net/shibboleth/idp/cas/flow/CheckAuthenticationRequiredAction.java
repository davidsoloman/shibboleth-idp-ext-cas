/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.context.SessionContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Determines whether authentication is required by examining both SSO session state and CAS
 * service ticket request message. Returns one of the following events:
 *
 * <ul>
 *     <li>{@link Events#GatewayRequested gatewayRequested} - Authentication not required since no ticket is requested.</li>
 *     <li>{@link Events#RenewRequested renewRequested} - Authentication required regardless of existing session.</li>
 *     <li>{@link Events#SessionFound sessionFound} - Authentication not required since session already exists.</li>
 *     <li>{@link Events#SessionNotFound sessionNotFound} - Authentication required since no active session exists.</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class CheckAuthenticationRequiredAction extends AbstractProfileAction<ServiceTicketRequest, Object> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(CheckAuthenticationRequiredAction.class);

    /** {@inheritDoc} */
    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext<ServiceTicketRequest, Object> profileRequestContext) {

        final ServiceTicketRequest request = FlowStateSupport.getServiceTicketRequest(springRequestContext);

        // Per http://www.jasig.org/cas/protocol section 2.1.1
        // It is RECOMMENDED that renew supersede gateway
        if (request.isRenew()) {
            return new Event(this, Events.RenewRequested.id());
        }

        if (request.isGateway()) {
            return new Event(this, Events.GatewayRequested.id());
        }

        final SessionContext sessionCtx = profileRequestContext.getSubcontext(SessionContext.class, false);
        Events result;
        if (sessionCtx != null) {
            final IdPSession session = sessionCtx.getIdPSession();
            if (session != null) {
                log.debug("Found session ID {}", session.getId());
                try {
                    // Timeout check updates session lastActivityInstant field
                    if (session.checkTimeout()) {
                        result = Events.SessionFound;
                    } else {
                        result = Events.SessionNotFound;
                    }
                } catch (SessionException e) {
                    log.debug("Error performing session timeout check. Assuming session has expired.", e);
                    result = Events.SessionNotFound;
                }
            } else {
                log.debug("Session not found.");
                result = Events.SessionNotFound;

            }
        } else {
            log.debug("Session context not found.");
            result = Events.SessionNotFound;
        }
        return result.event(this);
    }
}

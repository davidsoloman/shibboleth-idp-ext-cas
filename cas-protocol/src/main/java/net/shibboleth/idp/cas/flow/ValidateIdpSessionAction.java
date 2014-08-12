package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.profile.ActionSupport;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.SessionResolver;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.idp.session.criterion.SessionIdCriterion;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;

/**
 * IdP session validation for back-channel ticket request and validation. Possible outcomes:
 * <ul>
 *     <li>{@link ProtocolError#SessionExpired sessionExpired}</li>
 *     <li>{@link ProtocolError#SessionRetrievalError sessionRetrievalError}</li>
 *     <li>{@link Events#Success success}</li>
 * </ul>
 * <p>
 * Requires a {@link net.shibboleth.idp.cas.ticket.TicketContext} bound to the
 * {@link org.opensaml.profile.context.ProfileRequestContext} that is provided to the action.
 * <p>
 * On success, adds the current {@link net.shibboleth.idp.session.IdPSession}
 * as request scope parameter under the key {@value FlowStateSupport#IDP_SESSION_KEY} and also to a
 * {@link net.shibboleth.idp.session.context.SessionContext} that is a subcontext of the input
 * {@link org.opensaml.profile.context.ProfileRequestContext}.
 *
 * @author Marvin S. Addison
 */
public class ValidateIdpSessionAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ValidateIdpSessionAction.class);

    /** Looks up IdP sessions. */
    @Nonnull private SessionResolver sessionResolver;

    public void setSessionResolver(@Nonnull final SessionResolver resolver) {
        this.sessionResolver = Constraint.isNotNull(resolver, "Session resolver cannot be null.");
    }

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) {

        final TicketContext ticketContext = profileRequestContext.getSubcontext(TicketContext.class);
        if (ticketContext == null) {
            log.info("TicketContext not found in context tree.");
            return ActionSupport.buildEvent(this, EventIds.INVALID_PROFILE_CTX);
        }
        final String sessionId = ticketContext.getTicket().getSessionId();
        final IdPSession session;
        try {
            log.debug("Attempting to retrieve session {}", sessionId);
            session = sessionResolver.resolveSingle(new CriteriaSet(new SessionIdCriterion(sessionId)));
        } catch (ResolverException e) {
            log.debug("IdP session retrieval failed with error: {}", e);
            return ProtocolError.SessionRetrievalError.event(this);
        }
        boolean expired = (session == null);
        if (session != null) {
            try {
                expired = !session.checkTimeout();
                log.debug("Session {} expired={}", sessionId, expired);
            } catch (SessionException e) {
                log.debug("Error performing session timeout check. Assuming session has expired.", e);
                expired = true;
            }
        }
        if (expired) {
            return ProtocolError.SessionExpired.event(this);
        }
        FlowStateSupport.setIdpSession(springRequestContext, session);
        final SessionContext sessionContext = new SessionContext();
        sessionContext.setIdPSession(session);
        profileRequestContext.addSubcontext(sessionContext);
        return Events.Success.event(this);
    }
}

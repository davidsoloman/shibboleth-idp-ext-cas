/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import javax.annotation.Nonnull;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.cas.protocol.ProtocolError;
import net.shibboleth.idp.cas.session.CASSPSession;
import net.shibboleth.idp.cas.ticket.TicketContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.IdPSession;
import net.shibboleth.idp.session.SPSession;
import net.shibboleth.idp.session.SessionException;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Updates the {@link IdPSession} with a {@link CASSPSession} that describes the service granted access to and the
 * ticket that was successfully validated to grant access. Requires the following to be available under the
 * {@link org.opensaml.profile.context.ProfileRequestContext}:
 * <ul>
 *     <li>{@link SessionContext}</li>
 *     <li>{@link TicketContext}</li>
 * </ul>
 *
 * @author Marvin S. Addison
 */
public class UpdateIdPSessionWithSPSessionAction extends AbstractProfileAction {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(UpdateIdPSessionWithSPSessionAction.class);


    /** Lifetime of sessions to create. */
    @Positive @Duration private final long sessionLifetime;


    /**
     * Creates a new instance with given parameters.
     *
     * @param lifetime lifetime in milliseconds, determines upper bound for expiration of the
     * {@link CASSPSession} to be created
     */
    public UpdateIdPSessionWithSPSessionAction(@Positive @Duration final long lifetime) {
        sessionLifetime = Constraint.isGreaterThan(0, lifetime, "Lifetime must be greater than 0");
    }

    @Nonnull
    @Override
    protected Event doExecute(
            final @Nonnull RequestContext springRequestContext,
            final @Nonnull ProfileRequestContext profileRequestContext) {

        final TicketContext ticketContext = profileRequestContext.getSubcontext(TicketContext.class);
        if (ticketContext == null) {
            log.debug("Cannot create CAS SP session since no TicketContext found.");
            return ProtocolError.IllegalState.event(this);
        }
        final SessionContext sessionContext = profileRequestContext.getSubcontext(SessionContext.class);
        if (sessionContext == null) {
            log.debug("Cannot create CAS SP session since no SessionContext found.");
            return ProtocolError.IllegalState.event(this);
        }

        final long now = System.currentTimeMillis();
        final SPSession sps = new CASSPSession(
                ticketContext.getTicket().getService(),
                now,
                now + sessionLifetime,
                ticketContext.getTicket().getId());
        log.debug("Created SP session {}", sps);
        try {
            sessionContext.getIdPSession().addSPSession(sps);
        } catch (SessionException e) {
            log.warn("Failed updating IdP session with CAS SP session: {}", e.getMessage());
            return ProtocolError.IllegalState.event(this);
        }
        return Events.Success.event(this);
    }
}

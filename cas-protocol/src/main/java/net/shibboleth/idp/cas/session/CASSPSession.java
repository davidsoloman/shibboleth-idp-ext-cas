/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.session;

import javax.annotation.Nonnull;

import net.shibboleth.idp.session.BasicSPSession;
import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Describes a CAS protocol-specific service provider session created in response to a successful ticket validation.
 *
 * @author Marvin S. Addison
 */
public class CASSPSession extends BasicSPSession {

    /** Validated ticket that started the SP session. */
    @Nonnull @NotEmpty private final String ticketId;


    /**
     * Creates a new CAS SP session.
     *
     * @param id         the identifier of the service associated with this session
     * @param flowId     authentication flow used to authenticate the principal to this service
     * @param creation   creation time of session, in milliseconds since the epoch
     * @param expiration expiration time of session, in milliseconds since the epoch
     * @param ticketId   ticket ID used to gain access to the service
     */
    public CASSPSession(
            @Nonnull @NotEmpty String id,
            @Nonnull @NotEmpty String flowId,
            @Duration @Positive long creation,
            @Duration @Positive long expiration,
            @Nonnull @NotEmpty String ticketId) {
        super(id, flowId, creation, expiration);
        this.ticketId = Constraint.isNotNull(StringSupport.trimOrNull(ticketId), "Ticket ID cannot be null or empty");
    }

    @Nonnull @NotEmpty public String getTicketId() {
        return ticketId;
    }

    @Override
    public String toString() {
        return "CASSPSession: " + getId() + " via " + ticketId;
    }
}

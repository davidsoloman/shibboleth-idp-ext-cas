/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import org.joda.time.Instant;

/**
 * Generic CAS ticket that has a natural identifier and expiration. All CAS tickets are bound to an IdP session ID
 * that indicates the IdP session in which they were created.
 *
 * @author Marvin S. Addison
 */
public class Ticket {

    /** IdP session ID used to create ticket. */
    @Nonnull private final String sessionId;

    /** Ticket identifier. */
    @Nonnull private String id;

    /** Service/relying party that requested the ticket. */
    @Nonnull private String service;

    /** Expiration instant. */
    @Nonnull private Instant expirationInstant;

    /**
     * Creates a new ticket with the given parameters..
     *
     * @param id Ticket ID.
     * @param sessionId IdP session ID used to create ticket.
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     */
    public Ticket(
            @Nonnull final String id,
            @Nonnull final String sessionId,
            @Nonnull final String service,
            @Nonnull final Instant expiration) {
        this.id = Constraint.isNotNull(id, "Id cannot be null");
        this.sessionId = Constraint.isNotNull(sessionId, "SessionId cannot be null");
        this.service = Constraint.isNotNull(service, "Service cannot be null");
        this.expirationInstant = Constraint.isNotNull(expiration, "Expiration cannot be null");
    }

    @Nonnull public String getId() {
        return id;
    }

    @Nonnull public String getSessionId() {
        return sessionId;
    }

    @Nonnull public String getService() {
        return service;
    }

    @Nonnull public Instant getExpirationInstant() {
        return expirationInstant;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }
        final Ticket other = (Ticket) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return 23 + 31 * id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}

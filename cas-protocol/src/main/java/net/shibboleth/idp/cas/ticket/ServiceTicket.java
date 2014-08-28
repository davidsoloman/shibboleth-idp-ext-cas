/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;

import org.joda.time.Instant;

/**
 * CAS service ticket.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicket extends Ticket {

    /** Forced authentication flag. */
    private final boolean renew;

    /**
     * Creates a new authenticated ticket with an identifier, service, and expiration date.
     *
     * @param id Ticket ID.
     * @param sessionId IdP session ID used to create ticket.
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     * @param renew True if ticket was issued from forced authentication, false otherwise.
     */
    public ServiceTicket(
            @Nonnull final String id,
            @Nonnull final String sessionId,
            @Nonnull final String service,
            @Nonnull final Instant expiration,
            final boolean renew) {
        super(id, sessionId, service, expiration);
        this.renew = renew;
    }


    public boolean isRenew() {
        return renew;
    }
}

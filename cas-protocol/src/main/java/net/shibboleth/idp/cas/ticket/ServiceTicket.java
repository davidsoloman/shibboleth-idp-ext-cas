package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;

import org.joda.time.Instant;

/**
 * A service access ticket that contains metadata indicating whether it was granted in response to a forced
 * authentication request, i.e. <code>renew=true</code>.
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
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     */
    public ServiceTicket(
            @Nonnull final String id, @Nonnull final String service, @Nonnull final Instant expiration,
            final boolean isRenewed) {
        super(id, service, expiration);
        this.renew = isRenewed;
    }

    public boolean isRenew() {
        return renew;
    }
}

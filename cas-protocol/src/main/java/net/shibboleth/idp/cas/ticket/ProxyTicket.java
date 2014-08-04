package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import org.joda.time.Instant;

/**
 * CAS proxy ticket.
 *
 * @author Marvin S. Addison
 */
public class ProxyTicket extends Ticket {

    /** Proxy-granting ticket used to create ticket. */
    @Nonnull private final String pgtId;

    /**
     * Creates a new authenticated ticket with an identifier, service, and expiration date.
     *
     * @param id Ticket ID.
     * @param sessionId IdP session ID used to create ticket.
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     * @param pgtId Proxy-granting ticket ID used to create ticket.
     */
    public ProxyTicket(
            @Nonnull final String id,
            @Nonnull final String sessionId,
            @Nonnull final String service,
            @Nonnull final Instant expiration,
            @Nonnull final String pgtId) {
        super(id, sessionId, service, expiration);
        this.pgtId = Constraint.isNotNull(pgtId, "PgtId cannot be null");
    }

    @Nonnull public String getPgtId() {
        return pgtId;
    }
}

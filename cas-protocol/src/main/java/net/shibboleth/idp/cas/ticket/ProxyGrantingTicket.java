/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import org.joda.time.Instant;

/**
 * CAS proxy-granting ticket.
 *
 * @author Marvin S. Addison
 */
public class ProxyGrantingTicket extends Ticket {

    /** The ID of the parent proxy-granting ticket. */
    @Nullable String parentId;

    /**
     * Creates a proxy-granting ticket with the given values.
     *
     * @param id Ticket ID.
     * @param sessionId IdP session ID used to create ticket.
     * @param service Service that requested the ticket.
     * @param expiration Expiration instant.
     * @param parentId ID of parent proxy-granting ticket or null if this is first proxy in chain.
     */
    public ProxyGrantingTicket(
            @Nonnull final String id,
            @Nonnull final String sessionId,
            @Nonnull final String service,
            @Nonnull final Instant expiration,
            @Nullable final String parentId) {
        super(id, sessionId, service, expiration);
        this.parentId = StringSupport.trimOrNull(parentId);
    }

    @Nullable public String getParentId() {
        return parentId;
    }

    /**
     * Determines whether this proxy-granting ticket is the root of a proxy chain.
     *
     * @return True if this proxy-granting ticket has no parent, false otherwise.
     */
    public boolean isRoot() {
        return getParentId() == null;
    }
}

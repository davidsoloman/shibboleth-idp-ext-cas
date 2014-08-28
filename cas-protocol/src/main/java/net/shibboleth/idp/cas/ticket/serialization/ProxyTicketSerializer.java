/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket.serialization;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.ticket.ProxyTicket;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import org.joda.time.Instant;

/**
 * Proxy ticket storage serializer.
 *
 * @author Marvin S. Addison
 */
public class ProxyTicketSerializer extends AbstractTicketSerializer<ProxyTicket> {
    @Override
    @NotEmpty
    protected String[] extractFields(@Nonnull final ProxyTicket ticket) {
        return new String[] {
                ticket.getSessionId(),
                ticket.getService(),
                String.valueOf(ticket.getExpirationInstant().getMillis()),
                ticket.getPgtId(),
        };
    }

    @Override
    @Nonnull
    protected ProxyTicket createTicket(@Nonnull final String id, @NotEmpty final String[] fields) {
        if (fields.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields but got " + fields.length);
        }
        return new ProxyTicket(
                id,
                fields[0],
                fields[1],
                new Instant(Long.valueOf(fields[2])),
                fields[3]);
    }
}

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
                String.valueOf(ticket.isRenew()),
                ticket.getPgtId(),
        };
    }

    @Override
    @Nonnull
    protected ProxyTicket createTicket(@Nonnull final String id, @NotEmpty final String[] fields) {
        if (fields.length != 5) {
            throw new IllegalArgumentException("Expected 5 fields but got " + fields.length);
        }
        return new ProxyTicket(
                id,
                fields[0],
                fields[1],
                new Instant(Long.valueOf(fields[2])),
                Boolean.parseBoolean(fields[3]),
                fields[4]);
    }
}

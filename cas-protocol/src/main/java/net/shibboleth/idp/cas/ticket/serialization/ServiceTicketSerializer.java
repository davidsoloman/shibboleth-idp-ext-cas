package net.shibboleth.idp.cas.ticket.serialization;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.ticket.ServiceTicket;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import org.joda.time.Instant;

/**
 * Serializes service tickets in simple field-delimited form.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicketSerializer extends AbstractTicketSerializer<ServiceTicket> {
    @Override
    @NotEmpty
    protected String[] extractFields(@Nonnull final ServiceTicket ticket) {
        return new String[] {
                ticket.getSessionId(),
                ticket.getService(),
                String.valueOf(ticket.getExpirationInstant().getMillis()),
                String.valueOf(ticket.isRenew()),
        };
    }

    @Override
    @Nonnull
    protected ServiceTicket createTicket(@Nonnull final String id, @NotEmpty final String[] fields) {
        if (fields.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields but got " + fields.length);
        }
        return new ServiceTicket(
                id, fields[0], fields[1], new Instant(Long.valueOf(fields[2])), Boolean.parseBoolean(fields[3]));
    }
}

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket.serialization;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.shibboleth.idp.cas.ticket.ProxyGrantingTicket;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import org.joda.time.Instant;

/**
 * Serializes proxy-granting tickets in simple field-delimited form.
 *
 * @author Marvin S. Addison
 */
public class ProxyGrantingTicketSerializer extends AbstractTicketSerializer<ProxyGrantingTicket> {
    @Override
    @NotEmpty
    protected String[] extractFields(@Nonnull final ProxyGrantingTicket ticket) {
        final ArrayList<String> fields = new ArrayList<String>(4);
        fields.add(ticket.getSessionId());
        fields.add(ticket.getService());
        fields.add(String.valueOf(ticket.getExpirationInstant().getMillis()));
        if (ticket.getParentId() != null) {
            fields.add(ticket.getParentId());
        }
        return fields.toArray(new String[fields.size()]);
    }

    @Override
    @Nonnull
    protected ProxyGrantingTicket createTicket(@Nonnull final String id, @NotEmpty final String[] fields) {
        if (fields.length < 3) {
            throw new IllegalArgumentException("Expected at least 3 fields but got " + fields.length);
        }
        return new ProxyGrantingTicket(
                id,
                fields[0],
                fields[1],
                new Instant(Long.valueOf(fields[2])),
                fields.length > 3 ? fields[3] : null);
    }
}

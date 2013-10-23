package net.shibboleth.idp.cas.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageSerializer;
import org.opensaml.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple ticket management service that generates tickets using a {@link TicketIdGenerator} component and stores
 * tickets in a {@link org.opensaml.storage.StorageService}.
 *
 * @author Marvin S. Addison
 */
public class SimpleTicketService implements TicketService {

    private static final String SERVICE_TICKET_CONTEXT = "http://jasig.org/cas/tickets/ServiceTicket";

    /** Map of ticket classes to context names. */
    private static final Map<Class<? extends Ticket>, String> CONTEXT_CLASS_MAP = new HashMap<>();

    private static final StorageSerializer<ServiceTicket> SERVICE_TICKET_SERIALIZER = new ServiceTicketSerializer();

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SimpleTicketService.class);

    /** Storage service to which ticket persistence operations are delegated. */
    @Nonnull
    private StorageService storageService;

    /** Creates identifiers for tickets created with this factory. */
    @Nonnull
    private TicketIdGenerator ticketIdGenerator;

    /** Validity time period of tickets created with this factory. */
    @Duration
    @Positive
    private long ticketValidityPeriod;


    static {
        CONTEXT_CLASS_MAP.put(ServiceTicket.class, SERVICE_TICKET_CONTEXT);
    }


    public void setTicketIdGenerator(@Nonnull final TicketIdGenerator generator) {
        this.ticketIdGenerator = Constraint.isNotNull(generator, "Ticket generator cannot be null.");
    }

    public void setTicketValidityPeriod(@Duration @Positive final long millis) {
        this.ticketValidityPeriod = Constraint.isGreaterThan(0, millis, "Ticket validity period must be greater than 0.");
    }

    public void setStorageService(@Nonnull StorageService storageService) {
        this.storageService = Constraint.isNotNull(storageService, "Storage service cannot be null.");
    }

    @Override
    public ServiceTicket createServiceTicket(final String service, final boolean renew) {
        final ServiceTicket ticket = new ServiceTicket(
                ticketIdGenerator.generate(),
                service,
                DateTime.now().plus(ticketValidityPeriod).toInstant(),
                renew);
        log.debug("Generated ticket {}", ticket);
        storeTicket(ticket, SERVICE_TICKET_SERIALIZER);
        return ticket;
    }


    @Override
    public ServiceTicket removeServiceTicket(final String ticketId) {
        log.debug("Attempting to fetch service ticket " + ticketId);
        final ServiceTicket ticket;
        try {
            final StorageRecord<ServiceTicket> record = storageService.read(SERVICE_TICKET_CONTEXT, ticketId);
            if (record == null) {
                throw new RuntimeException("Service ticket not found: " + ticketId);
            }
            ticket = record.getValue(SERVICE_TICKET_SERIALIZER, SERVICE_TICKET_CONTEXT, ticketId);
        } catch (IOException e) {
            throw new RuntimeException("Error fetching ticket.");
        }
        try {
            log.debug("Attempting to delete " + ticket);
            if (this.storageService.delete(SERVICE_TICKET_CONTEXT, ticketId)) {
                log.debug("Deleted ticket {}", ticketId);
            } else {
                throw new RuntimeException("Failed deleting " + ticket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting ticket " + ticketId, e);
        }
        return ticket;
    }

    private void storeTicket(final Ticket ticket, final StorageSerializer<? extends Ticket> serializer) {
        try {
            if (!storageService.create(
                    CONTEXT_CLASS_MAP.get(ticket.getClass()),
                    ticket.getId(),
                    ticket,
                    serializer,
                    ticket.getExpirationInstant().getMillis())) {
                throw new RuntimeException("Failed to store ticket " + ticket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store ticket " + ticket, e);
        }
    }

    private static class ServiceTicketSerializer implements StorageSerializer<ServiceTicket> {

        private static final char DELIMITER = ':';

        @Nonnull
        @Override
        public String serialize(@Nonnull final ServiceTicket instance) throws IOException {
            return instance.getService() + DELIMITER + instance.isRenew();
        }

        @Nonnull
        @Override
        public ServiceTicket deserialize(
                final int version,
                @Nonnull @NotEmpty final String context,
                @Nonnull @NotEmpty final String key,
                @Nonnull @NotEmpty final String value,
                @Nullable final Long expiration) throws IOException {
            final int i = value.lastIndexOf(DELIMITER);
            if (i < 0 || i == value.length() - 1) {
                throw new RuntimeException("Error deserializing service ticket value " + value);
            }
            final String service = value.substring(0, i);
            final String renew = value.substring(i + 1);
            return new ServiceTicket(key, service, new Instant(expiration), Boolean.parseBoolean(renew));
        }
    }
}

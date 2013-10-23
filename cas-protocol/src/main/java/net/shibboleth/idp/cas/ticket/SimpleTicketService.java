package net.shibboleth.idp.cas.ticket;

import java.io.IOException;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.joda.time.DateTime;
import org.joda.time.Instant;
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
    public Ticket createTicket(final String service) {
        final Ticket ticket = new Ticket(
                ticketIdGenerator.generate(),
                service,
                DateTime.now().plus(ticketValidityPeriod).toInstant());
        log.debug("Generated ticket {}", ticket);
        try {
            if (!this.storageService.create(ticket)) {
                throw new RuntimeException("Failed to store ticket " + ticket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store ticket " + ticket, e);
        }
        return ticket;
    }

    @Override
    public Ticket removeTicket(final String ticketId) {
        log.debug("Attempting to fetch ticket " + ticketId);
        final Ticket ticket;
        try {
            ticket = (Ticket) this.storageService.read(new Ticket(ticketId, "unknown", new Instant()));
        } catch (IOException e) {
            throw new RuntimeException("Error fetching ticket.");
        }
        try {
            log.debug("Attempting to delete " + ticket);
            if (this.storageService.delete(ticket)) {
                log.debug("Deleted ticket {}", ticketId);
            } else {
                throw new RuntimeException("Failed deleting " + ticket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting ticket " + ticketId, e);
        }
        return ticket;
    }
}

package net.shibboleth.idp.cas.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.idp.cas.ticket.serialization.ProxyGrantingTicketSerializer;
import net.shibboleth.idp.cas.ticket.serialization.ProxyTicketSerializer;
import net.shibboleth.idp.cas.ticket.serialization.ServiceTicketSerializer;
import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.joda.time.DateTime;
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

    private static final String ST_CONTEXT = "http://jasig.org/cas/tickets/ST";

    private static final String PT_CONTEXT = "http://jasig.org/cas/tickets/PT";

    private static final String PGT_CONTEXT = "http://jasig.org/cas/tickets/PGT";

    /** Map of ticket classes to context names. */
    private static final Map<Class<? extends Ticket>, String> CONTEXT_CLASS_MAP = new HashMap<>();

    /** Map of ticket classes to serializers. */
    private static final Map<Class<? extends Ticket>, StorageSerializer<? extends Ticket>> SERIALIZER_MAP = new HashMap<>();

    private static final ServiceTicketSerializer ST_SERIALIZER = new ServiceTicketSerializer();

    private static final ProxyTicketSerializer PT_SERIALIZER = new ProxyTicketSerializer();

    private static final ProxyGrantingTicketSerializer PGT_SERIALIZER = new ProxyGrantingTicketSerializer();

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SimpleTicketService.class);

    /** Storage service to which ticket persistence operations are delegated. */
    @Nonnull
    private StorageService storageService;

    /** Creates identifiers for service tickets. */
    @Nonnull
    private TicketIdGenerator serviceTicketIdGenerator;

    /** Creates identifiers for proxy tickets. */
    @Nonnull
    private TicketIdGenerator proxyTicketIdGenerator;

    /** Creates identifiers for proxy-granting tickets. */
    @Nonnull
    private TicketIdGenerator proxyGrantingTicketIdGenerator;

    /** Validity time period of service tickets created with this factory. */
    @Duration
    @Positive
    private long serviceTicketValidityPeriod;

    /** Validity time period of proxy-granting tickets created with this factory. */
    @Duration
    @Positive
    private long proxyGrantingTicketValidityPeriod;

    /** Validity time period of proxy tickets created with this factory. */
    @Duration
    @Positive
    private long proxyTicketValidityPeriod;


    static {
        CONTEXT_CLASS_MAP.put(ServiceTicket.class, ST_CONTEXT);
        CONTEXT_CLASS_MAP.put(ProxyTicket.class, PT_CONTEXT);
        CONTEXT_CLASS_MAP.put(ProxyGrantingTicket.class, PGT_CONTEXT);
        SERIALIZER_MAP.put(ServiceTicket.class, ST_SERIALIZER);
        SERIALIZER_MAP.put(ProxyTicket.class, PT_SERIALIZER);
        SERIALIZER_MAP.put(ProxyGrantingTicket.class, PGT_SERIALIZER);
    }

    public void setStorageService(@Nonnull final StorageService storageService) {
        this.storageService = Constraint.isNotNull(storageService, "Storage service cannot be null.");
    }

    public void setServiceTicketIdGenerator(@Nonnull final TicketIdGenerator generator) {
        this.serviceTicketIdGenerator = Constraint.isNotNull(generator, "TicketIdGenerator cannot be null.");
    }

    public void setProxyTicketIdGenerator(@Nonnull final TicketIdGenerator generator) {
        this.proxyTicketIdGenerator = Constraint.isNotNull(generator, "TicketIdGenerator cannot be null.");
    }

    public void setServiceTicketValidityPeriod(@Duration @Positive final long millis) {
        this.serviceTicketValidityPeriod = Constraint.isGreaterThan(
                0, millis, "Ticket validity period must be greater than 0.");
    }

    public void setProxyGrantingTicketValidityPeriod(@Duration @Positive final long millis) {
        this.proxyGrantingTicketValidityPeriod = Constraint.isGreaterThan(
                0, millis, "Ticket validity period must be greater than 0.");
    }

    public void setProxyTicketValidityPeriod(@Duration @Positive final long millis) {
        this.proxyTicketValidityPeriod = Constraint.isGreaterThan(
                0, millis, "Ticket validity period must be greater than 0.");
    }

    @Override
    @Nonnull
    public ServiceTicket createServiceTicket(
            @Nonnull final String sessionId,
            @Nonnull final String service,
            final boolean renew) {
        Constraint.isNotNull(sessionId, "Session ID cannot be null");
        Constraint.isNotNull(service, "Service cannot be null");
        final ServiceTicket st = new ServiceTicket(
                serviceTicketIdGenerator.generate(),
                sessionId,
                service,
                DateTime.now().plus(serviceTicketValidityPeriod).toInstant(),
                renew);
        log.debug("Generated ticket {}", st);
        store(st);
        return st;
    }

    @Override
    @Nullable
    public ServiceTicket removeServiceTicket(@Nonnull final String id) {
        Constraint.isNotNull(id, "Id cannot be null");
        return delete(id, ServiceTicket.class);
    }

    @Override
    @Nonnull
    public ProxyGrantingTicket createProxyGrantingTicket(
            @Nonnull final ServiceTicket serviceTicket, @Nonnull final String pgtId) {
        Constraint.isNotNull(serviceTicket, "ServiceTicket cannot be null");
        Constraint.isNotNull(pgtId, "PGT ID cannot be null");
        final ProxyGrantingTicket pgt = new ProxyGrantingTicket(
                pgtId,
                serviceTicket.getSessionId(),
                serviceTicket.getService(),
                DateTime.now().plus(proxyGrantingTicketValidityPeriod).toInstant(),
                null);
        log.debug("Generated ticket {}", pgt);
        store(pgt);
        return pgt;
    }

    @Nonnull
    @Override
    public ProxyGrantingTicket createProxyGrantingTicket(
            @Nonnull final ProxyTicket proxyTicket, @Nonnull final String pgtId) {
        Constraint.isNotNull(proxyTicket, "ProxyTicket cannot be null");
        Constraint.isNotNull(pgtId, "PGT ID cannot be null");
        final ProxyGrantingTicket pgt = new ProxyGrantingTicket(
                pgtId,
                proxyTicket.getSessionId(),
                proxyTicket.getService(),
                DateTime.now().plus(proxyGrantingTicketValidityPeriod).toInstant(),
                proxyTicket.getPgtId());
        log.debug("Generated ticket {}", pgt);
        store(pgt);
        return pgt;
    }

    @Override
    @Nullable
    public ProxyGrantingTicket fetchProxyGrantingTicket(@Nonnull final String id) {
        Constraint.isNotNull(id, "Id cannot be null");
        return read(id, ProxyGrantingTicket.class);
    }

    @Override
    @Nullable
    public ProxyGrantingTicket removeProxyGrantingTicket(@Nonnull final String id) {
        Constraint.isNotNull(id, "Id cannot be null");
        final ProxyGrantingTicket pgt = delete(id, ProxyGrantingTicket.class);
        return pgt;
    }

    @Nonnull
    @Override
    public ProxyTicket createProxyTicket(
            @Nonnull final ProxyGrantingTicket pgt, @Nonnull final String service, final boolean renew) {
        Constraint.isNotNull(pgt, "ProxyGrantingTicket cannot be null");
        Constraint.isNotNull(service, "Service cannot be null");
        final ProxyTicket pt = new ProxyTicket(
                proxyTicketIdGenerator.generate(),
                pgt.getSessionId(),
                service,
                DateTime.now().plus(proxyGrantingTicketValidityPeriod).toInstant(),
                renew,
                pgt.getId());
        store(pt);
        return pt;
    }

    @Nullable
    @Override
    public ProxyTicket removeProxyTicket(final @Nonnull String id) {
        return delete(id, ProxyTicket.class);
    }

    private <T extends Ticket> void store(final T ticket) {
        log.debug("Storing {}", ticket);
        try {
            if (!storageService.create(
                    context(ticket.getClass()),
                    ticket.getId(),
                    ticket,
                    serializer(ticket.getClass()),
                    ticket.getExpirationInstant().getMillis())) {
                throw new RuntimeException("Failed to store ticket " + ticket);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store ticket " + ticket, e);
        }
    }

    private <T extends Ticket> T delete(final String id, final Class<T> clazz) {
        log.debug("Deleting {}", id);
        final T ticket = read(id, clazz);
        if (ticket == null) {
            return null;
        }
        try {
            log.debug("Attempting to delete " + ticket);
            if (this.storageService.delete(context(clazz), id)) {
                log.debug("Deleted ticket {}", id);
            } else {
                log.info("Failed deleting {}. Ticket probably expired from storage facility.", id);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting ticket " + id, e);
        }
        return ticket;
    }

    private <T extends Ticket> T read(final String id, final Class<T> clazz) {
        log.debug("Reading {}", id);
        final T ticket;
        try {
            final String context = context(clazz);
            final StorageRecord<T> record = storageService.read(context, id);
            if (record == null) {
                log.debug("{} not found", id);
                return null;
            }
            ticket = record.getValue(serializer(clazz), context, id);
        } catch (IOException e) {
            throw new RuntimeException("Error reading ticket.");
        }
        return ticket;
    }

    private static String context(final Class<? extends Ticket> clazz) {
        return CONTEXT_CLASS_MAP.get(clazz);
    }

    private static <T extends Ticket> StorageSerializer<T> serializer(final Class<T> clazz) {
        return (StorageSerializer<T>) SERIALIZER_MAP.get(clazz);
    }
}

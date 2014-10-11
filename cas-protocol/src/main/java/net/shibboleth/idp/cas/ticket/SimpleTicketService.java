/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.idp.cas.config.ProxyGrantingTicketConfiguration;
import net.shibboleth.idp.cas.config.ProxyTicketConfiguration;
import net.shibboleth.idp.cas.config.ServiceTicketConfiguration;
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
    private final StorageService storageService;

    @Nonnull
    private final ServiceTicketConfiguration serviceTicketConfiguration;

    @Nonnull
    private final ProxyGrantingTicketConfiguration proxyGrantingTicketConfiguration;

    @Nonnull
    private final ProxyTicketConfiguration proxyTicketConfiguration;

    static {
        CONTEXT_CLASS_MAP.put(ServiceTicket.class, ServiceTicketConfiguration.PROFILE_ID);
        CONTEXT_CLASS_MAP.put(ProxyTicket.class, ProxyTicketConfiguration.PROFILE_ID);
        CONTEXT_CLASS_MAP.put(ProxyGrantingTicket.class, ProxyGrantingTicketConfiguration.PROFILE_ID);
        SERIALIZER_MAP.put(ServiceTicket.class, ST_SERIALIZER);
        SERIALIZER_MAP.put(ProxyTicket.class, PT_SERIALIZER);
        SERIALIZER_MAP.put(ProxyGrantingTicket.class, PGT_SERIALIZER);
    }

    public SimpleTicketService(
            @Nonnull final StorageService storageService,
            @Nonnull final ServiceTicketConfiguration serviceTicketConfiguration,
            @Nonnull final ProxyGrantingTicketConfiguration proxyGrantingTicketConfiguration,
            @Nonnull final ProxyTicketConfiguration proxyTicketConfiguration)
    {
        this.storageService = Constraint.isNotNull(storageService, "StorageService cannot be null.");
        this.serviceTicketConfiguration = Constraint.isNotNull(
                serviceTicketConfiguration, "ServiceTicketConfiguration cannot be null.");
        this.proxyGrantingTicketConfiguration = Constraint.isNotNull(
                proxyGrantingTicketConfiguration, "ProxyGrantingTicketConfiguration cannot be null.");
        this.proxyTicketConfiguration = Constraint.isNotNull(
                proxyTicketConfiguration, "ProxyTicketConfiguration cannot be null.");
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
                serviceTicketConfiguration.getSecurityConfiguration().getIdGenerator().generateIdentifier(),
                sessionId,
                service,
                DateTime.now().plus(serviceTicketConfiguration.getTicketValidityPeriod()).toInstant(),
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
                DateTime.now().plus(proxyGrantingTicketConfiguration.getTicketValidityPeriod()).toInstant(),
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
                DateTime.now().plus(proxyGrantingTicketConfiguration.getTicketValidityPeriod()).toInstant(),
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
            @Nonnull final ProxyGrantingTicket pgt, @Nonnull final String service) {
        Constraint.isNotNull(pgt, "ProxyGrantingTicket cannot be null");
        Constraint.isNotNull(service, "Service cannot be null");
        final ProxyTicket pt = new ProxyTicket(
                proxyTicketConfiguration.getSecurityConfiguration().getIdGenerator().generateIdentifier(),
                pgt.getSessionId(),
                service,
                DateTime.now().plus(proxyTicketConfiguration.getTicketValidityPeriod()).toInstant(),
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

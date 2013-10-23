package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * CAS protocol response message for a successfully granted service ticket.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicketResponse {
    /** Service URL */
    @Nonnull
    private final String service;

    /** Granted service ticket. */
    @Nonnull
    private final String ticket;


    /**
     * Creates a CAS service ticket response message for a service and granted ticket.
     *
     * @param service Service that requested ticket.
     * @param ticket Granted service ticket.
     */
    public ServiceTicketResponse(final String service, final String ticket) {
        this.service = service;
        this.ticket = ticket;
    }

    @Nonnull
    public String getService() {
        return service;
    }

    @Nonnull
    public String getTicket() {
        return ticket;
    }

    public String getRedirectUrl() {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(service);
        builder.queryParam(ProtocolParam.Ticket.id(), ticket);
        return builder.build().toUriString();
    }
}

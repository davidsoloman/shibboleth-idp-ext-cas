/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.protocol;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
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

    /** Flag indicating whether ticket request is via SAML 1.1 protocol. */
    private boolean saml;


    /**
     * Creates a CAS service ticket response message for a service and granted ticket.
     *
     * @param service Service that requested ticket.
     * @param ticket Granted service ticket.
     */
    public ServiceTicketResponse(@Nonnull final String service, @Nonnull final String ticket) {
        this.service = Constraint.isNotNull(service, "Service cannot be null");
        this.ticket = Constraint.isNotNull(ticket, "Ticket cannot be null");
    }

    @Nonnull public String getService() {
        return service;
    }

    @Nonnull public String getTicket() {
        return ticket;
    }

    public boolean isSaml() {
        return saml;
    }

    public void setSaml(final boolean saml) {
        this.saml = saml;
    }

    public String getRedirectUrl() {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(service);
        if (saml) {
            builder.queryParam(SamlParam.SAMLart.name(), ticket);
        } else {
            builder.queryParam(ProtocolParam.Ticket.id(), ticket);
        }
        return builder.build().toUriString();
    }
}

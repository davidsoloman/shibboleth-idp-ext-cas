/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.flow;

import org.springframework.webflow.execution.Event;

/**
 * CAS protocol flow event identifiers.
 *
 * @author Marvin S. Addison
 */
public enum Events {

    /** Active IdP session not found. */
    SessionNotFound,

    /** Active IdP session found. */
    SessionFound,

    /** CAS protocol renew flag specified to force authentication. */
    RenewRequested,

    /** CAS protocol gateway flag specified to skip authentication. */
    GatewayRequested,

    /** Successful service ticket validation. */
    ServiceTicketValidated,

    /** Successful proxy ticket validation. */
    ProxyTicketValidated,

    /** Generic success event. */
    Success,

    /** Generic failure event. */
    Failure,

    /** Generic proceed event. */
    Proceed;

    /**
     * Converts enumeration name to an identifier suitable for a Spring Webflow event identifier.
     *
     * @return Events enumeration name with first letter lower-cased.
     */
    public String id() {
        return this.name().substring(0, 1).toLowerCase() + this.name().substring(1);
    }


    /**
     * Creates a Spring webflow event whose ID is given by {@link #id()}.
     *
     * @param source Event source.
     *
     * @return Spring webflow event.
     */
    public Event event(final Object source) {
        return new Event(source, id());
    }
}

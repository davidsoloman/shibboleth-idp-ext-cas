package net.shibboleth.idp.cas.protocol;

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;

/**
 * CAS protocol errors.
 *
 * @author Marvin S. Addison
 */
public enum ProtocolError {
    /** Service parameter required but not specified. */
    ServiceNotSpecified("INVALID_REQUEST", "E_SERVICE_NOT_SPECIFIED"),

    /** Validating service does not match service to which ticket was issued. */
    ServiceMismatch("INVALID_SERVICE", "E_SERVICE_MISMATCH"),

    /** IdP session that issued ticket has expired which invalidates ticket. */
    SessionExpired("INVALID_TICKET", "E_SESSION_EXPIRED"),

    /** Error retrieving IdP session. */
    SessionRetrievalError("INVALID_TICKET", "E_SESSION_RETRIEVAL_ERROR"),

    /** Ticket parameter required but not specified. */
    TicketNotSpecified("INVALID_REQUEST", "E_TICKET_NOT_SPECIFIED"),

    /** Ticket not found or expired. */
    TicketExpired("INVALID_TICKET", "E_TICKET_EXPIRED"),

    /** Validation specifies renew protocol flag but ticket was not issued from a forced authentication. */
    TicketNotFromRenew("INVALID_TICKET", "E_TICKET_NOT_FROM_RENEW"),

    /** Error creating ticket. */
    TicketCreationError("INTERNAL_ERROR", "E_TICKET_CREATION_ERROR"),

    /** Error retrieving ticket. */
    TicketRetrievalError("INTERNAL_ERROR", "E_TICKET_RETRIEVAL_ERROR"),

    /** Error removing ticket. */
    TicketRemovalError("INTERNAL_ERROR", "E_TICKET_REMOVAL_ERROR");

    /** Error code. */
    private final String code;

    /** Error detail code. */
    private final String detailCode;

    ProtocolError(final String code, final String detailCode) {
        this.code = code;
        this.detailCode = detailCode;
    }

    /**
     * Converts enumeration name to an identifier suitable for a Spring Webflow event identifier.
     *
     * @return Events enumeration name with first letter lower-cased.
     */
    public String id() {
        return this.name().substring(0, 1).toLowerCase() + this.name().substring(1);
    }

    /**
     * Creates a Spring webflow event whose ID is given by {@link #id()}} and contains the following attributes:
     *
     * <ul>
     *     <li>code</li>
     *     <li>detailCode</li>
     * </ul>
     *
     * The values of attributes correspond to fields of the same names.
     *
     * @param source Event source.
     *
     * @return Spring webflow event.
     */
    public Event event(final Object source) {
        final LocalAttributeMap attributes = new LocalAttributeMap();
        attributes.put("code", this.code);
        attributes.put("detailCode", this.detailCode);
        return new Event(source, id(), attributes);
    }
}

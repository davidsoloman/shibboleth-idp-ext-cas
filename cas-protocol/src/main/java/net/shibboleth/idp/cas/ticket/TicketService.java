package net.shibboleth.idp.cas.ticket;

/**
 * CAS ticket management service.
 *
 * @author Marvin S. Addison
 */
public interface TicketService {
    /**
     * Creates and stores a ticket for the given service.
     *
     * @param service Service for which ticket is granted.
     */
    Ticket createTicket(String service);

    /**
     * Removes the ticket with the given identifier.
     *
     * @param ticketId Identifier of ticket to remove.
     *
     * @return Removed ticket or null if ticket not found.
     */
    Ticket removeTicket(String ticketId);
}

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
     * @param renew True to indicate the ticket was generated in response to a forced authentication, false otherwise.
     *
     * @return Created ticket.
     */
    ServiceTicket createServiceTicket(String service, boolean renew);

    /**
     * Removes the service ticket with the given identifier.
     *
     * @param ticketId Identifier of ticket to remove.
     *
     * @return Removed ticket or null if ticket not found.
     */
    ServiceTicket removeServiceTicket(String ticketId);
}

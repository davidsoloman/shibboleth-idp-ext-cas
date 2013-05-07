package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ticket storage service.
 *
 * @author Marvin S. Addison
 */
public interface TicketStore {

    /**
     * Stores the given ticket.
     *
     * @param ticket Ticket to store.
     */
    void add(@Nonnull Ticket ticket);


    /**
     * Fetches the ticket with the given ID from the store.
     *
     * @param id ID of ticket to fetch.
     *
     * @return Ticket or null if not found.
     */
    @Nullable Ticket get(@Nonnull String id);


    /**
     * Removes the ticket with the given ID.
     *
     * @param id ID of ticket to remove.
     */
    void remove(@Nonnull String id);


    /**
     * Removes all expired tickets from the store.
     *
     * @return Number of expired tickets removed or a negative number
     * if removal counts are not supported.
     */
    int expunge();
}

package net.shibboleth.idp.cas.ticket;

import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.messaging.context.BaseContext;

import javax.annotation.Nonnull;

/**
 * IdP context that stores a granted CAS ticket.
 * This context is typically a child of {@link org.opensaml.profile.context.ProfileRequestContext}.
 *
 * @author Marvin S. Addison
 */
public class TicketContext extends BaseContext {
    @Nonnull private final Ticket ticket;

    /**
     * Creates a new ticket context to hold a CAS protocol ticket.
     *
     * @param ticket Ticket to hold.
     */
    public TicketContext(@Nonnull final Ticket ticket) {
        Constraint.isNotNull(ticket, "Ticket cannot be null");
        this.ticket = ticket;
    }

    /** @return Ticket held by this context. */
    public Ticket getTicket() {
        return ticket;
    }
}

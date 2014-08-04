package net.shibboleth.idp.cas.protocol;

import net.shibboleth.utilities.java.support.logic.Constraint;

import javax.annotation.Nonnull;

/**
 * Container for proxy ticket response parameters returned from <code>/proxy</code> URI.
 *
 * @author Marvin S. Addison
 */
public class ProxyTicketResponse {
    @Nonnull private final String pt;

    /**
     * Creates a new instance with given parameters.
     *
     * @param pt Proxy ticket ID.
     */
    public ProxyTicketResponse(@Nonnull final String pt) {
        Constraint.isNotNull(pt, "PT cannot be null");
        this.pt = pt;
    }

    /** @return Proxy ticket ID. */
    @Nonnull public String getPt() {
        return pt;
    }
}

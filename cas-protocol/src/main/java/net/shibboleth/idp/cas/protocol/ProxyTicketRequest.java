/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.protocol;

import net.shibboleth.utilities.java.support.logic.Constraint;

import javax.annotation.Nonnull;

/**
 * Container for proxy ticket request parameters provided to <code>/proxy</code> URI.
 *
 * @author Marvin S. Addison
 */
public class ProxyTicketRequest {
    /** Proxy-granting ticket ID. */
    @Nonnull private final String pgt;

    /** Target service to which proxy ticket will be delivered. */
    @Nonnull private final String targetService;


    /**
     * Creates a new proxy ticket request with given parameters.
     *
     * @param pgt Non-null proxy-granting ticket ID.
     * @param targetService Non-null
     */
    public ProxyTicketRequest(@Nonnull final String pgt, @Nonnull final String targetService) {
        Constraint.isNotNull(pgt, "PGT cannot be null");
        Constraint.isNotNull(targetService, "TargetService cannot be null");
        this.pgt = pgt;
        this.targetService = targetService;
    }

    /** @return Proxy-granting ticket ID. */
    @Nonnull public String getPgt() {
        return pgt;
    }

    /** @return Target service to which proxy ticket will be delivered. */
    @Nonnull public String getTargetService() {
        return targetService;
    }
}

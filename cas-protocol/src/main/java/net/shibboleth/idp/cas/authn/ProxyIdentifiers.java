package net.shibboleth.idp.cas.authn;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Result of successful proxy authentication containing the proxy-granting ticket ID and proxy-granting ticket IOU.
 *
 * @author Marvin S. Addison
 */
public class ProxyIdentifiers {

    /** Proxy-granting ticket ID. */
    @Nonnull private final String pgtId;

    /** Proxy-granting ticket IOU. */
    @Nonnull private final String pgtIou;

    public ProxyIdentifiers(@Nonnull final String pgtId, @Nonnull final String pgtIou) {
        this.pgtId = Constraint.isNotNull(pgtId, "PGT cannot be null.");
        this.pgtIou = Constraint.isNotNull(pgtIou, "PGTIOU cannot be null.");
    }

    @Nonnull public String getPgtId() {
        return pgtId;
    }

    @Nonnull public String getPgtIou() {
        return pgtIou;
    }
}

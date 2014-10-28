/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.messaging.context.BaseContext;

/**
 * IdP context container for CAS service (i.e. relying party) metadata.
 * This context is typically a child of {@link org.opensaml.profile.context.ProfileRequestContext}.
 *
 * @author Marvin S. Addison
 */
public class ServiceContext extends BaseContext {
    @Nonnull
    private final Service service;

    /**
     * Creates a new instance.
     *
     * @param service Service metadata held by context.
     */
    public ServiceContext(@Nonnull final Service service) {
        Constraint.isNotNull(service, "Service cannot be null");
        this.service = service;
    }

    /** @return Service metadata held by this context. */
    public Service getService() {
        return service;
    }
}

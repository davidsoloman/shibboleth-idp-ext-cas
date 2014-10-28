/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * Registry for explicitly verified CAS services (relying parties).
 *
 * @author Marvin S. Addison
 */
public interface ServiceRegistry {
    /**
     * Looks up a service entry from a service URL.
     *
     * @param serviceURL CAS service URL.
     *
     * @return Service found in registry or null if no match found.
     */
    @Nullable Service lookup(@Nonnull @NotEmpty String serviceURL);
}

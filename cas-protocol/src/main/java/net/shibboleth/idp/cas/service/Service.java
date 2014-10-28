/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import java.security.Principal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Container for metadata about a CAS service (i.e. relying party).
 *
 * @author Marvin S. Addison
 */
public class Service implements Principal {

    /** Service URL. */
    @Nonnull
    @NotEmpty
    private final String serviceURL;

    /** Group to which service belongs. */
    @Nullable
    private final String group;

    /** Proxy authorization flag. */
    private final boolean authorizedToProxy;

    /**
     * Creates a new service from given URL and group name.
     *
     * @param url CAS service URL.
     * @param group Group to which service belongs.
     * @param proxy True to authorize proxying, false otherwise.
     */
    public Service(
            @Nonnull @NotEmpty final String url,
            @Nullable @NotEmpty final String group,
            final boolean proxy) {
        this.serviceURL = Constraint.isNotNull(StringSupport.trimOrNull(url), "Service URL cannot be null or empty");
        this.group = StringSupport.trimOrNull(group);
        this.authorizedToProxy = proxy;
    }

    /** @return Service URL. */
    @Override
    public String getName() {
        return serviceURL;
    }

    /** @return Service group name. */
    @Nullable
    public String getGroup() {
        return group;
    }

    /** @return True if proxying is authorized, false otherwise. */
    public boolean isAuthorizedToProxy() {
        return authorizedToProxy;
    }
}

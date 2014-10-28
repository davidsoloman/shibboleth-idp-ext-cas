/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Defines a registered CAS service (i.e. relying party).
 *
 * @author Marvin S. Addison
 */
public class ServiceDefinition {
    /** Pattern used to match candidate services against. */
    @Nonnull
    private final Pattern matchPattern;

    /** Logical group to which matching servcies belong. */
    @Nullable
    private String group;

    /** Proxy authorization flag. */
    private boolean authorizedToProxy;


    /**
     * Creates a new instance with the given regular expression match pattern.
     *
     * @param regex CAS service URL match pattern as Java regular expression string.
     */
    public ServiceDefinition(@Nonnull @NotEmpty final String regex) {
        matchPattern = Pattern.compile(
                Constraint.isNotNull(StringSupport.trimOrNull(regex), "Regular expression cannot be null or empty"));
    }

    /**
     * Creates a new instance with the given regular expression match pattern.
     *
     * @param regex CAS service URL match pattern.
     */
    public ServiceDefinition(@Nonnull final Pattern pattern) {
        matchPattern = Constraint.isNotNull(pattern, "Pattern cannot be null or empty");
    }

    /**
     * @return Group name to which services matching this definition belong.
     */
    @Nullable public String getGroup() {
        return group;
    }

    /**
     * Sets the group name.
     *
     * @param group Group name.
     */
    public void setGroup(@NotEmpty final String group) {
        this.group = StringSupport.trimOrNull(group);
    }

    /** @return True if proxy is authorized, false otherwise. */
    public boolean isAuthorizedToProxy() {
        return authorizedToProxy;
    }

    /**
     * Sets the proxy authorization flag.
     *
     * @param proxy True to allow the service to request proxy-granting tickets, false otherwise.
     */
    public void setAuthorizedToProxy(final boolean proxy) {
        this.authorizedToProxy = proxy;
    }

    /**
     * Determines whether the given CAS service URL matches the pattern.
     *
     * @param serviceURL CAS service URL to test; MUST NOT be URL encoded.
     *
     * @return True if given service URL matches the pattern, false otherwise.
     */
    public boolean matches(final String serviceURL) {
        return matchPattern.matcher(serviceURL).matches();
    }

    @Override
    public String toString() {
        return matchPattern.pattern();
    }
}

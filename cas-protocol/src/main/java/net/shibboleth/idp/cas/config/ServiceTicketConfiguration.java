/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.config;

import com.google.common.base.Predicates;
import com.google.common.collect.*;
import net.shibboleth.idp.profile.config.AuthenticationProfileConfiguration;
import net.shibboleth.idp.saml.authn.principal.AuthnContextClassRefPrincipal;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;

import javax.annotation.Nonnull;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * CAS service ticket configuration modeled as an IdP profile.
 *
 * @author Marvin S. Addison
 */
public class ServiceTicketConfiguration extends AbstractTicketConfiguration
        implements AuthenticationProfileConfiguration {

    /** Service ticket profile URI. */
    public static final String PROFILE_ID = PROTOCOL_URI + "/st";

    /** Filters the usable authentication flows. */
    @Nonnull
    @NonnullElements
    private Set<String> authenticationFlows = Collections.emptySet();

    /** Selects, and limits, the authentication contexts to use for requests. */
    @Nonnull
    @NonnullElements
    private List<AuthnContextClassRefPrincipal> defaultAuthenticationContexts = Collections.emptyList();

    /** Precedence of name identifier formats to use for requests. */
    @Nonnull
    @NonnullElements
    private List<String> nameIDFormatPrecedence = Collections.emptyList();


    /** Creates a new instance. */
    public ServiceTicketConfiguration() {
        super(PROFILE_ID);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @NotLive
    @Unmodifiable
    public List<Principal> getDefaultAuthenticationMethods() {
        return ImmutableList.<Principal> copyOf(defaultAuthenticationContexts);
    }

    /**
     * Set the default authentication contexts to use, expressed as custom principals.
     *
     * @param contexts default authentication contexts to use
     */
    public void setDefaultAuthenticationMethods(
            @Nonnull @NonnullElements final List<AuthnContextClassRefPrincipal> contexts) {
        Constraint.isNotNull(contexts, "List of contexts cannot be null");

        defaultAuthenticationContexts = Lists.newArrayList(Collections2.filter(contexts, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @NotLive @Unmodifiable
    public Set<String> getAuthenticationFlows() {
        return ImmutableSet.copyOf(authenticationFlows);
    }

    /**
     * Set the authentication flows to use.
     *
     * @param flows   flow identifiers to use
     */
    public void setAuthenticationFlows(@Nonnull @NonnullElements final Collection<String> flows) {
        Constraint.isNotNull(flows, "Collection of flows cannot be null");

        authenticationFlows = Sets.newHashSet(Collections2.filter(flows, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @NotLive @Unmodifiable
    public List<String> getNameIDFormatPrecedence() {
        return ImmutableList.copyOf(nameIDFormatPrecedence);
    }

    /**
     * Set the name identifier formats to use.
     *
     * @param formats name identifier formats to use
     */
    public void setNameIDFormatPrecedence(@Nonnull @NonnullElements final List<String> formats) {
        Constraint.isNotNull(formats, "List of formats cannot be null");

        nameIDFormatPrecedence = Lists.newArrayList(Collections2.filter(formats, Predicates.notNull()));
    }

}

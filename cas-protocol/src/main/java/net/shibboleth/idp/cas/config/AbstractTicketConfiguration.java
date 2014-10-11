/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.config;

import net.shibboleth.idp.profile.config.AbstractProfileConfiguration;
import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;

import javax.annotation.Nonnull;

/**
 * Base class for CAS protocol configuration.
 *
 * @author Marvin S. Addison
 */
public abstract class AbstractTicketConfiguration extends AbstractProfileConfiguration
        implements InitializableComponent {

    /** CAS base protocol URI. */
    public static final String PROTOCOL_URI = "https://www.apereo.org/cas/protocol";

    /** Initialization flag. */
    private boolean initialized;

    /** Validity time period of tickets. */
    @Duration
    @Positive
    private long ticketValidityPeriod;

    /**
     * Creates a new configuration instance.
     *
     * @param profileId Unique profile identifier.
     */
    public AbstractTicketConfiguration(@Nonnull @NotEmpty final String profileId) {
        super(profileId);
    }

    @Override
    public void initialize() throws ComponentInitializationException {
        Constraint.isNotNull(getSecurityConfiguration(), "Security configuration cannot be null.");
        Constraint.isNotNull(getSecurityConfiguration().getIdGenerator(),
                "Security configuration ID generator cannot be null.");
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @return Ticket validity period in milliseconds.
     */
    @Positive
    public long getTicketValidityPeriod() {
        return ticketValidityPeriod;
    }

    /**
     * Sets the ticket validity period.
     *
     * @param millis Ticket validity period in milliseconds.
     */
    public void setTicketValidityPeriod(@Duration @Positive final long millis) {
        this.ticketValidityPeriod = Constraint.isGreaterThan(0, millis, "Ticket validity period must be positive.");
    }
}

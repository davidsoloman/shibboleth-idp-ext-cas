/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service registry that evaluates a candidate service URL against one or more defined services, where each
 * definition contains a service URL match pattern used to evaluate membership in the registry.
 *
 * @author Marvin S. Addison
 */
public class PatternServiceRegistry implements ServiceRegistry {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PatternServiceRegistry.class);

    /** List of service definitions that back registry. */
    @Nonnull
    @NonnullElements
    private List<ServiceDefinition> definitions = Collections.emptyList();

    /**
     * Sets the list of service definitions that back the registry.
     * @param definitions List of service definitions, each of which defines a match pattern to evaluate a candidate
     *                    service URL.
     */
    public void setDefinitions(@Nonnull @NonnullElements List<ServiceDefinition> definitions) {
        this.definitions = Constraint.isNotNull(definitions, "Service definition list cannot be null");
    }

    @Override
    @Nullable
    public Service lookup(@Nonnull String serviceURL) {
        Constraint.isNotNull(serviceURL, "Service URL cannot be null");
        for (ServiceDefinition def : definitions) {
            log.debug("Evaluating whether {} matches {}", serviceURL, def);
            if (def.matches(serviceURL)) {
                log.debug("Found match");
                return new Service(serviceURL, def.getGroup(), def.isAuthorizedToProxy());
            }
        }
        return null;
    }
}

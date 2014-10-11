/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import org.cryptacular.generator.IdGenerator;
import org.cryptacular.generator.RandomIdGenerator;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Generates CAS protocol ticket identifiers of the form:
 *
 * <pre>
 * [PREFIX]-[SEQUENCE_PART]-[RANDOM_PART]-[SUFFIX],
 * </pre>
 *
 * where suffix is optional. By default tickets have at least 128 bits of entropy in the random part of the identifier.
 *
 * @author Marvin S. Addison
 */
public class TicketIdentifierGenerationStrategy implements IdentifierGenerationStrategy {

    /** Ticket prefix. */
    @Nonnull @NotEmpty private String prefix;

    /** Ticket suffix. */
    @Nullable private String suffix;

    /** Generator of random ticket part. */
    private IdGenerator randomPartGenerator;


    /**
     * Creates a new ticket ID generator.
     *
     * @param randomLength Length in characters of random part of the ticket.
     * @param prefix Ticket ID prefix (e.g. ST, PT, PGT). MUST be a URL safe string.
     */
    public TicketIdentifierGenerationStrategy(
            @Positive final int randomLength,
            @Nonnull @NotEmpty final String prefix) {
        if (randomLength < 1) {
            throw new IllegalArgumentException("Length of random part of ticket must be positive");
        }
        this.randomPartGenerator = new RandomIdGenerator(randomLength);
        this.prefix = Constraint.isNotNull(StringSupport.trimOrNull(prefix), "Prefix cannot be null or empty");
        if (!isUrlSafe(this.prefix)) {
            throw new IllegalArgumentException("Unsupported prefix " + this.prefix);
        }
    }

    /**
     * Sets the ticket ID suffix.
     *
     * @param suffix Ticket suffix.
     */
    public void setSuffix(@Nullable final String suffix) {
        final String s = StringSupport.trimOrNull(suffix);
        if (s != null) {
            if (!isUrlSafe(s)) {
                throw new IllegalArgumentException("Unsupported suffix " + s);
            }
            this.suffix = s;
        }
    }

    @Override
    @Nonnull public String generateIdentifier() {
        final StringBuilder builder = new StringBuilder(100);
        builder.append(prefix).append('-');
        builder.append(System.currentTimeMillis()).append('-');
        builder.append(randomPartGenerator.generate());
        if (suffix != null) {
            builder.append('-').append(suffix);
        }
        return builder.toString();
    }

    @Nonnull
    @Override
    public String generateIdentifier(final boolean xmlSafe) {
        return generateIdentifier();
    }

    private static boolean isUrlSafe(final String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.US_ASCII.name()).equals(s);
        } catch (Exception e) {
            return false;
        }
    }
}

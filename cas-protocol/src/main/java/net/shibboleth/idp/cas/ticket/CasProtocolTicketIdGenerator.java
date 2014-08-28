/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.ticket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import org.cryptacular.generator.IdGenerator;
import org.cryptacular.generator.RandomIdGenerator;
import org.springframework.util.StringUtils;

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
public class CasProtocolTicketIdGenerator implements TicketIdGenerator, InitializableComponent {

    /** Default ticket prefix, {@value}. */
    private static final String DEFAULT_PREFIX = "ST";

    /** Default number of characters in the random part of a generated ticket, {@value}. */
    private static final int DEFAULT_LENGTH = 25;

    /** Number of characters in random part of generated ticket. */
    @Positive private int length = DEFAULT_LENGTH;

    /** Ticket prefix. */
    @Nonnull private String prefix = DEFAULT_PREFIX;

    /** Ticket suffix. */
    @Nullable private String suffix;

    /** Generator of random ticket part. */
    private IdGenerator randomPartGenerator;

    public void setLength(@Positive final int length) {
        this.length = (int) Constraint.isGreaterThan(0, length, "Length must be greater than 0.");
    }

    public void setPrefix(@Nonnull final String prefix) {
        this.prefix = Constraint.isNotNull(StringSupport.trimOrNull(prefix), "Prefix cannot be null.");
    }

    public void setSuffix(@Nullable final String suffix) {
        this.suffix = StringSupport.trimOrNull(suffix);
    }

    @Override
    public boolean isInitialized() {
        return randomPartGenerator != null;
    }

    @Override
    public void initialize() throws ComponentInitializationException {
        try {
            randomPartGenerator = new RandomIdGenerator(this.length);
        } catch (Exception e) {
            throw new ComponentInitializationException("Error initializing random ID generator", e);
        }
    }

    @Override
    @Nonnull public String generate() {
        final StringBuilder builder = new StringBuilder(2 * length);
        builder.append(prefix).append('-');
        builder.append(System.currentTimeMillis()).append('-');
        builder.append(randomPartGenerator.generate());
        if (StringUtils.hasText(suffix)) {
            builder.append('-').append(suffix);
        }
        return builder.toString();
    }

}

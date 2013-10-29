/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.shibboleth.idp.cas.ticket;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
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
public class CasProtocolTicketIdGenerator implements TicketIdGenerator {

    /** Default ticket prefix, {@value}. */
    private static final String DEFAULT_PREFIX = "ST";

    /** Default number of characters in the random part of a generated ticket, {@value}. */
    private static final int DEFAULT_LENGTH = 25;

    /** Allowed characters in random part of ticket identifier. */
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679";

    /** Number of characters in random part of generated ticket. */
    @Positive private int length = DEFAULT_LENGTH;

    /** Ticket prefix. */
    @Nonnull private String prefix = DEFAULT_PREFIX;

    /** Ticket suffix. */
    @Nullable private String suffix;

    /** Random source . */
    private final SecureRandom random;


    /** Creates a new ticket generator instance. */
    public CasProtocolTicketIdGenerator() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot instantiate SHA1PRNG random algorithm.");
        }
    }

    public void setLength(@Positive final int length) {
        this.length = (int) Constraint.isGreaterThan(0, length, "Length must be greater than 0.");
    }

    public void setPrefix(@Nonnull final String prefix) {
        this.prefix = Constraint.isNotNull(StringSupport.trimOrNull(prefix), "Prefix cannot be null.");
    }

    public void setSuffix(@Nullable final String suffix) {
        this.suffix = StringSupport.trimOrNull(suffix);
    }

    @Nonnull public String generate() {
        final StringBuilder builder = new StringBuilder(2 * length);
        builder.append(prefix).append('-');
        builder.append(System.currentTimeMillis()).append('-');
        for (int i = 0; i < length; i++) {
            builder.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        if (StringUtils.hasText(suffix)) {
            builder.append('-').append(suffix);
        }
        return builder.toString();
    }
}

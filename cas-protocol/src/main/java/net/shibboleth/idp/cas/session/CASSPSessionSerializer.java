/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.session;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

import net.shibboleth.idp.session.AbstractSPSessionSerializer;
import net.shibboleth.idp.session.SPSession;
import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NonNegative;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * JSON serializer for {@link CASSPSession} class.
 *
 * @author Marvin S. Addison
 */
public class CASSPSessionSerializer extends AbstractSPSessionSerializer {

    /** Field name of CAS ticket. */
    @Nonnull @NotEmpty private static final String TICKET_FIELD = "st";


    /**
     * Constructor.
     *
     * @param offset milliseconds to subtract from record expiration to establish session expiration value
     */
    public CASSPSessionSerializer(@Duration @NonNegative long offset) {
        super(offset);
    }

    @Override
    protected void doSerializeAdditional(@Nonnull final SPSession instance, @Nonnull final JsonGenerator generator) {
        if (!(instance instanceof CASSPSession)) {
            throw new IllegalArgumentException("Expected instance of CASSPSession but got " + instance);
        }
        generator.write(TICKET_FIELD, ((CASSPSession) instance).getTicketId());
    }

    @Nonnull
    @Override
    protected SPSession doDeserialize(
            @Nonnull final JsonObject obj,
            @Nonnull @NotEmpty final String id,
            final long creation,
            final long expiration) throws IOException {
        return new CASSPSession(id, creation, expiration, obj.getString(TICKET_FIELD));
    }
}

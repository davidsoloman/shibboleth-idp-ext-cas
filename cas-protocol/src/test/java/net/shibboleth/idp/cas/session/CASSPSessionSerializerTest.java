/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.session;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CASSPSessionSerializerTest {

    private CASSPSessionSerializer serializer = new CASSPSessionSerializer(0);

    @Test
    public void testSerializeDeserialize() throws Exception{
        final long exp = 1410539474000000000L;
        final CASSPSession original = new CASSPSession(
                "https://foo.example.com/shibboleth",
                "authn/Password",
                1410532279838046000L,
                exp,
                "ST-1234126-ABC1346DEADBEEF");
        final String serialized = serializer.serialize(original);
        final CASSPSession deserialized = (CASSPSession) serializer.deserialize(1, "context", "key", serialized, exp);
        assertEquals(deserialized.getId(), original.getId());
        assertEquals(deserialized.getAuthenticationFlowId(), original.getAuthenticationFlowId());
        assertEquals(deserialized.getCreationInstant(), original.getCreationInstant());
        assertEquals(deserialized.getExpirationInstant(), original.getExpirationInstant());
        assertEquals(deserialized.getTicketId(), original.getTicketId());
    }

}
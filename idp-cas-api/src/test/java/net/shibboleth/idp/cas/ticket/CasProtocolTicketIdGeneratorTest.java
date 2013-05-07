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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for {@link CasProtocolTicketIdGenerator}.
 *
 * @author Marvin S. Addison
 */
public class CasProtocolTicketIdGeneratorTest {

    private static final Pattern TICKET_REGEX = Pattern.compile("(.+)-(\\d+)-([A-Za-z0-9]+)(-(.+))?");

    @DataProvider(name = "generators")
    public Object[][] provideGenerators() {
        final CasProtocolTicketIdGenerator defaultGenerator = new CasProtocolTicketIdGenerator();
        final CasProtocolTicketIdGenerator prefixGenerator = new CasProtocolTicketIdGenerator();
        prefixGenerator.setPrefix("PT");
        final CasProtocolTicketIdGenerator suffixGenerator = new CasProtocolTicketIdGenerator();
        suffixGenerator.setSuffix("node1");
        suffixGenerator.generate();
        final CasProtocolTicketIdGenerator longGenerator = new CasProtocolTicketIdGenerator();
        longGenerator.setLength(100);
        final CasProtocolTicketIdGenerator christmasTreeGenerator = new CasProtocolTicketIdGenerator();
        christmasTreeGenerator.setPrefix("AA");
        christmasTreeGenerator.setLength(23);
        christmasTreeGenerator.setSuffix("nodeZ");
        return new Object[][] {
                new Object[] { defaultGenerator, "ST", 1, 20, null },
                new Object[] { prefixGenerator, "PT", 1, 20, null },
                new Object[] { suffixGenerator, "ST", 2, 20, "node1" },
                new Object[] { longGenerator, "ST", 1, 100, null },
                new Object[] { christmasTreeGenerator, "AA", 1, 23, "nodeZ" },
        };
    }

    @Test(dataProvider = "generators")
    public void testGenerate(
            final CasProtocolTicketIdGenerator generator,
            final String expectedPrefix,
            final int expectedSequenceNumber,
            final int expectedRandomLength,
            final String expectedSuffix) throws Exception {

        final String id = generator.generate();
        final Matcher m = TICKET_REGEX.matcher(id);
        assertTrue(m.matches());
        assertEquals(expectedPrefix, m.group(1));
        assertEquals(expectedSequenceNumber, Integer.parseInt(m.group(2)));
        assertEquals(expectedRandomLength, m.group(3).length());
        if (expectedSuffix != null) {
            assertEquals(expectedSuffix, m.group(5));
        }
    }
}
/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.service;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PatternServiceRegistryTest {

    @DataProvider(name = "services")
    public Object[][] getServices() {
        final ServiceDefinition d1 = new ServiceDefinition("https://([A-Za-z0-9_-]+\\.)*example\\.org(:\\d+)?/.*");
        d1.setGroup("example.org-plus-subdomains");
        d1.setAuthorizedToProxy(false);
        final ServiceDefinition d2 = new ServiceDefinition("https://trusted\\.example\\.org/.*");
        d2.setGroup("trusted-service");
        d2.setAuthorizedToProxy(true);
        return new Object[][] {
                {
                        Arrays.asList(d1, d2),
                        "https://trusted.example.org/landing",
                        new Service("https://trusted.example.org/landing", "example.org-plus-subdomains", false),
                },
                {
                        Arrays.asList(d2, d1),
                        "https://trusted.example.org/landing",
                        new Service("https://trusted.example.org/landing", "trusted-service", true),
                },
                {
                        Arrays.asList(d1, d2),
                        "https://service.untrusted.org/landing",
                        null,
                },
        };
    };

    @Test(dataProvider = "services")
    public void testLookup(
            final List<ServiceDefinition> services, final String serviceURL, final Service expected)
            throws Exception {
        final PatternServiceRegistry registry = new PatternServiceRegistry();
        registry.setDefinitions(services);
        final Service actual = registry.lookup(serviceURL);
        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(actual.getName(), expected.getName());
            assertEquals(actual.getGroup(), expected.getGroup());
            assertEquals(actual.isAuthorizedToProxy(), expected.isAuthorizedToProxy());
        }
    }
}
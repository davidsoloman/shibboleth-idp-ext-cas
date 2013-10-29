package net.shibboleth.idp.cas.util;

import java.security.KeyStore;
import java.util.Arrays;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit test for {@link TrustStoreFactoryBean}.
 *
 * @author Marvin S. Addison
 */
public class TrustStoreFactoryBeanTest {

    @DataProvider(name = "data")
    public Object[][] provideGenerators() {
        return new Object[][] {
                new Object[] { new Resource[] { new ClassPathResource("/certs/nobody-1.pem") } },
                new Object[] { new Resource[] {
                        new ClassPathResource("/certs/nobody-1.pem"),
                        new ClassPathResource("/certs/nobody-2.pem") },
                },
        };
    }

    @Test(dataProvider = "data")
    public void testCreateInstance(final Resource[] resources) throws Exception {
        final TrustStoreFactoryBean factory = new TrustStoreFactoryBean();
        factory.setTrustedCertificates(Arrays.asList(resources));
        final KeyStore actual = factory.createInstance();
        assertEquals(actual.size(), resources.length);
    }
}

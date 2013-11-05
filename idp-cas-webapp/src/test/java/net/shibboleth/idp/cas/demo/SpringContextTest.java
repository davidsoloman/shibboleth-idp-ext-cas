package net.shibboleth.idp.cas.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Verifies Spring wiring.
 *
 * @author Marvin S. Addison
 */
public class SpringContextTest {

    @DataProvider(name = "contexts")
    public Object[][] testData() {
        return new Object[][] {
                new Object[] {
                        new String [] {
                                "classpath:/conf/global-beans.xml",
                                "classpath:/conf/session-manager.xml",
                                "file:src/main/webapp/WEB-INF/spring/idp-servlet.xml"
                        }
                }
        };
    }

    @Test(dataProvider = "contexts")
    public void testContext(final String[] contextPaths) {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(contextPaths);
        assertTrue(context.getBeanDefinitionCount() > 0);
    }
}

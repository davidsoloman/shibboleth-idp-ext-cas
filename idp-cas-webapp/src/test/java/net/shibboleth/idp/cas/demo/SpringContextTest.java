package net.shibboleth.idp.cas.demo;

import org.springframework.context.support.FileSystemXmlApplicationContext;
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
                                "src/main/webapp/WEB-INF/spring/global-beans.xml",
                                "src/main/webapp/WEB-INF/spring/deployer-beans.xml",
                                "src/main/webapp/WEB-INF/spring/idp-servlet.xml"
                        }
                }
        };
    }

    @Test(dataProvider = "contexts")
    public void testContext(final String[] contextPaths) {
        final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(contextPaths);
        assertTrue(context.getBeanDefinitionCount() > 0);
    }
}

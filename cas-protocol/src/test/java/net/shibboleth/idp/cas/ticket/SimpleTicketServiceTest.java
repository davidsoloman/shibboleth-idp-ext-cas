package net.shibboleth.idp.cas.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit test for {@link SimpleTicketService} class.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration({
        "/conf/global-beans.xml",
        "/conf/session-manager.xml",
        "/conf/test-beans.xml",
        "/flows/cas-protocol-beans.xml"
})
public class SimpleTicketServiceTest extends AbstractTestNGSpringContextTests {

    private static final String TEST_SESSION_ID = "jHXRo42W0ATPEN+X5Zk1cw==";

    private static final String TEST_SERVICE = "https://example.com/widget";

    @Autowired
    private SimpleTicketService ticketService;

    @Test
    public void testCreateRemoveServiceTicket() throws Exception {
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        assertNotNull(st);
        assertEquals(ticketService.removeServiceTicket(st.getId()), st);
        assertNull(ticketService.removeServiceTicket(st.getId()));
    }

    @Test
    public void testCreateFetchRemoveProxyGrantingTicket() throws Exception {
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        assertNotNull(st);
        final ProxyGrantingTicket pgt = ticketService.createProxyGrantingTicket(st, TEST_SERVICE);
        assertNotNull(pgt);
        assertEquals(ticketService.fetchProxyGrantingTicket(pgt.getId()), pgt);
        assertEquals(ticketService.removeProxyGrantingTicket(pgt.getId()), pgt);
        assertNull(ticketService.removeProxyGrantingTicket(pgt.getId()));
    }

    @Test
    public void testCreateRemoveProxyTicket() throws Exception {
        final ServiceTicket st = ticketService.createServiceTicket(TEST_SESSION_ID, TEST_SERVICE, false);
        assertNotNull(st);
        final ProxyGrantingTicket pgt = ticketService.createProxyGrantingTicket(st, TEST_SERVICE);
        assertNotNull(pgt);
        final ProxyTicket pt = ticketService.createProxyTicket(pgt, TEST_SERVICE, true);
        assertNotNull(pt);
        assertEquals(ticketService.removeProxyTicket(pt.getId()), pt);
        assertNull(ticketService.removeProxyTicket(pt.getId()));
    }
}

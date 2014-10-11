/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.config;

/**
 * CAS proxy ticket configuration modeled as an IdP profile.
 *
 * @author Marvin S. Addison
 */
public class ProxyTicketConfiguration extends AbstractTicketConfiguration {
    /** Proxy ticket profile URI. */
    public static final String PROFILE_ID = PROTOCOL_URI + "/pt";


    /** Creates a new instance. */
    public ProxyTicketConfiguration() {
        super(PROFILE_ID);
    }
}

/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package net.shibboleth.idp.cas.protocol;

/**
 * SAML 1.1 protocol params needed to support <code>/samlValidate</code> endpoint.
 *
 * @author Marvin S. Addison
 */
public enum SamlParam {
    /** TARGET parameter used to convey service URL. */
    TARGET,

    /** SAMLart parameter used to convey service ticket. */
    SAMLart;
}

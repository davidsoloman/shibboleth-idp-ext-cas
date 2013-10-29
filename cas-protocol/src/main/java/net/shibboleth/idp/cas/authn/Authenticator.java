package net.shibboleth.idp.cas.authn;

import java.security.GeneralSecurityException;

/**
 * Strategy pattern component for authentication.
 *
 * @author Marvin S. Addison
 */
public interface Authenticator<CredentialType, ResultType> {
    /**
     * Authenticates the given credential.
     *
     * @param credential Credential to authenticate.
     *
     * @return An authentication result of the defined type.
     *
     * @throws GeneralSecurityException On authentication failure.
     */
    ResultType authenticate(CredentialType credential) throws GeneralSecurityException;
}

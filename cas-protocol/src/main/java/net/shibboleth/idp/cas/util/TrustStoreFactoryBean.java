package net.shibboleth.idp.cas.util;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.List;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.cryptacular.util.CertUtil;
import org.springframework.core.io.Resource;

/**
 * Creates a keystore containing exclusively trusted credential entries from a list of resources where each one
 * is a PEM-encoded or DER-encoded X.509 certificate.
 *
 * @author Marvin S. Addison
 */
public class TrustStoreFactoryBean {

    private static final char[] DEFAULT_PASSWORD = "changeit".toCharArray();

    /** List of trusted certificates. */
    @NotEmpty private List<Resource> trustedCertificates;

    public void setTrustedCertificates(@NotEmpty final List<Resource> trustedCertificates) {
        Constraint.isNotEmpty(trustedCertificates, "Trusted certificates cannot be null.");
        this.trustedCertificates = trustedCertificates;
    }

    /**
     * Creates a {@link KeyStore} containing trusted certificate entries corresponding to the
     * {@link #trustedCertificates} property.
     *
     * @return Keystore containing trusted certificate entries.
     *
     * @throws IOException On IO errors reading certificates.
     * @throws KeyStoreException On creating the keystore.
     */
    public KeyStore createInstance() throws KeyStoreException, IOException {
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keystore.load(null, DEFAULT_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing keystore", e);
        }
        X509Certificate certificate;
        for (Resource resource : trustedCertificates) {
            certificate = CertUtil.readCertificate(resource.getInputStream());
            keystore.setEntry(
                    CertUtil.subjectCN(certificate),
                    new KeyStore.TrustedCertificateEntry(certificate),
                    null);
        }
        return keystore;
    }
}

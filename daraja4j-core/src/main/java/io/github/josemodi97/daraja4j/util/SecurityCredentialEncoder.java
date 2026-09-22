package io.github.josemodi97.daraja4j.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Encodes the {@code SecurityCredential} field Daraja requires on every
 * Initiator-authenticated operation (B2C, B2B, B2Pochi, Reversal, Account
 * Balance, Transaction Status): the plaintext Initiator password, RSA-encrypted
 * with Safaricom's public certificate for the target environment and then
 * Base64-encoded. Sandbox and production use different certificates &mdash;
 * using the wrong one for the target environment is the most common Daraja
 * integration failure, since the request otherwise looks well-formed.
 *
 * <p>Built entirely on {@code java.security}/{@code javax.crypto} - no
 * third-party dependency.
 */
public final class SecurityCredentialEncoder {

    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private SecurityCredentialEncoder() {
    }

    /** Encrypts {@code initiatorPassword} with {@code certificate}'s public key and Base64-encodes the result. */
    public static String encode(String initiatorPassword, X509Certificate certificate) {
        try {
            PublicKey publicKey = certificate.getPublicKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(initiatorPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to encode the SecurityCredential: " + e.getMessage(), e);
        }
    }

    /** {@link #encode(String, X509Certificate)} after parsing the certificate from a PEM/DER stream. */
    public static String encode(String initiatorPassword, InputStream certificateStream) {
        return encode(initiatorPassword, loadCertificate(certificateStream));
    }

    /** {@link #encode(String, X509Certificate)} after loading and parsing the certificate from a file path. */
    public static String encode(String initiatorPassword, Path certificatePath) {
        try (InputStream in = Files.newInputStream(certificatePath)) {
            return encode(initiatorPassword, in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the Daraja public certificate at " + certificatePath, e);
        }
    }

    /** Parses an X.509 certificate (PEM or DER) from a stream. */
    public static X509Certificate loadCertificate(InputStream certificateStream) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(certificateStream);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to parse the Daraja public certificate: " + e.getMessage(), e);
        }
    }

    /** Parses an X.509 certificate (PEM or DER) from raw bytes. */
    public static X509Certificate loadCertificate(byte[] certificateBytes) {
        return loadCertificate(new ByteArrayInputStream(certificateBytes));
    }
}

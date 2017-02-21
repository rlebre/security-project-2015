package utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import server.ServerSecUtils;

/**
 * CHAP implementation
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class Cc {

    private final String cfgPath = "CitizenCard.cfg";
    private String name;

    public Cc() {
        name = null;
    }

    public boolean isCardConnected() {
        boolean connected = true;

        try {
            Provider provider = new sun.security.pkcs11.SunPKCS11(cfgPath);
            java.security.Security.addProvider(provider);

            KeyStore ks = KeyStore.getInstance("PKCS11", "SunPKCS11-PTeID");
        } catch (KeyStoreException | ProviderException ex) {
            connected = false;
        } catch (NoSuchProviderException ex) {
            System.err.println("PTeID provider not found");
            connected = true;
        }

        return connected;
    }

    private byte[] getName() throws IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, CertificateException {
        if (name == null) {
            Provider provider = new sun.security.pkcs11.SunPKCS11(cfgPath);
            java.security.Security.addProvider(provider);

            // Provides cryptographic keys
            KeyStore ks = KeyStore.getInstance("PKCS11", "SunPKCS11-PTeID");
            ks.load(null, null);

            //Get certificate
            X509Certificate certificate = (X509Certificate) ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");

            // Get name
            X500Principal principal = certificate.getSubjectX500Principal();
            name = principal.getName() + certificate.getSerialNumber().toString();
        }

        // hashing certificate ID
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
        }

        md.update(name.getBytes());

        for (int i = 0; i < 1000; i++) {
            byte[] aa = md.digest();
            md.update(aa);
        }

        return md.digest();
    }

    public byte[] getId() {
        try {
            return signData(getName());
        } catch (KeyStoreException | IOException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | UnrecoverableKeyException | CertificateException | SignatureException ex) {
            Logger.getLogger(Cc.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public byte[] signData(byte[] buf) throws KeyStoreException, IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, UnrecoverableKeyException, CertificateException, SignatureException {
        Provider p = new sun.security.pkcs11.SunPKCS11(cfgPath);
        Security.addProvider(p);

        KeyStore ks = KeyStore.getInstance("PKCS11", "SunPKCS11-PTeID");
        ks.load(null, null);

        PrivateKey privKey = (PrivateKey) ks.getKey("CITIZEN AUTHENTICATION CERTIFICATE", null);

        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initSign(privKey);

        sign.update(buf);
        return sign.sign();
    }

    public byte[] getCertificate() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
        Provider provider = new sun.security.pkcs11.SunPKCS11(cfgPath);
        java.security.Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("PKCS11", "SunPKCS11-PTeID");
        ks.load(null, null);

        Certificate certificate = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        return certificate.getEncoded();
    }

    public boolean verifySignature(String text, String sessionId) {

        try {
            String message = text.substring(0, text.lastIndexOf(":")) + sessionId;
            String signature = text.substring(text.lastIndexOf(":") + 1, text.length());
            byte[] cert = getCertificate();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(cert);
            Certificate certif = (Certificate) certFactory.generateCertificate(in);

            PublicKey pubKey = certif.getPublicKey();

            String hmac = new String(Base64.getEncoder().encode(produceHmac(pubKey, message)));

            //System.out.println(hmac);
            return signature.equals(hmac);
        } catch (InvalidKeyException | IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | NoSuchProviderException ex) {
            return false;
        }
    }

    private byte[] produceHmac(Key key, String message) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        byte[] keyBytes = key.getEncoded();

        // Generate HMAC SHA1 key
        Key hmacKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");

        // Generate HMAC
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(hmacKey);

        sha256_HMAC.update(message.getBytes());

        return sha256_HMAC.doFinal();
    }

    private String encodeBytes(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    private byte[] decodeString(String text) {
        return Base64.getDecoder().decode(text);
    }
}

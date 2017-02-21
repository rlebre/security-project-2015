package server;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class CcAuth {

    private final String cfgPath;
    private byte[] lastChallenge;

    public CcAuth() {
        cfgPath = "CitizenCard.cfg";
        lastChallenge = null;
    }

    public byte[] generateChallenge() {
        SecureRandom rnd = new SecureRandom();
        byte[] challenge = new byte[64];
        rnd.nextBytes(challenge);

        lastChallenge = challenge;
        return challenge;
    }

    public boolean checkCertChain(byte[] certificate) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathBuilderException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream is = new ByteArrayInputStream(certificate);
        X509Certificate targetCertificate = (X509Certificate) certificateFactory.generateCertificate(is);

        // Create keystore (this has all needed certificates to validate the chain)
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream("CC_KS"), null);

        // Separate certificates in two lists: anchors and intermediates
        Set<TrustAnchor> anchors = new HashSet<TrustAnchor>();
        Set<Certificate> intermediates = new HashSet<Certificate>();
        Enumeration<String> aliases = ks.aliases();

        while (aliases.hasMoreElements()) {

            String alias = aliases.nextElement();
            Certificate cert = ks.getCertificate(alias);
            PublicKey key = cert.getPublicKey();
            boolean isAnchor = true;

            // Verify if certificate is anchor or intermediate
            try {
                cert.verify(key);
            } catch (InvalidKeyException ex) {
                isAnchor = false;
            } catch (SignatureException ex) {
                isAnchor = false;
            }

            if (isAnchor) {
                anchors.add(new TrustAnchor((X509Certificate) cert, null));
            } else {
                intermediates.add(cert);
            }
        }

        // Define parameters
        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(targetCertificate);

        PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(anchors, selector);
        pkixParams.setRevocationEnabled(false);
        CertStore intermediatesCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediates));
        pkixParams.addCertStore(intermediatesCertStore);

        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
        PKIXCertPathBuilderResult path = (PKIXCertPathBuilderResult) builder.build(pkixParams);

        // Validate        
        CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
        PKIXParameters validationParams = new PKIXParameters(anchors);
        validationParams.setRevocationEnabled(false);
        validationParams.setDate(Calendar.getInstance().getTime());

        // Verify
        boolean valid = true;
        try {
            cpv.validate(path.getCertPath(), validationParams);
        } catch (CertPathValidatorException ex) {
            System.out.println("Error: An error ocurred while validating certificate.");
            valid = false;
        }

        return valid;
    }

    public boolean verifySignature(byte[] signature, PublicKey pubKey) throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
        Provider provider = new sun.security.pkcs11.SunPKCS11(cfgPath);
        java.security.Security.addProvider(provider);

        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(pubKey);
        sign.update(lastChallenge);

        return sign.verify(signature); //verifica a correçao dos dados
    }

    public boolean verifySignature(byte[] challenge, byte[] signature, PublicKey pubKey) throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
        //Provider provider = new sun.security.pkcs11.SunPKCS11(cfgPath);
        //java.security.Security.addProvider(provider);

        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(pubKey);
        sign.update(challenge);

        return sign.verify(signature); //verifica a correçao dos dados
    }
}

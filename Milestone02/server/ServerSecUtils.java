package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilities used on server side
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class ServerSecUtils {

    /**
     * Empty constructor.
     */
    public ServerSecUtils() {
    }

    /**
     * Generates a digest hash from password
     *
     * @param password Password in plain text
     * @return Hash of the given password
     */
    public byte[] hash(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("No hash algorithm found.");
        }
        md.update(password.getBytes());

        return md.digest();
    }

    /**
     * Validates username and email given as input
     *
     * @param username Username
     * @param email E-mail
     * @return True if input can be validated False otherwise.
     */
    public boolean validateUser(String username, String email) {
        Matcher matcher = null;
        Pattern userPattern = Pattern.compile("^[a-zA-Z0-9]{3,}[a-zA-Z0-9,._-]*");
        //Pattern pwPattern = Pattern.compile("^[a-zA-Z0-9]{1}[a-zA-Z0-9,._-]{3,}");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9]*@[a-zA-Z0-9]{1,}.[a-zA-Z]{1,4}");

        return userPattern.matcher(username).matches() && emailPattern.matcher(email).matches();
    }

    /**
     * Generates an unique User Key
     *
     * @param name Username
     * @param pw_hash Password hash
     * @return Byte array representing User Key
     */
    protected byte[] generateUserKey(String name, byte[] pw_hash) {
        SecureRandom random = new SecureRandom();
        byte[] offset = new BigInteger(32, random).toByteArray();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            // existe sempre este algoritmo
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(name.getBytes());
            outputStream.write(pw_hash);
            outputStream.write(offset);
            offset = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException ex) {
            // existe sempre o stream
        }
        md.update(offset);
        byte[] digest = md.digest();
        byte[] toReturn = new byte[16];
        System.arraycopy(digest, 15, toReturn, 0, 16);
        return toReturn;
    }

    /**
     * Generates an unique Player Key given a player ID
     *
     * @param base Byte array representing a player ID hash
     * @return Byte array representing Player Key
     */
    protected byte[] generatePlayerKey(byte[] base) {
        // long salt = 1284380985628823925L;
        byte[] salt = new byte[]{4, 124, -84, 97, -58, 117, -69, 79, 38, 97, -61,
                                 14, -80, 73, -106, -27, -81, -119, 76, 24, -107, 76,
                                 -57, 107, -10, 22, -8, -127, 95, 59, 38, -83};
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
        }

        md.update(salt);

        md.update(base);
        for (int i = 0;
                i < 1000; i++) {
            byte[] aa = md.digest();
            md.update(aa);
        }

        byte[] digest = md.digest();
        byte[] toReturn = new byte[16];
        System.arraycopy(digest, 15, toReturn, 0, 16);
        return toReturn;
    }

    /**
     * Generates an unique Device Key given a device ID
     *
     * @param base Byte array representing a device ID hash
     * @return Byte array representing Device Key
     */
    protected byte[] generateDeviceKey(byte[] base) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
        }

        md.update(base);
        for (int i = 0; i < 5000; i++) {
            byte[] aa = md.digest();
            md.update(aa);
        }

        byte[] digest = md.digest();
        byte[] toReturn = new byte[16];
        System.arraycopy(digest, 15, toReturn, 0, 16);
        return toReturn;
    }

    /**
     * Generates an unique random File Key
     *
     * @return Byte array representing File Key
     */
    protected byte[] generateFileKey() {
        SecureRandom random = new SecureRandom();
        byte[] offset = new BigInteger(32, random).toByteArray();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            // existe sempre este algoritmo
        }

        md.update(offset);
        byte[] digest = md.digest();
        byte[] toReturn = new byte[16];
        System.arraycopy(digest, 15, toReturn, 0, 16);
        return toReturn;
    }

    protected byte[] generateCcId(Certificate cert) {
        byte[] serial = ((X509Certificate) cert).getSerialNumber().toByteArray();
        byte[] name = ((X509Certificate) cert).getSubjectX500Principal().getName().getBytes();
        byte[] key = cert.getPublicKey().getEncoded();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            // existe sempre este algoritmo
        }

        byte[] offset = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(serial);
            outputStream.write(name);
            outputStream.write(key);
            offset = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException ex) {
            // existe sempre o stream
        }
        md.update(offset);
        for (int i = 0; i < 1000; i++) {
            byte[] aa = md.digest();
            md.update(aa);
        }

        byte[] digest = md.digest();

        return md.digest();
    }

    protected byte[] generateUserKey(String name, Certificate cert) {
        SecureRandom random = new SecureRandom();
        byte[] offset = new BigInteger(32, random).toByteArray();
        byte[] serial = ((X509Certificate) cert).getSerialNumber().toByteArray();
        byte[] namearray = ((X509Certificate) cert).getSubjectX500Principal().getName().getBytes();

        byte[] key = cert.getPublicKey().getEncoded();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            // existe sempre este algoritmo
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(name.getBytes());
            outputStream.write(serial);
            outputStream.write(namearray);
            outputStream.write(key);
            outputStream.write(offset);
            offset = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException ex) {
            // existe sempre o stream
        }
        md.update(offset);
        byte[] digest = md.digest();
        byte[] toReturn = new byte[16];
        System.arraycopy(digest, 15, toReturn, 0, 16);
        return toReturn;
    }

    public String produceHmac(Key key, String message) {

        byte[] keyBytes = key.getEncoded();

        // Generate HMAC SHA1 key
        Key hmacKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");

        // Generate HMAC
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(hmacKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return null;
        }

        sha256_HMAC.update(message.getBytes());

        return encodeBytes(sha256_HMAC.doFinal());
    }

    public boolean verifySignature(String text, String sessionId, PublicKey pubKey) {

        try {
            String message = text.substring(0, text.lastIndexOf(":"));
            byte[] signature = decodeString(text.substring(text.lastIndexOf(":") + 1, text.length()));
            byte[] hmac = produceHmac(pubKey, message + sessionId).getBytes();

            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(pubKey);
            sign.update(hmac);

            return sign.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            return false;
        }
    }

    private String encodeBytes(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    private byte[] decodeString(String text) {
        return Base64.getDecoder().decode(text);
    }
}

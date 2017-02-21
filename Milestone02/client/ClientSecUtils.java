package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilities used on client side
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class ClientSecUtils {

    /**
     * Empty constructor
     */
    public ClientSecUtils() {

    }

    /**
     * Validates username and email given as input
     *
     * @param username Username
     * @param email E-mail
     * @return True if input can be validated False otherwise.
     */
    public boolean validateUser(String username, String email) {
        Pattern userPattern = Pattern.compile("^[a-zA-Z0-9]{3,}[a-zA-Z0-9,._-]*");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9]*@[a-zA-Z0-9]{1,}.[a-zA-Z]{1,4}");

        return userPattern.matcher(username).matches() && emailPattern.matcher(email).matches();
    }

    /**
     * Generates a digest hash from password
     *
     * @param password Password in plain text
     * @return Hash of the given password
     */
    public byte[] hashPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
        }
        md.update(password.getBytes());

        return md.digest();
    }

    /**
     * Generates an unique Player Key given a player ID
     *
     * @param base Byte array representing a player ID hash
     * @return Byte array representing Player Key
     */
    public byte[] generatePlayerKey(byte[] base) {
        byte[] salt = new byte[]{4, 124, -84, 97, -58, 117, -69, 79, 38, 97, -61,
                                 14, -80, 73, -106, -27, -81, -119, 76, 24, -107, 76,
                                 -57, 107, -10, 22, -8, -127, 95, 59, 38, -83};
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("SHA algorithm not found.");
            System.exit(0);
        }

        md.update(salt);

        md.update(base);
        for (int i = 0; i < 1000; i++) {
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
    public byte[] generateDeviceKey(byte[] base) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("SHA algorithm not found.");
            System.exit(0);
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
     * Returns a device ID based on board serial number or UUID
     *
     * @param mode Mode: can be "SN" or "UUID"
     * @return Serial number
     */
    public String getDeviceId(String mode) {
        boolean modeBool = mode.equals("SN");
        GetSN serialnumber = new GetSN(modeBool);
        return serialnumber.getSN();
    }

    /**
     * Decrypts the file key using the device key
     *
     * @param key Device key
     * @param text Encrypted file key
     * @return File key
     */
    public byte[] decryptKey(byte[] key, byte[] text) {
        byte[] cipherText = null;
        SecretKey secretKey = null;
        String cipherMode = "";

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Cipher algorithm not found.");
            System.exit(0);
        }

        secretKey = new SecretKeySpec(key, "AES");
        cipherMode = "AES/CBC/NoPadding";

        Cipher c = null;
        try {
            c = Cipher.getInstance(cipherMode);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            System.err.println("Error deciphering file: no algorithm");
        }

        IvParameterSpec ivspec = new IvParameterSpec("keyinitializvect".getBytes());
        try {
            c.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
            System.err.println("Error deciphering file: invalidKey");
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(ClientSecUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        long bytesRead = 0;
        long fileSize = text.length;
        int blockSize = c.getBlockSize();

        byte[] dataBlock = new byte[blockSize];

        ByteArrayInputStream inputStream = new ByteArrayInputStream(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            while (bytesRead < fileSize) {
                bytesRead += inputStream.read(dataBlock);
                cipherText = c.update(dataBlock);
                outputStream.write(cipherText);
            }
        } catch (IOException e) {
            System.err.println("Error deciphering file: i/o exception");
        }

        try {
            cipherText = c.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            System.err.println("Error deciphering file: block size / bad padding");
        }
        try {
            outputStream.write(cipherText);
            outputStream.flush();
        } catch (IOException ex) {
            System.err.println("IO error.");
        }
        return outputStream.toByteArray();
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

        return new String(Base64.getEncoder().encode(sha256_HMAC.doFinal()));
    }
}

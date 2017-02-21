package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implements an IEDCS cryptographic header
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @version 1.0
 */
public class CryptoHeader {

    private byte[] fileKey;
    private byte[] deviceKey;
    private byte[] userKey;
    private byte[] playerKey;
    private byte[] header;

    /**
     * Constructs a cryptographic header
     *
     * @param deviceKey Client device key
     * @param playerKey Client player key
     * @param userKey Client user key
     * @param fileKey Random file key
     */
    public CryptoHeader(byte[] deviceKey, byte[] playerKey, byte[] userKey, byte[] fileKey) {
        System.arraycopy(deviceKey, 15, this.deviceKey, 0, 16);
        System.arraycopy(userKey, 15, this.userKey, 0, 16);
        System.arraycopy(playerKey, 15, this.playerKey, 0, 16);

        System.arraycopy(fileKey, 15, this.fileKey, 0, 16);
        header = getHeader();
    }

    /**
     * Constructs a cryptographic header
     *
     * @param deviceKey Client device key
     * @param playerKey Client player key
     * @param fileKey Random file key
     * @param username Client username
     * @param db Private connection to database
     */
    public CryptoHeader(byte[] deviceKey, byte[] playerKey, byte[] fileKey, String username, DbConnection db) {
        ServerSecUtils utils = new ServerSecUtils();
        this.fileKey = new byte[16];
        this.deviceKey = new byte[16];
        this.userKey = new byte[16];
        this.playerKey = new byte[16];

        System.arraycopy(deviceKey, 0, this.deviceKey, 0, 16);
        System.arraycopy(playerKey, 0, this.playerKey, 0, 16);
        System.arraycopy(fileKey, 0, this.fileKey, 0, 16);
        System.arraycopy(db.getUserKey(username), 0, this.userKey, 0, 16);

        header = getHeader();
    }

    /**
     * Returns the full constructed header
     *
     * @return Full constructed header
     */
    public byte[] getHeader() {
        byte[] first = initCrypto(deviceKey, fileKey);
        byte[] second = initDecrypt(userKey, first);
        byte[] third = initCrypto(playerKey, second);

        return third;
    }

    /**
     * Verifies synchronization of player and server in terms of
     * encryption/decryption of keys
     *
     * @param received Cryptographic token received from player
     * @return Token to send to player
     */
    public byte[] playerSynchronize(byte[] received) {

        return initCrypto(userKey, received);
    }

    /**
     * Verifies synchronization of player and server in terms of
     * encryption/decryption of keys
     *
     * @return True if matches, false otherwise
     */
    public boolean synchronizationMatches() {
        return Arrays.equals(playerSynchronize(fileKey), deviceKey);
    }

    private byte[] initCrypto(byte[] key, byte[] text) {//byte[] keyData) {
        byte[] cipherText = null;
        SecretKey secretKey = null;
        String cipherMode = "";

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException ex) {
        }

        secretKey = new SecretKeySpec(key, "AES");

        cipherMode = "AES/CBC/NoPadding";

        Cipher c = null;
        try {
            c = Cipher.getInstance(cipherMode);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            System.err.println("Error ciphering file: no algorithm");
        }
        IvParameterSpec ivspec = new IvParameterSpec("keyinitializvect".getBytes());
        try {
            c.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
            System.err.println("Error ciphering file: invalidKey");
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CryptoHeader.class.getName()).log(Level.SEVERE, null, ex);
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

        }

        try {
            cipherText = c.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            System.err.println("Error ciphering file: block size / bad padding");
        }

        try {
            outputStream.write(cipherText);
            outputStream.flush();
        } catch (IOException ex) {
            System.err.println("Error ciphering file: i/o error");
        }
        return outputStream.toByteArray();

    }

    private byte[] initDecrypt(byte[] key, byte[] text) {
        byte[] cipherText = null;
        SecretKey secretKey = null;
        String cipherMode = "";

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException ex) {
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
            Logger.getLogger(CryptoHeader.class.getName()).log(Level.SEVERE, null, ex);
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
        }
        return outputStream.toByteArray();
    }
}

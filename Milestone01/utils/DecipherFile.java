package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
 * Creates an object to deciphering text using AES in CBC mode.
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class DecipherFile {

    private final byte[] text;
    private final byte[] key;
    private final byte[] iv;

    /**
     * Constructor of a decipher object
     *
     * @param text Text to cipher
     * @param key Key used for operation
     * @param iv Initialization vector for operation
     */
    public DecipherFile(byte[] text, byte[] key, byte[] iv) {
        this.text = text;
        this.key = key;
        this.iv = iv;
    }

    /**
     * Method to begin with decryption
     *
     * @return A byte array with deciphered bytes on a variable length depending
     * on text
     */
    public byte[] initDecrypt() {
        byte[] cipherText = null;
        SecretKey secretKey = null;
        String cipherMode = "";

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        secretKey = new SecretKeySpec(key, "AES");
        cipherMode = "AES/CBC/PKCS5Padding";

        Cipher c = null;
        try {
            c = Cipher.getInstance(cipherMode);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CipherFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        IvParameterSpec ivspec = new IvParameterSpec(iv);

        try {
            c.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(DecipherFile.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CipherFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outputStream.write(cipherText);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(CipherFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputStream.toByteArray();
    }

    /**
     * Returns the File Key used to cipher the text
     *
     * @return returns the File Key used to decipher the text
     */
    public byte[] getFK() {
        return key.clone();
    }
}

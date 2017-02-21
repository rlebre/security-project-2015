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
 * Creates an object to ciphering text using AES in CBC mode.
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class CipherFile {

    private final byte[] text;
    private final byte[] key;
    private final byte[] iv;

    /**
     * Constructor of a cipher object
     *
     * @param text Text to cipher
     * @param key Key used for operation
     * @param iv Initialization vector for operation
     */
    public CipherFile(String text, byte[] key, byte[] iv) {
        this.text = (text + "                  \0").getBytes();
        this.key = key;
        this.iv = iv;
    }

    /**
     * Method to begin with encryption
     *
     * @return A byte array with ciphered bytes on a variable length depending
     * on text
     */
    public byte[] initCrypto() {
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
            System.err.println("Error ciphering file: no algorithm");
        }
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        try {
            c.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException ex) {
            System.err.println("Error ciphering file: invalidKey");
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

    /**
     * Pads a string to a given block size for correct ciphering
     *
     * @param str Text to add padding
     * @return Text with padding
     */
    private String pad(String str) {
        StringBuilder padded = new StringBuilder(str);
        int reallength = str.length();

        while (reallength % 16 != 0) {
            padded.append('\0');
            reallength++;
        }
        return padded.toString();
    }

    /**
     * Returns the File Key used to cipher the text
     *
     * @return returns the File Key used to cipher the text
     */
    public byte[] getFK() {
        return key.clone();
    }
}

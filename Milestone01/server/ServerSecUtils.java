package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}

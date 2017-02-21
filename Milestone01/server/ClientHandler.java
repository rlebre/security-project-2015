package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import utils.BookReader;
import utils.BookReaderException;
import utils.CipherFile;

/**
 * Implements an IEDCS SSL client handler
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see Server
 * @see ServerSecUtils
 * @version 1.0
 */
public class ClientHandler extends Thread {

    private String usernameLogged;
    private String line = null;
    private BufferedReader socketIn = null;
    private InputStream streamIn = null;
    private OutputStream streamOut = null;
    private PrintWriter socketOut = null;
    private Socket socket = null;
    private DbConnection db = null;
    private ServerSecUtils utils;

    private final byte[] clientDeviceId;
    private final byte[] clientPlayerId;

    private final byte[] clientDeviceKey;
    private final byte[] clientPlayerKey;

    private byte[] fileKey;
    private CryptoHeader header;

    private Map<Thread, String> users;

    /**
     * Constructs a handler for a specific client
     *
     * @param s Socket reference received from main server thread
     * @param loggedUsers
     */
    public ClientHandler(SSLSocket s, Map<Thread, String> loggedUsers) {
        this.users = loggedUsers;
        usernameLogged = "";
        db = new DbConnection();
        this.socket = s;
        utils = new ServerSecUtils();

        try {
            streamIn = socket.getInputStream();
            socketIn = new BufferedReader(new InputStreamReader(streamIn));
            streamOut = socket.getOutputStream();
            socketOut = new PrintWriter(streamOut);
        } catch (IOException ex) {
            System.out.println("IO error in server thread");
        }

        // read first communication from server; it should be a hash calculated based on player and device IDs
        String ids = readResponse();

        String[] splited = ids.split(":");
        this.clientDeviceId = decodeString(splited[0]);
        this.clientPlayerId = decodeString(splited[1]);

        clientDeviceKey = utils.generateDeviceKey(clientDeviceId);
        clientPlayerKey = utils.generatePlayerKey(clientPlayerId);

        if (!db.checkPlayerExistance(clientPlayerKey)) {
            sendCommand("error:corrupted");
            closeConnections();
            this.closeConnections();
        } else {
            sendCommand("Player authorized");
            db.registerDevice(clientDeviceId, splited[2], splited[3], clientPlayerKey);
        }
    }

    /**
     * Runs current handler
     */
    @Override
    public void run() {
        try {
            line = socketIn.readLine().trim();
            while (!line.equals("QUIT")) {
                String[] command = line.split(":");
                if (command[0].equals("login") && command.length == 3) {
                    // format-> login:user:password

                    if (handleMessage(command[0], command[1], decodeString(command[2]), "")) {
                        //System.out.println("User logged: " + command[1]);

                        usernameLogged = command[1];
                        if (users.containsValue(usernameLogged)) {
                            socketOut.println("login:already_logged");
                            socketOut.flush();
                        } else {
                            users.put(this, usernameLogged);
                            socketOut.println("login:ok:" + command[1]);
                            socketOut.flush();
                        }
                        printServerState();
                    } else {
                        socketOut.println("login:refused");
                        socketOut.flush();
                    }
                } else if (command[0].equals("register") && command.length == 4) {
                    // format-> register:user:password:email
                    if (handleMessage(command[0], command[1], decodeString(command[2]), command[3])) {
                        sendCommand("register:ok:" + command[1]);
                    } else {
                        sendCommand("register:error");
                    }
                } else if (command[0].equals("catalog") && (command.length == 2 || command.length == 3)) {
                    switch (command[1]) {
                        case "full": {
                            ArrayList<String> array = db.getFullCatalog();
                            Iterator<String> it = array.iterator();
                            socketOut.println("catalog:begin_results");
                            socketOut.flush();
                            while (it.hasNext()) {
                                socketOut.println(it.next());
                                socketOut.flush();
                            }
                            socketOut.println("catalog:end_results");
                            socketOut.flush();
                            break;
                        }
                        case "search": {
                            ArrayList<String> array = db.searchCatalog(command[2]);
                            Iterator<String> it = array.iterator();
                            socketOut.println("catalog:begin_results");
                            socketOut.flush();
                            while (it.hasNext()) {
                                socketOut.println(it.next());
                                socketOut.flush();
                            }
                            socketOut.println("catalog:end_results");
                            socketOut.flush();
                            break;
                        }
                        case "user": {
                            ArrayList<String> array = db.searchUserCatalog(command[2]);
                            Iterator<String> it = array.iterator();
                            socketOut.println("catalog:begin_results");
                            socketOut.flush();
                            while (it.hasNext()) {
                                socketOut.println(it.next());
                                socketOut.flush();
                            }
                            socketOut.println("catalog:end_results");
                            socketOut.flush();
                            break;
                        }
                    }
                } else if (command[0].equals("purchase") && command.length == 3) {
                    if (!db.purchaseItem(command[1], command[2])) {
                        System.out.println("Error buying product: non existent or already bought.");
                        socketOut.println("purchase:error");
                    } else {
                        System.out.println("Sucessfully purchased.");
                        socketOut.println("purchase:success");
                    }
                } else if (command[0].equals("retrieve") && command.length == 3) {
                    boolean cenas = db.checkOnUserCart(command[2], Integer.parseInt(command[1]));
                    if (cenas) {
                        initRetrieve(command[1]);
                    }
                } else if (command[0].equals("settings"))// && (command.length == 3 || command.length == 5))
                {
                    if (command[1].equals("cart_list")) {
                        ArrayList<String> array = db.searchUserCatalog(command[2]);
                        Iterator<String> it = array.iterator();
                        socketOut.println("catalog:begin_results");
                        socketOut.flush();
                        while (it.hasNext()) {
                            socketOut.println(it.next());
                            socketOut.flush();
                        }
                        socketOut.println("catalog:end_results");
                        socketOut.flush();
                        break;
                    } else if (command[1].equals("change_email")) {
                        if (db.changeEmail(command[2], command[3], command[4])) {
                            socketOut.println("change_email:success");
                        } else {
                            socketOut.println("change_email:error");
                        }
                    } else if (command[1].equals("change_pw")) {
                        if (db.changePw(command[2], decodeString(command[3]), decodeString(command[4]))) {
                            socketOut.println("change_pw:success");
                            socketOut.flush();
                        } else {
                            socketOut.println("change_pw:error");
                            socketOut.flush();
                        }
                    }
                } else if (command[0].equals("logout")) {
                    socketOut.println("logout");
                    users.remove(this);
                    printServerState();
                } else {
                    socketOut.println(line);
                }
                socketOut.flush();

                line = socketIn.readLine();
            }
        } catch (IOException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client " + line + " terminated abruptly");
        } catch (NullPointerException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("Client " + line + " Closed");
        } catch (InterruptedException ex) {
        } finally {
            users.remove(this);
            closeConnections();
        }
    }

    private boolean handleMessage(String command, String user, byte[] passwd, String email) {
        switch (command) {
            case "login": {
                // VALIDAR TAMANHO DE ALTAMENTE[] 
                // formato: login:username:password
                if (utils.validateUser(user, "junk@junk.com")) {
                    return db.authorizeUser(user, passwd);
                }
            }
            case "register": {
                if (utils.validateUser(user, email)) {
                    return db.registerUser(user, passwd, email, utils.generateUserKey(user, passwd));
                }
            }
            default: {
                System.out.println("Corrupted command");
                break;
            }
        }

        return false;
    }

    private void closeConnections() {
        int a = 0;
        try {
            if (socketIn != null) {
                socketIn.close();
                a++;
                //System.out.println(" Socket Input Stream Closed");
            }

            if (socketOut != null) {
                socketOut.close();
                a++;
                //System.out.println("Socket Output Stream Closed");
            }

            if (socket != null) {
                socket.close();
                a++;
                //System.out.println("Socket Closed");
            }

            if (db != null) {
                db.closeConnection();
                a++;
                //System.out.println("Database Connection Closed");
            }

            if (a > 0) {
                System.out.println("\tSession " + this.getName() + " closed.");
            }
        } catch (IOException ie) {
            System.out.println("Socket Close Error");
        }
    }

    private void initRetrieve(String item) throws IOException, InterruptedException {
        fileKey = utils.generateFileKey();

        header = new CryptoHeader(clientDeviceKey, clientPlayerKey, fileKey, usernameLogged, db);

        System.out.println("Header: " + bytesToHex(header.getHeader()) + ", File: " + bytesToHex(fileKey) + ", Device: " + bytesToHex(clientDeviceKey) + ", Player: " + bytesToHex(clientPlayerKey));

        String filePath = db.getPath(item, usernameLogged);
        System.out.println(filePath);
        BookReader bookReader = null;

        try {
            bookReader = new BookReader(filePath);
        } catch (BookReaderException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        socketIn.readLine();
        socketOut.println("stream:begin_stream");
        socketOut.flush();

        socketOut.println("stream:key_tradeof");
        socketOut.flush();

        sendCommand(encodeBytes(header.getHeader()));

        byte[] teste = decodeString(readResponse());

        System.out.println("Bytes recebidos: " + bytesToHex(teste));

        sendCommand(encodeBytes(header.playerSynchronize(teste)));

        socketOut.println("stream:end_key_tradeof");
        socketOut.flush();
        socketOut.println("stream:page_break");
        socketOut.flush();

        while (bookReader.hasNextPage()) {
            if (socketIn.readLine().equals("stream:cancel_stream")) {
                break;
            }

            byte iv[] = new byte[16];
            System.arraycopy(utils.hash("A1"), 0, iv, 0, 16);
            String sendPage = bookReader.readNextPage();
            CipherFile cipher = new CipherFile(sendPage, fileKey, iv);
            byte[] encryptedPage = cipher.initCrypto();
            sendCommand(encodeBytes(encryptedPage));
            socketOut.println("stream:page_break");
            socketOut.flush();
            Thread.sleep(1);
        }

        socketOut.println("stream:end_stream");
        socketOut.flush();
    }

    private String encodeBytes(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    private byte[] decodeString(String text) {
        return Base64.getDecoder().decode(text);
    }

    private String readResponse() {
        String response = "";
        if (socketIn != null) {
            try {
                response = socketIn.readLine();
            } catch (IOException ex) {
            }
        } else {
            closeConnections();
            System.exit(0);
        }

        return response;
    }

    private void sendCommand(String cmd) {
        if (socketOut != null) {
            socketOut.println(cmd);
            socketOut.flush();
        } else {
            closeConnections();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    private void printServerState() {
        System.out.println("\nUsers currently connected: ");
        for (Map.Entry<Thread, String> entry : users.entrySet()) {
            System.out.println(entry.getKey().getId() + "\t" + entry.getKey().getName() + "\t" + entry.getValue());
        }
    }
}

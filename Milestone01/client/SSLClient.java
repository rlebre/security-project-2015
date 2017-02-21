package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import utils.DecipherFile;

/**
 * Implements an IEDCS SSL client
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see Client
 * @see ClientSecUtils
 * @see DecipherFile
 * @see BasicPrints
 * @version 1.0
 * @serial 5102794185910280346L
 */
public class SSLClient implements HandshakeCompletedListener {

    private final long playerId = 5102794185910280346L; // Player unique ID
    private final ClientSecUtils utils;                 // Client utilities
    private byte[] deviceKey;                           // device key computed in runtime
    private byte[] playerKey;                           // player key compuded in runtime
    private boolean isUserLogged = false;               // user logged on client indicator
    private String username;                            // username of user logged
    private SSLSocket socket = null;                    // SSL Socket reference
    private String line = null;                         // auxiliary string to perform actions in code
    private BufferedReader scKeyboard = null;
    private BufferedReader socketReader = null;
    private PrintWriter socketWriter = null;
    private OutputStream streamOut = null;
    private InputStream streamIn = null;
    private boolean handshakeCompleted = false;

    /**
     * Constructor of client socket
     */
    public SSLClient() {
        utils = new ClientSecUtils();
    }

    /**
     * Once SSL handshake is completed, this method is launched by mean of an
     * event. Initializes streams, writers and readers for communication with
     * server
     *
     * @param hce Event completion
     */
    @Override
    public void handshakeCompleted(HandshakeCompletedEvent hce) {
        scKeyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            streamIn = socket.getInputStream();
            socketReader = new BufferedReader(new InputStreamReader(streamIn));
            streamOut = socket.getOutputStream();
        } catch (IOException ex) {
            System.err.println("Error creating socket reader/writer");
        }

        socketWriter = new PrintWriter(streamOut);

        // send device and player information
        String deviceID = utils.getDeviceId("UUID");

        // construct of a string to send information of device and player to server
        String toSend = new String(Base64.getEncoder().encode(utils.hashPassword(deviceID)));
        toSend += ":" + new String(Base64.getEncoder().encode(utils.hashPassword("" + playerId)));
        toSend += ":" + TimeZone.getDefault().getDisplayName();
        toSend += ":" + System.getProperty("os.name");

        // format ->  device_id:player_id:location:operating_system
        sendCommand(toSend);

        if (readResponse().equals("error:corrupted")) {
            System.out.println("ERROR - IEDCS Player corrupted.\nImpossible to continue.");
            closeConnections();
            //System.exit(0);
        } else {
            System.out.println("Player authorized by server.");
        }

        // synchronize device and player keys with server
        deviceKey = utils.generateDeviceKey(utils.hashPassword(deviceID));
        playerKey = utils.generatePlayerKey(utils.hashPassword("" + playerId));
        handshakeCompleted = true;
    }

    /**
     * Main method to run SSL Client
     *
     * @param args Server host name / address
     * @throws UnknownHostException Host not found
     * @throws IOException Socket / Keyboard error
     * @throws InterruptedException Thread.sleep error
     */
    public void run(String args[]) throws UnknownHostException, IOException, InterruptedException {
        try {
            //  creates an address given machine name as argument
            InetAddress address = InetAddress.getByName(args[0]);

            //  loading of certificate on file keystore with the password 'seguranca2015'
            char[] passphrase = "seguranca2015".toCharArray();
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("mySrvKeystore"), passphrase);

            //  adding certificate to trusted certificates
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);

            //  initialization of TLS connection
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();

            context.init(null, trustManagers, null);

            SSLSocketFactory sf = context.getSocketFactory();

            //  creation of socket
            socket = (SSLSocket) sf.createSocket(address, Integer.parseInt(args[1]));
            socket.setUseClientMode(true);  // set client mode

            socket.addHandshakeCompletedListener(this);     // add event listener for handshake completion
            System.out.println("Starting handshaking...");
            socket.startHandshake();    // starting SSL handshaking

            // while handshaking is not completed, sleep current thread
            while (!handshakeCompleted) {
                sleep(3);
            }

            // feedback over connection
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        // while quit command is not performed, repeat whole life cycle
        do {
            line = "QUIT";
            isUserLogged = false;
            mainMenuInteractions();
            loginMenuInteractions();
        } while (!line.equals("QUIT"));
    }

    /**
     * Manage UI interactions. Given a menu, user will choose between given
     * items and current method will handle such choice
     */
    private void mainMenuInteractions() {
        String response = "";
        while (!response.equals("QUIT") && !isUserLogged) {
            BasicPrints.printMainMenu();

            try {
                response = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on main menu.");
            }

            try {
                int aux = Integer.parseInt(response);
                if (aux < 0 || aux > 3) {
                    System.out.print("\nInvalid option. Retry: ");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nError - invalid option.");
                continue;
            }

            switch (response) {
                case "0": {
                    response = "QUIT";
                    sendCommand("QUIT");

                    closeConnections();
                    break;
                }
                case "1": {
                    sendCommand("catalog:full");
                    break;
                }
                case "2": {
                    String[] c = loginUser();
                    sendCommand(c[0] + ":" + c[1] + ":" + encodeBytes(utils.hashPassword(c[2])));
                    break;
                }
                case "3": {
                    String[] c = registerUser();
                    sendCommand(c[0] + ":" + c[1] + ":" + encodeBytes(utils.hashPassword(c[2])) + ":" + c[3]);
                    break;
                }
                default: {
                    System.err.println("Corrupted command on main menu.");
                    break;
                }
            }

            String aux = handleResponse(readResponse());
            if (!aux.contains(":")) {
                System.out.println("Server Response : " + aux);
            }
        }
    }

    /**
     * Manage UI interactions. Once user is logged on and given a menu, user
     * will choose between given items and current method will handle such
     * choice
     */
    private void loginMenuInteractions() {
        String response = "";

        while (!response.equals("QUIT") && isUserLogged) {
            BasicPrints.printLoginMenu(username);

            try {
                response = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on user menu.");
            }

            // validating choice: it must be a number between 0 and 6
            try {
                int aux = Integer.parseInt(response);
                if (aux < 0 || aux > 6) {
                    System.out.print("\nInvalid option. Retry: ");
                    continue;       // if invalid option, repeat cycle
                }
            } catch (NumberFormatException e) {
                System.out.print("\nInvalid option. Retry: ");
                continue;       // if invalid option, repeat cycle
            }

            switch (response) {
                case "0": {
                    response = "QUIT";
                    sendCommand("QUIT");
                    break;
                }
                case "1": // retrieve full catalog
                {
                    sendCommand("catalog:full");
                    break;
                }
                case "2": // Search on catalog
                {
                    System.out.print("Keyword: ");
                    String keyword = "";
                    try {
                        keyword = scKeyboard.readLine();
                    } catch (IOException ex) {
                        System.err.println("Error getting input from keyboard on user menu.");
                    }
                    sendCommand("catalog:search:" + keyword);
                    break;
                }
                case "3": // Purchase item
                {
                    System.out.print("Item ID (0 - Cancel): ");
                    int id = 0;
                    try {
                        id = Integer.parseInt(scKeyboard.readLine());
                    } catch (IOException | NumberFormatException ex) {
                        System.err.println("Invalid item ID, please repeat.");
                    }

                    sendCommand("purchase:" + id + ":" + username);
                    break;
                }
                case "4": // retrieve item
                {
                    sendCommand("catalog:user:" + username);

                    response = readResponse();

                    response = retrieveItem(response);
                    if (response.equals("junk")) {
                        continue;
                    }
                    sendCommand(response + "\n");
                    break;
                }
                case "5": // settings
                {
                    settingsInteractions();
                    continue;
                }
                case "6": // logout
                {
                    username = "";
                    isUserLogged = false;
                    sendCommand("logout");
                    line = "logout";
                    break;
                }
                default: {
                    System.out.println("Corrupted command");
                    break;
                }
            }

            if (!response.equals("QUIT")) {
                response = readResponse();  // reading reply from server

                String aux = handleResponse(response);  // given a reply, handles next step

                if (!aux.contains(":")) {
                    System.out.println("Server Response : " + aux);
                }
            } else {
                closeConnections();
            }
        }
    }

    /**
     * Manage UI interactions. Once user is logged on and given a menu, user
     * will choose between given items and current method will handle such
     * choice
     */
    private void settingsInteractions() {
        String response = "";
        line = "";
        boolean back = false;   // variable used to exit settings menu
        while (!back && isUserLogged) {
            BasicPrints.printSettingsMenu(username);

            try {
                response = scKeyboard.readLine();

                int aux = Integer.parseInt(response);
                if (aux < 0 || aux > 4) {
                    System.out.print("\nInvalid option. Retry: ");
                    continue;
                }
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on settings menu.");
                continue;           // if error reading from keyboard, try again
            } catch (NumberFormatException e) {
                System.out.print("\nInvalid option. Retry: ");
                continue;       // if error on keyboard input, try again
            }

            switch (response) {
                case "0": {
                    response = "QUIT";
                    socketWriter.println(response);
                    socketWriter.flush();
                    back = true;
                    break;
                }
                case "1": // change email
                {
                    System.out.print("Actual email: ");
                    String prev = null, next = null;
                    try {
                        prev = scKeyboard.readLine();
                    } catch (IOException ex) {
                    }

                    System.out.print("New email: ");
                    try {
                        next = scKeyboard.readLine();
                    } catch (IOException ex) {
                    }

                    response = "settings:change_email:" + username + ":" + prev + ":" + next;
                    socketWriter.println(response);
                    socketWriter.flush();
                    break;
                }
                case "2": // change password
                {
                    System.out.print("Actual password: ");

                    String prev = null, next = null;
                    try {
                        prev = scKeyboard.readLine();
                    } catch (IOException ex) {
                        System.err.println("Error getting input from keyboard on settings menu.");
                    }

                    System.out.print("New password: ");
                    try {
                        next = scKeyboard.readLine();
                    } catch (IOException ex) {
                        System.err.println("Error getting input from keyboard on settings menu.");
                    }

                    socketWriter.println("settings:change_pw:" + username + ":" + encodeBytes(utils.hashPassword(prev)) + ":" + encodeBytes(utils.hashPassword(next)));
                    socketWriter.flush();

                    break;
                }
                case "3": {
                    socketWriter.println("settings:cart_list:" + username);
                    socketWriter.flush();
                    break;
                }
                case "4": {
                    back = true;
                    break;
                }
                default: {
                    System.out.println("Corrupted command");
                    break;
                }
            }
            if (!back) {
                if (!response.equals("QUIT")) {
                    response = readResponse();

                    String aux = handleResponse(response);
                    if (!aux.contains(":")) {
                        System.out.println("Server Response : " + aux);
                    }
                } else {
                    closeConnections();
                }
            }
        }
    }

    /**
     * Reads and validates login information. The validation is done by pattern
     * recognition and once not validated, it will ask for new attempting.
     * Validation will include SQL Injection defenses.
     *
     * @return String array containing information data to send to server for
     * authentication
     */
    private String[] loginUser() {
        // username must start with a letter or number followed by at least two numbers,
        // characters ou symbols
        Pattern userPattern = Pattern.compile("^[a-zA-Z0-9]{1}[a-zA-Z0-9,._-]{2,}");
        // password must start with a letter or number followed by at least two numbers,
        // characters ou symbols
        Pattern pwPattern = Pattern.compile("^[a-zA-Z0-9]{1}[a-zA-Z0-9,._-]{3,}");
        String user = "";
        String password = "";

        Matcher matcher = userPattern.matcher(user);
        while (!matcher.matches()) {
            System.out.print("\nUsername: ");

            try {
                user = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on login.");
            }
            matcher = userPattern.matcher(user);

            if (!matcher.matches()) {
                System.out.print("\nUsername invalido");
            }
        }

        // verification  of pattern matching
        matcher = pwPattern.matcher(password);
        while (!matcher.matches()) {
            System.out.print("Password: ");

            try {
                password = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on login.");
            }

            // verification  of pattern matching
            matcher = pwPattern.matcher(password);

            if (!matcher.matches()) {
                System.out.println("\nPassword invalida");
            }
        }

        return new String[]{"login", user, password};
    }

    /**
     * Reads and validates registration information. The validation is done by
     * pattern recognition and once not validated, it will ask for new
     * attempting. Validation will include SQL Injection defenses.
     *
     * @return String array containing information data to send to server for
     * registration
     */
    private String[] registerUser() {
        // username must start with a letter or number followed by at least two numbers,
        // characters ou symbols
        Pattern userPattern = Pattern.compile("^[a-zA-Z0-9]{1}[a-zA-Z0-9,._-]{2,}");

        // password must start with a letter or number followed by at least two numbers,
        // characters ou symbols
        Pattern pwPattern = Pattern.compile("^[a-zA-Z0-9]{1}[a-zA-Z0-9,._-]{3,}");

        // email must start with a letter or number followed by an @and at least two numbers,
        // characters ou symbols
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9]{1,}@{1}[a-zA-Z0-9]{1,}.[a-zA-Z]{1,4}");
        String user = "";
        String password = "";
        String email = "";

        // verification  of pattern matching
        Matcher matcher = userPattern.matcher(user);
        while (!matcher.matches()) {
            System.out.print("\nUsername: ");

            try {
                user = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on register.");
            }

            // verification  of pattern matching
            matcher = userPattern.matcher(user);
            if (!matcher.matches()) {
                System.err.println("Username invalido");
            }
        }

        // verification  of pattern matching
        matcher = pwPattern.matcher(password);
        while (!matcher.matches()) {
            System.out.print("Password: ");

            try {
                password = scKeyboard.readLine();
            } catch (IOException ex) {
                System.err.println("Error getting input from keyboard on register.");
            }

            // verification  of pattern matching
            matcher = pwPattern.matcher(password);
            if (!matcher.matches()) {
                System.err.println("Password invalida");
            }
        }

        // verification  of pattern matching
        matcher = emailPattern.matcher(email);
        while (!matcher.matches()) {
            System.out.print("Email: ");

            try {
                email = scKeyboard.readLine();
            } catch (IOException ex) {
            }

            // verification  of pattern matching
            matcher = emailPattern.matcher(email);
            if (!matcher.matches()) {
                System.err.println("E-mail invalido");
            }
        }

        return new String[]{"register", user, password, email};
    }

    /**
     * Handles server reply.
     *
     * @param response reply from server
     * @return Formatted output
     */
    private String handleResponse(String response) {
        String[] split = response.split(":");

        switch (split[0]) {
            case "login": {
                if (split[1].equals("refused")) {
                    response = "Error - connection refused.";
                    isUserLogged = false;
                    username = "";
                } else if (split[1].equals("already_logged")) {
                    response = "Error - user already logged in.";
                    isUserLogged = false;
                    username = "";
                } else {
                    response = "Logged in as " + split[2] + ".";
                    username = split[2];
                    isUserLogged = true;
                }
                break;
            }
            case "register": {
                if (split[1].equals("error")) {
                    response = "Error - registration failed.";
                } else {
                    response = "Registered " + split[2] + ".";
                }
                break;
            }
            case "catalog": {
                if (split[1].equals("begin_results")) {
                    String aux = "";
                    System.out.println();

                    do {
                        try {
                            aux = socketReader.readLine();
                        } catch (IOException ex) {
                        }
                        if (!aux.equals("catalog:end_results") && !aux.equals("catalog:begin_results")) {
                            System.out.println(aux);
                        }
                    } while (!aux.equals("catalog:end_results"));
                    System.out.println();

                }
                break;
            }
            case "stream": {
                if (split[1].equals("begin_stream")) {
                    String aux = readResponse();
                    if (!aux.equals("stream:key_tradeof")) {
                        break;
                    }
                    byte[] fileKey = handleHeader();
                    do {
                        try {
                            aux = socketReader.readLine();
                        } catch (IOException ex) {
                            System.err.println("Error getting input from keyboard on stream menu.");
                        }

                        if (aux.equals("stream:page_break")) {
                            try {
                                System.out.println("\nEnter to continue reading. To cancel streaming, type 'cancel' or '0'. ");
                                aux = scKeyboard.readLine();
                                System.out.println("\n\t--------------------------------------------------\n");
                                if (aux.equals("cancel") || aux.equals("0")) {
                                    socketWriter.println("stream:cancel_stream");
                                    socketWriter.flush();
                                } else {
                                    socketWriter.println("stream:next_page");
                                    socketWriter.flush();
                                    byte[] value = decodeString(readResponse());

                                    byte[] iv = new byte[16];
                                    System.arraycopy(utils.hashPassword("A1"), 0, iv, 0, 16);
                                    DecipherFile decrypted = new DecipherFile(value, fileKey, iv);
                                    System.out.write(decrypted.initDecrypt());
                                }

                            } catch (IOException ex) {
                            }
                        }
                    } while (!aux.equals("stream:end_stream"));
                }
                break;
            }
            case "purchase": {
                if (split[1].equals("success")) {
                    response = "Sucessfully purchased.";
                } else if (split[1].equals("error")) {
                    response = "Error buying product: non existent or already bought.";
                }
                break;
            }
            case "change_email": {
                if (split[1].equals("success")) {
                    response = "Email successfuly changed.";
                } else if (split[1].equals("error")) {
                    response = "Error changing email: inconsistent data.";
                }
                break;
            }
            case "change_pw": {
                if (split[1].equals("success")) {
                    response = "Password successfuly changed.";
                } else if (split[1].equals("error")) {
                    response = "Error changing password: inconsistent data.";
                }
                break;
            }
            case "logout": {
                response = "Logged out! ";
                break;
            }
            case "BACK": {
                response = "BACK";
                break;
            }
        }

        return response;
    }

    /**
     * Reads and handles the cryptographic header
     *
     * @return Returns the File Key that corresponds to the cryptographic header
     * acquired
     */
    private byte[] handleHeader() {
        // reads the full encrypted cryptographic header
        byte[] cryptHeader = decodeString(readResponse());

        // decrypts the header using the player key
        byte[] third = utils.decryptKey(playerKey, cryptHeader);

        // sends decoded data to server in order to cooperation
        sendCommand(encodeBytes(third));

        // receiving key decoded by server
        byte[] second = decodeString(readResponse());

        // computing file key with server interaction
        byte[] fileKey = utils.decryptKey(deviceKey, second);
        //System.out.println("Header: " + bytesToHex(cryptHeader) + ", File: " + bytesToHex(fileKey) + ", Device: " + bytesToHex(deviceKey) + ", Player: " + bytesToHex(playerKey));

        return fileKey;
    }

    /**
     * Given a list of files allowed, returns a string formatted to send to
     * server in order to start streaming
     *
     * @param response Header of list of files
     * @return Formatted string t send to server
     */
    private String retrieveItem(String response) {
        String toReturn = "";
        List<Integer> lista = new LinkedList<>();
        int item_id = -1;

        if (response.equals("catalog:begin_results")) {
            String aux = "";

            do {
                System.out.println(aux);
                try {
                    aux = socketReader.readLine();
                } catch (IOException ex) {
                }

                // pattern to find item ID
                Matcher matcher = Pattern.compile("\\d+").matcher(aux);
                if (matcher.find()) {
                    // once found, adds ID to the list
                    lista.add(Integer.valueOf(matcher.group()));
                }
            } while (!aux.equals("catalog:end_results"));

            System.out.println();
            do {
                System.out.print("Item ID (0 - Cancel): ");
                try {
                    toReturn = scKeyboard.readLine();
                } catch (IOException ex) {
                    System.err.println("Error getting input from keyboard on retrieving.");
                }

                item_id = -1;

                try {
                    item_id = Integer.parseInt(toReturn);
                } catch (NumberFormatException e) {
                    System.out.println("\nError - invalid option.");
                }

                // if item_id is not on the list, print error
                if (!lista.contains(item_id) && item_id != 0) {
                    System.out.println("Error - item not purchased yet. Please choose one of the list (0 - Cancel).");
                }
            } while (!lista.contains(item_id) && item_id != 0);

            // if all set, return string with all parameters
            toReturn = "retrieve:" + item_id + ":" + username;
        }
        if (item_id == 0 || item_id == -1) {
            toReturn = "junk";
        }

        return toReturn;
    }

    /**
     * Closes socket reader, writer, keyboard reader and the socket itself
     * connections
     */
    private void closeConnections() {
        try {
            if (socketReader != null) {
                socketReader.close();
            }
            if (socketWriter != null) {
                socketWriter.close();
            }
            if (scKeyboard != null) {
                scKeyboard.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connectionss");
        }
        System.out.println("Connection Closed");
        System.exit(0);
    }

    /**
     * Reads a message from socket (reads a line)
     *
     * @return String containing read data
     */
    private String readResponse() {
        String response = "";
        if (socketReader != null) {
            try {
                response = socketReader.readLine();
            } catch (IOException ex) {
            }
        } else {
            closeConnections();
        }

        return response;
    }

    /**
     * Sends a string over socket for server
     *
     * @param cmd String to send
     */
    private void sendCommand(String cmd) {
        socketWriter.println(cmd);
        socketWriter.flush();
    }

    /**
     * Encodes a byte array with base64
     *
     * @param bytes Bytes to encode
     * @return String with bytes encoded
     */
    private String encodeBytes(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    /**
     * Decode a base64 string to a byte array
     *
     * @param text String to decode
     * @return bytes of decoded string
     */
    private byte[] decodeString(String text) {
        return Base64.getDecoder().decode(text);
    }

    /**
     * Converts a byte array to a string in hexadecimal values representing each
     * byte of the array
     *
     * @param bytes Bytes to convert
     * @return String converted
     */
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
}

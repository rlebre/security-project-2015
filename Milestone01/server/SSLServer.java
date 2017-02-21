package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Implements an IEDCS SSL server
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see Server
 * @see ServerSecUtils
 * @see ClientHandler
 * @version 1.0
 */
public class SSLServer implements HandshakeCompletedListener {

    ArrayList<Thread> clientlist = new ArrayList<>();
    private Map<Thread, String> users = new HashMap<>();

    /**
     * Once SSL handshake is completed, this method is launched by mean of an
     * event. Initializes streams, writers and readers for communication with
     * server
     *
     * @param hce Event completion
     */
    @Override
    public void handshakeCompleted(HandshakeCompletedEvent hce) {
        ClientHandler st = new ClientHandler(hce.getSocket(), users);
        st.start();

        clientlist.add(st);
        System.out.print("\n\n\n\nConnection Established: ");
        System.out.println(hce.getSocket().toString());

        System.out.println("Players currently connected:");

        for (Thread thread : clientlist) {
            System.out.println(thread.getId() + "\t" + thread.getName() + "\t" + thread.getState() + "\t" + thread.getPriority());
        }
    }

    /**
     * Main method to run SSL Client
     *
     * @param port Server listening port
     * @throws KeyStoreException Key store error
     * @throws IOException Socket error
     * @throws NoSuchAlgorithmException X509 algorithm not found
     * @throws UnrecoverableKeyException Password for certificate error
     * @throws CertificateException Certificate corrupted
     * @throws KeyManagementException Key manager error
     */
    public void run(String port) throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyManagementException {

        char[] passphrase = "seguranca2015".toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("mySrvKeystore"), passphrase);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, passphrase);
        SSLContext context = SSLContext.getInstance("TLS");
        KeyManager[] keyManagers = kmf.getKeyManagers();

        context.init(keyManagers, null, null);

        SSLServerSocketFactory ssf = context.getServerSocketFactory();
        SSLSocket s = null;
        SSLServerSocket ss2 = null;
        System.out.println("Server Listening......");

        try {
            SSLServerSocketFactory sslserversocketfactory = context.getServerSocketFactory();
            ss2 = (SSLServerSocket) sslserversocketfactory.createServerSocket(Integer.parseInt(port)); // can also use static final PORT_NUM , when defined
        } catch (IOException e) {
            System.out.println("Server error");
        }

        ArrayList<SSLSocket> socketList = new ArrayList<>();

        while (true) {
            s = (SSLSocket) ss2.accept();
            s.addHandshakeCompletedListener(this);
            s.startHandshake();
        }
    }
}

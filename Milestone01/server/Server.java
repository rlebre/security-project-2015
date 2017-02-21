package server;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Implements an IEDCS Server
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see SSLServer
 * @version 1.0
 */
public class Server {

    /**
     * Main function to run IEDCS Server
     *
     * @param args Port of server host
     * @throws org.apache.commons.cli.ParseException Apache exception
     */
    public static void main(String args[]) throws ParseException {

        Options options = new Options();
        options.addOption("p", "port", true, "Listening port");
        options.addOption("h", "help", false, "Prints this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String parameter = "4445";

        if ((cmd.hasOption('i') || cmd.hasOption("ip"))) {
            // if just one argument~, it must be the IP
            parameter = cmd.getOptionValue("i");
        } else if ((cmd.hasOption('h') || cmd.hasOption("help"))) {
            help(options);
        }

        // instantion of a new socket handler
        SSLServer sslserver = new SSLServer();
        try {
            // setup of new client session
            sslserver.run(parameter);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyManagementException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Prints help to command line arguments
     *
     * @param options Available options on formatter
     */
    private static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String header = "Default: localhost:4445\n";
        String footer = "\nPlease report issues at {ruilebre,tomasrodrigues}@ua.pt";
        formatter.printHelp("java IEDCS_Server [options]", header, options, footer);
        System.exit(0);
    }
}

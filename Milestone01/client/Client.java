package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Implements an IEDCS Player
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see SSLClient
 * @version 1.0
 * @serial 5102794185910280346L
 */
public class Client {

    /**
     * Main function to run IEDCS Player
     *
     * @param args IP and Port of server host
     * @throws org.apache.commons.cli.ParseException Apache exception
     */
    public static void main(String args[]) throws ParseException {
        Options options = new Options();
        options.addOption("i", "ip", true, "IP of server host");
        options.addOption("p", "port", true, "Port of server host");
        options.addOption("h", "help", false, "Prints this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String[] parameters = {"localhost", "4445"};

        // if the two arguments is passed
        if ((cmd.hasOption('i') || cmd.hasOption("ip")) && (cmd.hasOption('p') || cmd.hasOption("port"))) {
            parameters[0] = cmd.getOptionValue("i");
            parameters[1] = cmd.getOptionValue("p");
        } else if ((cmd.hasOption('i') || cmd.hasOption("ip"))) {
            // if just one argument~, it must be the IP
            parameters[0] = cmd.getOptionValue("i");
        } else if ((cmd.hasOption('h') || cmd.hasOption("help"))) {
            help(options);
        }

        // instantion of a new socket handler
        SSLClient sslserver = new SSLClient();
        try {
            // setup of new client session
            sslserver.run(parameters);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
        formatter.printHelp("java IEDCS_Player [options]", header, options, footer);
        System.exit(0);
    }
}

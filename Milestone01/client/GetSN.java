package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Implements an unique identifier getter class
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see Client
 * @version 1.0
 */
public class GetSN {

    private String sn = null;
    private final String[] windowsUUID;
    private final String[] windowsSN;
    private final String markerUUID;
    private final String markerSN;
    private final boolean returnSN;

    /**
     * Creates an object to return computer unique identifier
     *
     * @param sn true to initialize serial number mode. false to initialize UUID
     * mode
     */
    public GetSN(boolean sn) {
        windowsUUID = new String[]{
            "wmic", "csproduct", "get", "UUID"
        };

        windowsSN = new String[]{
            "wmic", "bios", "get", "serialnumber"
        };

        markerSN = "Serial Number:";
        markerUUID = "UUID:";
        returnSN = sn;
    }

    /**
     * Returns Serial number given the parameter on constructor
     *
     * @return Serial number
     */
    public String getSN() {
        String os = System.getProperty("os.name");

        if (os.startsWith("Windows")) {
            if (returnSN) {
                return windowsGetSerialNumber(windowsSN, markerSN);
            } else {
                return windowsGetSerialNumber(windowsUUID, markerUUID);
            }
        } else if (os.startsWith("Linux")) {
            if (returnSN) {
                return linuxGetSerialNumber(markerSN);
            } else {
                return linuxGetSerialNumber(markerUUID);
            }
        } else if (os.startsWith("Mac")) {
            return macGetSerialNumber();
        } else {
            return "Unknown OS";
        }
    }

    private String windowsGetSerialNumber(String[] arg, String marker) {

        if (sn != null) {
            return sn;
        }

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(arg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(is);
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if (marker.replace(" ", "").replace(":", "").equals(next)) {
                    sn = sc.next().trim();
                    break;
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    private String linuxGetSerialNumber(String marker) {

        if (sn == null) {
            readDmidecode(marker);
        }
        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    private String macGetSerialNumber() {

        if (sn != null) {
            return sn;
        }

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[]{
                "/usr/sbin/system_profiler", "SPHardwareDataType"
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        String marker = "Serial Number";
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    private void readDmidecode(String marker) {

        String line = null;
        BufferedReader br = null;

        try {
            br = readLinuxCommand("dmidecode -t system");
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(marker)[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private BufferedReader readLinuxCommand(String command) {

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command.split(" "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BufferedReader(new InputStreamReader(is));
    }

}

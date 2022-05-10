package oshi.software.os.linux;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.software.os.linux.proc.OSVersionInfoEx;

public class LinuxOperatingSystem
implements OperatingSystem {
    private OperatingSystemVersion _version = null;
    private String _family = null;

    public String getFamily() {
        if (this._family == null) {
            Scanner in2;
            try {
                in2 = new Scanner(new FileReader("/etc/os-release"));
            }
            catch (FileNotFoundException e2) {
                return "";
            }
            in2.useDelimiter("\n");
            while (in2.hasNext()) {
                String[] splittedLine = in2.next().split("=");
                if (!splittedLine[0].equals("NAME")) continue;
                this._family = splittedLine[1].replaceAll("^\"|\"$", "");
                break;
            }
            in2.close();
        }
        return this._family;
    }

    public String getManufacturer() {
        return "GNU/Linux";
    }

    public OperatingSystemVersion getVersion() {
        if (this._version == null) {
            this._version = new OSVersionInfoEx();
        }
        return this._version;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.getManufacturer());
        sb2.append(" ");
        sb2.append(this.getFamily());
        sb2.append(" ");
        sb2.append(this.getVersion().toString());
        return sb2.toString();
    }
}


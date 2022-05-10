package oshi.software.os.linux.proc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import oshi.software.os.OperatingSystemVersion;

public class OSVersionInfoEx
implements OperatingSystemVersion {
    private String _version = null;
    private String _codeName = null;
    private String version = null;

    public OSVersionInfoEx() {
        Scanner in2 = null;
        try {
            in2 = new Scanner(new FileReader("/etc/os-release"));
        }
        catch (FileNotFoundException e2) {
            return;
        }
        in2.useDelimiter("\n");
        while (in2.hasNext()) {
            String[] splittedLine = in2.next().split("=");
            if (splittedLine[0].equals("VERSION_ID")) {
                this.setVersion(splittedLine[1].replaceAll("^\"|\"$", ""));
            }
            if (!splittedLine[0].equals("VERSION")) continue;
            splittedLine[1] = splittedLine[1].replaceAll("^\"|\"$", "");
            String[] split = splittedLine[1].split("[()]");
            if (split.length <= 1) {
                split = splittedLine[1].split(", ");
            }
            if (split.length > 1) {
                this.setCodeName(split[1]);
                continue;
            }
            this.setCodeName(splittedLine[1]);
        }
        in2.close();
    }

    public String getCodeName() {
        return this._codeName;
    }

    public String getVersion() {
        return this._version;
    }

    public void setCodeName(String _codeName) {
        this._codeName = _codeName;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String toString() {
        if (this.version == null) {
            this.version = this.getVersion() + " (" + this.getCodeName() + ")";
        }
        return this.version;
    }
}


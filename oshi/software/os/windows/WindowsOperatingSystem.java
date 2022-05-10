package oshi.software.os.windows;

import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.software.os.windows.nt.OSVersionInfoEx;

public class WindowsOperatingSystem
implements OperatingSystem {
    private OperatingSystemVersion _version = null;

    public OperatingSystemVersion getVersion() {
        if (this._version == null) {
            this._version = new OSVersionInfoEx();
        }
        return this._version;
    }

    public String getFamily() {
        return "Windows";
    }

    public String getManufacturer() {
        return "Microsoft";
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


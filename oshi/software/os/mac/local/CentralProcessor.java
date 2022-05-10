package oshi.software.os.mac.local;

import java.util.ArrayList;
import oshi.hardware.Processor;
import oshi.util.ExecutingCommand;

public class CentralProcessor
implements Processor {
    private String _vendor;
    private String _name;
    private String _identifier = null;
    private String _stepping;
    private String _model;
    private String _family;
    private Boolean _cpu64;

    public String getVendor() {
        if (this._vendor == null) {
            this._vendor = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.vendor");
        }
        return this._vendor;
    }

    public void setVendor(String vendor) {
        this._vendor = vendor;
    }

    public String getName() {
        if (this._name == null) {
            this._name = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.brand_string");
        }
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getIdentifier() {
        if (this._identifier == null) {
            StringBuilder sb2 = new StringBuilder();
            if (this.getVendor().contentEquals("GenuineIntel")) {
                sb2.append(this.isCpu64bit() ? "Intel64" : "x86");
            } else {
                sb2.append(this.getVendor());
            }
            sb2.append(" Family ");
            sb2.append(this.getFamily());
            sb2.append(" Model ");
            sb2.append(this.getModel());
            sb2.append(" Stepping ");
            sb2.append(this.getStepping());
            this._identifier = sb2.toString();
        }
        return this._identifier;
    }

    public void setIdentifier(String identifier) {
        this._identifier = identifier;
    }

    public boolean isCpu64bit() {
        if (this._cpu64 == null) {
            this._cpu64 = ExecutingCommand.getFirstAnswer("sysctl -n hw.cpu64bit_capable").equals("1");
        }
        return this._cpu64;
    }

    public void setCpu64(boolean cpu64) {
        this._cpu64 = cpu64;
    }

    public String getStepping() {
        if (this._stepping == null) {
            this._stepping = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.stepping");
        }
        return this._stepping;
    }

    public void setStepping(String _stepping) {
        this._stepping = _stepping;
    }

    public String getModel() {
        if (this._model == null) {
            this._model = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.model");
        }
        return this._model;
    }

    public void setModel(String _model) {
        this._model = _model;
    }

    public String getFamily() {
        if (this._family == null) {
            this._family = ExecutingCommand.getFirstAnswer("sysctl -n machdep.cpu.family");
        }
        return this._family;
    }

    public void setFamily(String _family) {
        this._family = _family;
    }

    public float getLoad() {
        ArrayList<String> topResult = ExecutingCommand.runNative("top -l 1 -R -F -n1");
        String[] idle = topResult.get(3).split(" ");
        return 100.0f - Float.valueOf(idle[6].replace("%", "")).floatValue();
    }

    public String toString() {
        return this.getName();
    }
}


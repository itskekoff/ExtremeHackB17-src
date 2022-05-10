package ShwepSS.B17.cg.settings;

import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Module;
import java.util.ArrayList;

public class SettingsManager {
    private static ArrayList<Setting> settings;

    public SettingsManager() {
        settings = new ArrayList();
    }

    public void rSetting(Setting in2) {
        settings.add(in2);
    }

    public static ArrayList<Setting> getSettings() {
        return settings;
    }

    public ArrayList<Setting> getSettingsByMod(Module mod) {
        ArrayList<Setting> out = new ArrayList<Setting>();
        for (Setting s2 : SettingsManager.getSettings()) {
            if (!s2.getModule().equals(mod)) continue;
            out.add(s2);
        }
        if (out.isEmpty()) {
            return null;
        }
        return out;
    }

    public Setting getSettingByName(String name) {
        for (Setting set : SettingsManager.getSettings()) {
            if (!set.getName().equalsIgnoreCase(name)) continue;
            return set;
        }
        return null;
    }
}


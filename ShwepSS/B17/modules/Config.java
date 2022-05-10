package ShwepSS.B17.modules;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.cg.settings.SettingsManager;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class Config {
    public File dir;
    public File configs;
    public File dataFile;

    public Config() {
        this.dir = new File(Minecraft.getMinecraft().mcDataDir, "ExtremeHack");
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        this.dataFile = new File(this.dir, "config.txt");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            }
            catch (IOException var2) {
                var2.printStackTrace();
            }
        }
    }

    public void save() {
        ArrayList<String> toSave = new ArrayList<String>();
        for (Module mod : ModuleManager.getModules()) {
            toSave.add("Feature:" + mod.getName() + ":" + mod.isEnabled() + ":" + mod.getKey());
        }
        SettingsManager var10000 = ExtremeHack.instance.getSetmgr();
        for (Setting set : SettingsManager.getSettings()) {
            if (set.isCheck()) {
                toSave.add("Setting:" + set.getName() + ":" + set.getModule().getName() + ":" + set.getValue());
            }
            if (set.isCombo()) {
                toSave.add("Setting:" + set.getName() + ":" + set.getModule().getName() + ":" + set.getValString());
            }
            if (!set.isSlider()) continue;
            toSave.add("Setting:" + set.getName() + ":" + set.getModule().getName() + ":" + set.getValFloat());
        }
        try {
            PrintWriter pw = new PrintWriter(this.dataFile);
            for (String str : toSave) {
                pw.println(str);
            }
            pw.close();
        }
        catch (FileNotFoundException var5) {
            var5.printStackTrace();
        }
    }

    public void load() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String s2 = reader.readLine();
            while (s2 != null) {
                lines.add(s2);
                s2 = reader.readLine();
            }
            reader.close();
            for (String s1 : lines) {
                Setting set;
                Module m2;
                String[] args = s1.split(":");
                if (s1.toLowerCase().startsWith("feature:")) {
                    m2 = ModuleManager.getModuleByName(args[1]);
                    if (m2 == null) continue;
                    if (!m2.getName().contains("Config") && !m2.enabled && Boolean.parseBoolean(args[2])) {
                        m2.setEnabled(Boolean.parseBoolean(args[2]));
                    }
                    m2.setKey(Integer.parseInt(args[3]));
                    continue;
                }
                if (!s1.toLowerCase().startsWith("setting:") || (m2 = ModuleManager.getModuleByName(args[2])) == null || (set = ExtremeHack.instance.getSetmgr().getSettingByName(args[1])) == null) continue;
                if (set.isCheck()) {
                    set.setValue(Boolean.parseBoolean(args[3]));
                }
                if (set.isCombo()) {
                    set.setValString(args[3]);
                }
                if (!set.isSlider()) continue;
                set.setValDouble(Double.parseDouble(args[3]));
                set.setValDouble(Float.parseFloat(args[3]));
            }
        }
        catch (Exception var7) {
            var7.printStackTrace();
        }
    }
}


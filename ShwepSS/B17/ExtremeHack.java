package ShwepSS.B17;

import ShwepSS.B17.Utils.EnumProxyType;
import ShwepSS.B17.cg.ClickGuiScreen;
import ShwepSS.B17.cg.settings.SettingsManager;
import ShwepSS.B17.modules.Config;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;
import ShwepSS.B17.modules.hacks.bot.ProxyManager;
import java.io.File;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class ExtremeHack {
    public static Minecraft mc;
    public static ModuleManager manager;
    public static ExtremeHack instance;
    public SettingsManager setmgr;
    public static Config config;
    public ClickGuiScreen cg;
    public static String proxyIP;
    public EnumProxyType proxyType = EnumProxyType.NONE;
    public static boolean shkilo;
    public static ArrayList<String> shkiloips;

    static {
        proxyIP = "127.0.0.1";
        shkilo = false;
        shkiloips = new ArrayList();
    }

    public ExtremeHack(Minecraft minecraft) {
        File file = new File("webpanel.txt");
        this.setSetmgr(new SettingsManager());
        instance = this;
        mc = minecraft;
        manager = new ModuleManager();
        manager.loadMods(ExtremeHack.manager.mods);
        this.autoStart();
        System.out.println("EHack b17 Started! loaded modules: " + ModuleManager.modules.size());
        ProxyManager.downloadFile();
        ProxyManager.loadProxiesFromFile();
    }

    public void autoStart() {
        ModuleManager.getModuleByName("HUD").toggle();
        ModuleManager.getModuleByName("InvWalk").toggle();
        ModuleManager.getModuleByName("AutoReconnect").toggle();
        ModuleManager.getModuleByName("\u041a\u043c\u0434 \u0447\u0438\u0442\u0430").toggle();
        ModuleManager.getModuleByName("ItemESP").toggle();
        ModuleManager.getModuleByName("BlockHighlight").toggle();
        ModuleManager.getModuleByName("ChinaHats").toggle();
    }

    public static void onKeyPress(int keyCode) {
        try {
            for (Module module : ModuleManager.getModules()) {
                if (module.key != keyCode) continue;
                module.toggle();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static final ModuleManager getManager() {
        return manager;
    }

    public EnumProxyType getProxyType() {
        return this.proxyType;
    }

    public SettingsManager getSetmgr() {
        return this.setmgr;
    }

    public void setSetmgr(SettingsManager setmgr) {
        this.setmgr = setmgr;
    }
}


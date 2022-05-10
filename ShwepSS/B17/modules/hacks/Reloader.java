package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.ClickGuiScreen;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.cg.settings.SettingsManager;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;

public class Reloader
extends Module {
    private File moduleFolder;

    public Reloader() {
        super("Reloader", "\u0420\u0435\u043b\u043e\u0430\u0434 \u0432\u0441\u0435\u0445 \u043c\u043e\u0434\u0443\u043b\u0435\u0439", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        for (Module mod : ExtremeHack.manager.getEnabledModules()) {
            if (mod.getName().contains("Reloader")) continue;
            mod.onDisable();
            ChatUtils.emessage("\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d \u043c\u043e\u0434\u0443\u043b\u044c: " + mod.getName());
        }
        Minecraft.getMinecraft().displayGuiScreen(null);
        ExtremeHack.instance.setmgr = new SettingsManager();
        ModuleManager.modules.clear();
        ChatUtils.emessage("\u0423\u0434\u0430\u043b\u0435\u043d\u044b \u0432\u0441\u0435 \u043c\u043e\u0434\u0443\u043b\u0438...");
        ExtremeHack.manager = new ModuleManager();
        ExtremeHack.manager.loadMods(ExtremeHack.manager.mods);
        ModuleManager.modules.sort(Comparator.comparingInt(m1 -> FontUtil.elegant_16.getStringWidth(((Module)m1).name)).reversed());
        ExtremeHack.instance.cg = new ClickGuiScreen();
        ChatUtils.emessage("\u041d\u043e\u0432\u044b\u0435 \u043c\u043e\u0434\u0443\u043b\u0438 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u044b!");
        ExtremeHack.instance.autoStart();
        ChatUtils.emessage("\u0410\u0432\u0442\u043e\u0421\u0442\u0430\u0440\u0442 \u043c\u043e\u0434\u0443\u043b\u0435\u0439...");
        for (Module module : ModuleManager.getModules()) {
        }
        ChatUtils.emessage("\u0420\u0430\u0431\u043e\u0442\u0430 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430!");
        Minecraft.getMinecraft().displayGuiScreen(ExtremeHack.instance.cg);
        this.toggle();
    }

    private Module[] loadExternalModules(File[] moduleJars) {
        ArrayList<Module> moduleList = new ArrayList<Module>();
        Iterator<Module> it2 = ServiceLoader.load(Module.class, this.getPluginClassLoader(moduleJars)).iterator();
        while (it2.hasNext()) {
            try {
                moduleList.add(it2.next());
            }
            catch (Exception x2) {
                x2.printStackTrace();
            }
        }
        return moduleList.toArray(new Module[moduleList.size()]);
    }

    private ClassLoader getPluginClassLoader(File[] jarFiles) {
        Function<File, URL> toUrl = file -> {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException e2) {
                e2.printStackTrace();
                return null;
            }
        };
        return URLClassLoader.newInstance(Arrays.stream(jarFiles).map(toUrl).collect(Collectors.toList()).toArray(new URL[jarFiles.length]));
    }
}


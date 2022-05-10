package ShwepSS.B17.modules;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.AntiBan;
import ShwepSS.B17.modules.hacks.AntiVelocity;
import ShwepSS.B17.modules.hacks.AuthMeBrute;
import ShwepSS.B17.modules.hacks.AutoArmor;
import ShwepSS.B17.modules.hacks.AutoGapple;
import ShwepSS.B17.modules.hacks.AutoLogin;
import ShwepSS.B17.modules.hacks.AutoReconnect;
import ShwepSS.B17.modules.hacks.AutoSprint;
import ShwepSS.B17.modules.hacks.AutoUnban;
import ShwepSS.B17.modules.hacks.BedFucker;
import ShwepSS.B17.modules.hacks.BlockHighlight;
import ShwepSS.B17.modules.hacks.Carusel;
import ShwepSS.B17.modules.hacks.ChatBypass;
import ShwepSS.B17.modules.hacks.ChestStealer;
import ShwepSS.B17.modules.hacks.ChinaHat;
import ShwepSS.B17.modules.hacks.ChitKrutiklka;
import ShwepSS.B17.modules.hacks.Commands;
import ShwepSS.B17.modules.hacks.CopyPlayer;
import ShwepSS.B17.modules.hacks.CrasherVersions;
import ShwepSS.B17.modules.hacks.Dropper;
import ShwepSS.B17.modules.hacks.ESPBox;
import ShwepSS.B17.modules.hacks.EntityFlySpeed;
import ShwepSS.B17.modules.hacks.Esp;
import ShwepSS.B17.modules.hacks.Ewkakiz_ESP;
import ShwepSS.B17.modules.hacks.ExternalChat;
import ShwepSS.B17.modules.hacks.ExtremeAura;
import ShwepSS.B17.modules.hacks.FastPlace;
import ShwepSS.B17.modules.hacks.FastUse;
import ShwepSS.B17.modules.hacks.Flexer;
import ShwepSS.B17.modules.hacks.FreeCam;
import ShwepSS.B17.modules.hacks.FullBright;
import ShwepSS.B17.modules.hacks.Girlyanda;
import ShwepSS.B17.modules.hacks.HUD;
import ShwepSS.B17.modules.hacks.HideNHack;
import ShwepSS.B17.modules.hacks.InvWalk;
import ShwepSS.B17.modules.hacks.ItemESP;
import ShwepSS.B17.modules.hacks.KrazhaItems;
import ShwepSS.B17.modules.hacks.Lift;
import ShwepSS.B17.modules.hacks.Ludojop;
import ShwepSS.B17.modules.hacks.MPEScript;
import ShwepSS.B17.modules.hacks.Masturbate;
import ShwepSS.B17.modules.hacks.MegaStrafe;
import ShwepSS.B17.modules.hacks.MiksGame;
import ShwepSS.B17.modules.hacks.MileyCyrus;
import ShwepSS.B17.modules.hacks.MurderBug;
import ShwepSS.B17.modules.hacks.MurderHack;
import ShwepSS.B17.modules.hacks.NameTags;
import ShwepSS.B17.modules.hacks.NoFall;
import ShwepSS.B17.modules.hacks.NoHurtCam;
import ShwepSS.B17.modules.hacks.NoOpenGui;
import ShwepSS.B17.modules.hacks.Nuker;
import ShwepSS.B17.modules.hacks.OldAura;
import ShwepSS.B17.modules.hacks.OldSpam;
import ShwepSS.B17.modules.hacks.OpenGui;
import ShwepSS.B17.modules.hacks.Parkour;
import ShwepSS.B17.modules.hacks.PlayerListPin;
import ShwepSS.B17.modules.hacks.PlugFinder;
import ShwepSS.B17.modules.hacks.RandomSwitch;
import ShwepSS.B17.modules.hacks.RapidBow;
import ShwepSS.B17.modules.hacks.Reloader;
import ShwepSS.B17.modules.hacks.RepeatPackets;
import ShwepSS.B17.modules.hacks.SafeWalk;
import ShwepSS.B17.modules.hacks.ShulkerViewer;
import ShwepSS.B17.modules.hacks.SimpleAura;
import ShwepSS.B17.modules.hacks.Slezhka;
import ShwepSS.B17.modules.hacks.SpeedHack;
import ShwepSS.B17.modules.hacks.Spider;
import ShwepSS.B17.modules.hacks.StackItem;
import ShwepSS.B17.modules.hacks.StorageESP;
import ShwepSS.B17.modules.hacks.TargetHud;
import ShwepSS.B17.modules.hacks.TargetRound;
import ShwepSS.B17.modules.hacks.TargetStrafe;
import ShwepSS.B17.modules.hacks.Tracers;
import ShwepSS.B17.modules.hacks.Trajectories;
import ShwepSS.B17.modules.hacks.WebChat;
import ShwepSS.B17.modules.hacks.WritePackets;
import ShwepSS.B17.modules.hacks.bot.Joiner;
import ShwepSS.B17.modules.hacks.bot.JoinerFollow;
import ShwepSS.B17.modules.hacks.bot.JoinerSpam;
import ShwepSS.B17.modules.hacks.bot.OneBotSpam;
import ShwepSS.B17.modules.hacks.bot.OneBotsFollow;
import ShwepSS.B17.modules.hacks.bot.OneBotsMother;
import ShwepSS.B17.modules.hacks.bot.b16AutoReg;
import ShwepSS.B17.modules.hacks.bot.b16Follow;
import ShwepSS.B17.modules.hacks.bot.b16Kilka;
import ShwepSS.B17.modules.hacks.bot.b16OnChat;
import ShwepSS.B17.modules.hacks.bot.b16Proxy;
import ShwepSS.B17.modules.hacks.themes.BlueTheme;
import ShwepSS.B17.modules.hacks.themes.ConfGui;
import ShwepSS.B17.modules.hacks.themes.ExtremeTheme;
import ShwepSS.B17.modules.hacks.themes.GreenTheme;
import ShwepSS.B17.modules.hacks.themes.QuadTheme;
import ShwepSS.B17.modules.hacks.themes.Rainbow;
import ShwepSS.B17.modules.hacks.themes.RedTheme;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    public static ArrayList<Module> modules = new ArrayList();
    public Module[] mods = new Module[]{new AutoUnban(), new Lift(), new FastPlace(), new RandomSwitch(), new RapidBow(), new AntiVelocity(), new HUD(), new Esp(), new OpenGui(), new Spider(), new SimpleAura(), new Joiner(), new InvWalk(), new PlayerListPin(), new FullBright(), new Ludojop(), new JoinerSpam(), new JoinerFollow(), new ExtremeAura(), new Girlyanda(), new AutoArmor(), new ChitKrutiklka(), new TargetHud(), new MurderHack(), new NoHurtCam(), new TargetStrafe(), new SpeedHack(), new ExternalChat(), new Parkour(), new Flexer(), new NoOpenGui(), new NoFall(), new MegaStrafe(), new Reloader(), new BlueTheme(), new ExtremeTheme(), new RedTheme(), new GreenTheme(), new Rainbow(), new ChestStealer(), new Nuker(), new MurderBug(), new StackItem(), new BedFucker(), new Dropper(), new CrasherVersions(), new AuthMeBrute(), new AutoReconnect(), new FastUse(), new AutoSprint(), new Masturbate(), new Tracers(), new MileyCyrus(), new TargetRound(), new ShulkerViewer(), new StorageESP(), new ItemESP(), new ESPBox(), new KrazhaItems(), new OneBotsFollow(), new OneBotsMother(), new WritePackets(), new RepeatPackets(), new Carusel(), new MPEScript(), new OldSpam(), new QuadTheme(), new ConfGui(), new Trajectories(), new AutoGapple(), new NameTags(), new CopyPlayer(), new WebChat(), new SafeWalk(), new PlugFinder(), new Commands(), new Slezhka(), new EntityFlySpeed(), new BlockHighlight(), new HideNHack(), new ChatBypass(), new AutoLogin(), new AntiBan(), new FreeCam(), new ChinaHat(), new Ewkakiz_ESP(), new MiksGame(), new b16AutoReg(), new b16Kilka(), new b16Follow(), new b16OnChat(), new b16Proxy(), new OneBotSpam(), new OldAura()};

    public void loadMods(Module[] mod) {
        Module[] arrmodule = this.mods;
        int n2 = this.mods.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            Module m2 = arrmodule[i2];
            modules.add(m2);
        }
    }

    public static final Module getModuleByName(String par1Str) {
        for (Module module : ModuleManager.getModules()) {
            if (!module.getName().equalsIgnoreCase(par1Str)) continue;
            return module;
        }
        return null;
    }

    public static final ArrayList<Module> getModules() {
        return modules;
    }

    public CopyOnWriteArrayList<Module> getEnabledModules() {
        CopyOnWriteArrayList<Module> enabledModules = new CopyOnWriteArrayList<Module>();
        for (Module m2 : ModuleManager.getModules()) {
            if (!m2.isEnabled()) continue;
            enabledModules.add(m2);
        }
        return enabledModules;
    }

    public Module[] getModulesInCategory(Category category) {
        return (Module[])modules.stream().filter(module -> module.getCategory() == category).toArray(Module[]::new);
    }
}


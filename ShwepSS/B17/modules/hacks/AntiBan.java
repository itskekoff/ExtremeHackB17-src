package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;

public class AntiBan
extends Module {
    public AntiBan() {
        super("\u0410\u043d\u0442\u0438\u0411\u0430\u043d", "\u0412\u044b\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u043e\u043f\u0430\u0441\u043d\u044b\u0435 \u043c\u043e\u0434\u0443\u043b\u0438", 0, Category.Tesla, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("SimpleAura").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("SimpleAura").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("ExtremeAura").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("ExtremeAura").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("AntiKnockBack").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("AntiKnockBack").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("TargetStrafe").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("TargetStrafe").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("MegaStrafe").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("MegaStrafe").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("RapidBow").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("RapidBow").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("Dropper").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("Dropper").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("RepeatPackets").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("RepeatPackets").toggle();
        }
        ExtremeHack.getManager();
        if (ModuleManager.getModuleByName("MegaStrafe").isEnabled()) {
            ExtremeHack.getManager();
            ModuleManager.getModuleByName("MegaStrafe").toggle();
        }
    }

    @Override
    public void onDisable() {
    }
}


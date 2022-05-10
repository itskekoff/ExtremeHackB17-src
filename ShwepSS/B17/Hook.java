package ShwepSS.B17;

import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;

public class Hook {
    public static final void onTickIngame() {
        for (Module m2 : ModuleManager.getModules()) {
            if (!m2.isEnabled()) continue;
            m2.onTick();
        }
    }

    public static final void onRenderIngame() {
        for (Module m2 : ModuleManager.getModules()) {
            if (!m2.isEnabled()) continue;
            m2.onRender();
        }
    }

    public static final void onClickBlock(int x2, int y2, int z2) {
        for (Module m2 : ModuleManager.getModules()) {
            if (!m2.isEnabled()) continue;
            m2.onClickBlock(x2, y2, z2);
        }
    }

    public static final int onRightClickDelayTimer(int i2) {
        for (Module m2 : ModuleManager.getModules()) {
            if (!m2.enabled || m2.onRightClickDelayTimer(i2) == i2) continue;
            return m2.onRightClickDelayTimer(i2);
        }
        return i2;
    }
}


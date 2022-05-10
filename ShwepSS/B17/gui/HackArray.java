package ShwepSS.B17.gui;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;
import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class HackArray {
    public static void renderArrayListGui() {
        ScaledResolution sr2 = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        ExtremeHack.getManager();
        ModuleManager.modules.sort(Comparator.comparingInt(m2 -> Minecraft.getMinecraft().fontRendererObj.getStringWidth(((Module)m2).name)).reversed());
        int count = 0;
        for (Module m3 : ExtremeHack.getManager().getEnabledModules()) {
            double offset = count * (fr.FONT_HEIGHT + 6);
            if (m3.slider != mc.fontRendererObj.getStringWidth(m3.getName())) {
                ++m3.slider;
            }
            if (!m3.shown) continue;
            Gui.drawRect(sr2.getScaledWidth() - mc.fontRendererObj.getStringWidth(m3.getName()) - 8, count * (fr.FONT_HEIGHT + 6), sr2.getScaledWidth(), 6 + fr.FONT_HEIGHT + count * (fr.FONT_HEIGHT + 6), -1156246251);
            Gui.drawRect(sr2.getScaledWidth() - mc.fontRendererObj.getStringWidth(m3.getName()) - 12, count * (fr.FONT_HEIGHT + 6), sr2.getScaledWidth() - mc.fontRendererObj.getStringWidth(m3.name) - 8, 6 + fr.FONT_HEIGHT + count * (fr.FONT_HEIGHT + 6), HackConfigs.ThemeColor);
            fr.drawStringWithShadow(m3.name, sr2.getScaledWidth() - m3.slider - 5, 4 + count * (fr.FONT_HEIGHT + 6), HackConfigs.ThemeColor);
            ++count;
        }
    }
}


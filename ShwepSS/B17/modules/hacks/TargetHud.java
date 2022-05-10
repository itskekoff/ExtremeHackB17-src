package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TargetHud
extends Module {
    public TargetHud() {
        super("TargetHUD", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0441\u0442\u0430\u0442\u044b \u043c\u0443\u0434\u0438\u043b\u044b", 19, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr2 = new ScaledResolution(mc);
        try {
            GuiRenderUtils.drawBorderedRect2(sr2.getScaledWidth() / 2 + 50, sr2.getScaledHeight() / 3, sr2.getScaledWidth() / 2 + 200, sr2.getScaledHeight() / 2 - 40, 1.4f, HackConfigs.ThemeColor, new Color(26, 30, 27).getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnable(3042);
            mc.getConnection().getPlayerInfo(HackConfigs.Mudila.getUniqueID());
            if (NetworkPlayerInfo.responseTime < 0) {
                return;
            }
            if (HackConfigs.Mudila == null) {
                return;
            }
            if (HackConfigs.Mudila.getUniqueID() == null) {
                return;
            }
            Gui.drawScaledCustomSizeModalRect(sr2.getScaledWidth() / 2 + 53, sr2.getScaledHeight() / 3 + 3, 8.0f, 8.0f, 8, 8, 37.0, 37.0, 64.0f, 64.0f);
            this.drawHead(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(HackConfigs.Mudila.getUniqueID()).getLocationSkin(), sr2.getScaledWidth() / 2 + 53, sr2.getScaledHeight() / 3 + 3);
            FontUtil.roboto_16.drawCenteredStringWithOutline("Nick -> " + HackConfigs.Mudila.getName(), sr2.getScaledWidth() / 2 + 125, sr2.getScaledHeight() / 3 + 5, HackConfigs.ThemeColor);
            FontUtil.roboto_16.drawCenteredStringWithOutline("Health -> " + HackConfigs.Mudila.getHealth() / 2.0f, sr2.getScaledWidth() / 2 + 125, sr2.getScaledHeight() / 3 + 15, HackConfigs.ThemeColor);
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    public void drawHead(ResourceLocation skin, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8.0f, 8.0f, 8, 8, 37.0, 37.0, 64.0f, 64.0f);
        GL11.glPopMatrix();
    }
}


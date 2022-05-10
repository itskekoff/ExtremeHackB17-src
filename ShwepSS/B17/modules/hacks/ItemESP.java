package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;

public class ItemESP
extends Module {
    public static boolean isEnabled;

    public ItemESP() {
        super("ItemESP", "\u043f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0434\u0440\u043e\u043f", 0, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        isEnabled = true;
    }

    @Override
    public void onDisable() {
        isEnabled = false;
        EventManager.unregister(this);
    }

    @EventTarget
    public void onRender(Event3DRender e2) {
        Minecraft mc = Minecraft.getMinecraft();
        for (Object o2 : mc.world.loadedEntityList) {
            if (!(o2 instanceof EntityItem)) continue;
            EntityItem item = (EntityItem)o2;
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)((float)item.lastTickPosX) + (item.posX - item.lastTickPosX) * (double)Minecraft.getMinecraft().timer.field_194147_b - RenderManager.renderPosX, item.lastTickPosY + (item.posY - item.lastTickPosY) * (double)Minecraft.getMinecraft().timer.field_194147_b - RenderManager.renderPosY, item.lastTickPosZ + (item.posZ - item.lastTickPosZ) * (double)Minecraft.getMinecraft().timer.field_194147_b - RenderManager.renderPosZ);
            GlStateManager.rotate(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            Gui.drawRect(9.0, -25.0, 13.0, -17.0, -16777216);
            Gui.drawRect(5.0, -21.0, 12.0, -25.0, -16777216);
            Gui.drawRect(10.0, -22.0, 12.0, -18.0, HackConfigs.ThemeColor);
            Gui.drawRect(6.0, -22.0, 12.0, -24.0, HackConfigs.ThemeColor);
            Gui.drawRect(-9.0, -25.0, -13.0, -17.0, -16777216);
            Gui.drawRect(-5.0, -21.0, -12.0, -25.0, -16777216);
            Gui.drawRect(-10.0, -22.0, -12.0, -18.0, HackConfigs.ThemeColor);
            Gui.drawRect(-6.0, -22.0, -12.0, -24.0, HackConfigs.ThemeColor);
            Gui.drawRect(9.0, -7.0, 13.0, 1.0, -16777216);
            Gui.drawRect(9.0, -3.0, 5.0, 1.0, -16777216);
            Gui.drawRect(10.0, -6.0, 12.0, 0.0, HackConfigs.ThemeColor);
            Gui.drawRect(10.0, -2.0, 6.0, 0.0, HackConfigs.ThemeColor);
            Gui.drawRect(-9.0, -7.0, -13.0, 1.0, -16777216);
            Gui.drawRect(-10.0, -3.0, -5.0, 1.0, -16777216);
            Gui.drawRect(-10.0, -6.0, -12.0, 0.0, HackConfigs.ThemeColor);
            Gui.drawRect(-10.0, -2.0, -6.0, 0.0, HackConfigs.ThemeColor);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 0.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }
}


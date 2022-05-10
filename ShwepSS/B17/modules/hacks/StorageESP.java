package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.Utils.RenderUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.AxisAlignedBB;

public class StorageESP
extends Module {
    public StorageESP() {
        super("StorageESP", "\u0447\u0435\u0441\u0442\u044b \u0438 \u043f\u0440\u043e\u0447\u0430\u044f \u0445\u0443\u0439\u043d\u044f \u0415\u0421\u041f", 45, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @EventTarget
    public void onGgggg(Event3DRender event) {
        Minecraft mc = Minecraft.getMinecraft();
        for (Object o2 : mc.world.loadedTileEntityList) {
            TileEntity ent = (TileEntity)o2;
            if (!(ent instanceof TileEntityChest) && !(ent instanceof TileEntityDispenser) && !(ent instanceof TileEntityEnderChest) || ent instanceof TileEntityChest || ent instanceof TileEntityDispenser || ent instanceof TileEntityEnderChest) continue;
            this.drawEsp(ent, event.pticks());
        }
    }

    private void drawEsp(TileEntity ent, float pTicks) {
        double x1 = (double)ent.getPos().getX() - RenderManager.renderPosX;
        double y1 = (double)ent.getPos().getY() - RenderManager.renderPosY;
        double z1 = (double)ent.getPos().getZ() - RenderManager.renderPosZ;
        float[] color = this.getColor(ent);
        AxisAlignedBB box = new AxisAlignedBB(x1, y1, z1, x1 + 1.0, y1 + 1.0, z1 + 1.0);
        if (ent instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest)TileEntityChest.class.cast(ent);
            if (chest.adjacentChestZPos != null) {
                box = new AxisAlignedBB(x1 + 0.0625, y1, z1 + 0.0625, x1 + 0.9375, y1 + 0.875, z1 + 1.9375);
            } else if (chest.adjacentChestXPos != null) {
                box = new AxisAlignedBB(x1 + 0.0625, y1, z1 + 0.0625, x1 + 1.9375, y1 + 0.875, z1 + 0.9375);
            } else {
                if (chest.adjacentChestZPos != null || chest.adjacentChestXPos != null || chest.adjacentChestZNeg != null || chest.adjacentChestXNeg != null) {
                    return;
                }
                box = new AxisAlignedBB(x1 + 0.0625, y1, z1 + 0.0625, x1 + 0.9375, y1 + 0.875, z1 + 0.9375);
            }
        } else if (ent instanceof TileEntityEnderChest) {
            box = new AxisAlignedBB(x1 + 0.0625, y1, z1 + 0.0625, x1 + 0.9375, y1 + 0.875, z1 + 0.9375);
        }
        RenderUtils.drawFilledBBESP(box, new Color(color[0], color[1], color[2]).getRGB() & 0x19FFFFFF);
        RenderUtils.drawOutlinedBox(box);
    }

    private float[] getColor(TileEntity ent) {
        if (ent instanceof TileEntityChest) {
            return new float[]{0.0f, 0.9f, 0.9f};
        }
        if (ent instanceof TileEntityDispenser) {
            return new float[]{0.5f, 0.5f, 0.5f};
        }
        if (ent instanceof TileEntityEnderChest) {
            return new float[]{0.3f, 0.0f, 0.3f};
        }
        return new float[]{1.0f, 1.0f, 1.0f};
    }
}


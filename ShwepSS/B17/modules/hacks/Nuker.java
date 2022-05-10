package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Nuker
extends Module {
    public Nuker() {
        super("Nuker", "\u041a\u0440\u0443\u0448\u0438\u0442 \u0431\u043b\u043e\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 (\u043d\u0443\u0436\u0435\u043d \u043a\u0440\u0435\u0430\u0442)", 0, Category.Player, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        int size = 5;
        int sizeOther = Math.round(size / 2);
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.capabilities.isCreativeMode) {
            for (int x2 = -size; x2 < size + sizeOther; ++x2) {
                for (int z2 = -size; z2 < size + sizeOther; ++z2) {
                    for (int y2 = -size; y2 < size + sizeOther; ++y2) {
                        boolean shouldBreakBlock = true;
                        int blockX = (int)(mc.player.posX + (double)x2);
                        int blockY = (int)(mc.player.posY + (double)y2);
                        int blockZ = (int)(mc.player.posZ + (double)z2);
                        if (Block.getIdFromBlock(mc.world.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock()) == 0) continue;
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(blockX, blockY, blockZ), EnumFacing.UP));
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }
}


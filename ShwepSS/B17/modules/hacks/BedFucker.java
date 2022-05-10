package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class BedFucker
extends Module {
    public BedFucker() {
        super("BedFucker", "\u041f\u044b\u0442\u0430\u0435\u0442\u0441\u044f \u043b\u043e\u043c\u0430\u0442\u044c \u043a\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u043a\u0432\u043e\u0437\u044c \u0431\u043b\u043e\u043a\u0438", 0, Category.Player, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        int playerX = (int)mc.player.posX;
        int playerZ = (int)mc.player.posZ;
        int playerY = (int)mc.player.posY;
        for (int y2 = playerY - 6; y2 <= playerY + 6; ++y2) {
            for (int x2 = playerX - 6; x2 <= playerX + 6; ++x2) {
                for (int z2 = playerZ - 6; z2 <= playerZ + 6; ++z2) {
                    BlockPos pos = new BlockPos(x2, y2, z2);
                    if (mc.world.getBlockState(pos).getBlock() != Blocks.BED) continue;
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }
}


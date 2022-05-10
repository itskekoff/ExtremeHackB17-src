package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Downloader {
    public static boolean enabled = false;
    public static ArrayList<WorldBlock> blocks = new ArrayList();
    public static ArrayList<BlockPos> loadedBlockPos = new ArrayList();

    public static void downloadBlocks(int size) {
        ChatUtils.emessage("\u041d\u0430\u0447\u0430\u0442\u0430 \u0441\u043a\u0430\u0447\u043a\u0430 \u0431\u043b\u043e\u043a\u043e\u0432, \u043e\u0436\u0438\u0434\u0430\u0439\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f.");
        Minecraft mc = Minecraft.getMinecraft();
        int sizeOther = Math.round(size / 2);
        for (int x2 = -size; x2 < size + sizeOther; ++x2) {
            for (int z2 = -size; z2 < size + sizeOther; ++z2) {
                for (int y2 = -size; y2 < size + sizeOther; ++y2) {
                    boolean shouldBreakBlock = true;
                    int blockX = (int)(mc.player.posX + (double)x2);
                    int blockY = (int)(mc.player.posY + (double)y2);
                    int blockZ = (int)(mc.player.posZ + (double)z2);
                    IBlockState block = mc.world.getBlockState(new BlockPos(blockX, blockY, blockZ));
                    if (block == null || block.getBlock() == Blocks.AIR || loadedBlockPos.contains(new BlockPos(blockX, blockY, blockZ))) continue;
                    loadedBlockPos.add(new BlockPos(blockX, blockY, blockZ));
                    blocks.add(new WorldBlock(block, new BlockPos(blockX, blockY, blockZ)));
                    ChatUtils.emessage("Block saved #" + blocks.size());
                }
            }
        }
        ChatUtils.message("\u0412\u044b\u043a\u0430\u0447\u043a\u0430 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430!");
    }

    public static class WorldBlock {
        public IBlockState s;
        public BlockPos b;

        public WorldBlock(IBlockState s2, BlockPos b2) {
            this.s = s2;
            this.b = b2;
        }
    }
}


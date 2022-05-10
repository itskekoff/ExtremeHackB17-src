package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;

public class OneBotsFollow
extends Module {
    public OneBotsFollow() {
        super("+1Bot follow", "\u0411\u0444 \u0431\u043e\u0442\u044b \u0441\u043b\u0435\u0434\u0443\u044e\u0442 \u0437\u0430 \u0432\u0430\u043c\u0438", 0, Category.BOTS, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (NetHandlerPlayClient handler : NetHandlerPlayClient.bots) {
            handler.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        }
    }

    @Override
    public void onDisable() {
    }
}


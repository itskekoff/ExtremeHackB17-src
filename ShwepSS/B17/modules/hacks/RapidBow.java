package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

public class RapidBow
extends Module {
    private Minecraft mc;
    Setting speed = new Setting("Speed", this, 4.0, 0.5, 15.0, false);

    public RapidBow() {
        super("RapidBow", "\u0411\u044b\u0441\u0442\u0440\u044b\u0439 \u043b\u0443\u043a", 0, Category.Combat, true);
        this.mc = Minecraft.getMinecraft();
        ExtremeHack.instance.getSetmgr().rSetting(this.speed);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && (double)mc.player.getItemInUseMaxCount() >= this.speed.getValDouble()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
    }
}


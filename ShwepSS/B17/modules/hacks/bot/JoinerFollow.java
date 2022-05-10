package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.bot.JoinerBot;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import net.minecraft.client.Minecraft;

public class JoinerFollow
extends Module {
    public Setting delay = new Setting("Delay", this, 1500.0, 100.0, 10000.0, false);
    TimerUtils time = new TimerUtils();

    public JoinerFollow() {
        super("JoinerFollow", "\u0414\u0436\u043e\u0439\u043d\u0435\u0440\u044b \u0431\u0443\u0434\u0443\u0442 \u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u044c \u0437\u0430 \u0432\u0430\u043c\u0438", 0, Category.BOTS, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.time.check(this.delay.getValFloat())) {
            for (JoinerBot bat2 : JoinerBot.bots) {
                bat2.sendPacket(new ClientPlayerPositionRotationPacket(mc.player.onGround, mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch));
            }
            this.time.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}


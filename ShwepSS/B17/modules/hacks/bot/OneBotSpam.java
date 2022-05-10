package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketChatMessage;

public class OneBotSpam
extends Module {
    public Setting delay = new Setting("Delay", this, 1500.0, 100.0, 10000.0, false);
    TimerUtils time = new TimerUtils();

    public OneBotSpam() {
        super("+1Bot spam -", "\u0421\u043f\u0430\u043c\u0438\u0442 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043e\u0442 \u0431\u0430\u0433\u0430\u043d\u044b\u0445 \u0431\u043e\u0442\u043e\u0432", 0, Category.BOTS, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        if (this.time.check(this.delay.getValFloat())) {
            for (NetHandlerPlayClient bat2 : NetHandlerPlayClient.bots) {
                bat2.sendPacket(new CPacketChatMessage(HackConfigs.spom));
            }
            this.time.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}


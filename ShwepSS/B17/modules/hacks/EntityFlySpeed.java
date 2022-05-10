package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class EntityFlySpeed
extends Module {
    Setting speed = new Setting("Speed", this, 0.7, 0.1, 10.0, false);
    Setting speedY = new Setting("SpeedY", this, 0.6, 0.1, 10.0, false);

    public EntityFlySpeed() {
        super("EntityFly/Speed", "\u041f\u043e\u0437\u0432\u043e\u043b\u0438\u0442 \u043b\u0435\u0442\u0430\u0442\u044c \u043d\u0430 \u043b\u043e\u0434\u043a\u0430\u0445 \u0438 \u0442\u0434", 0, Category.Movement, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.speed);
        ExtremeHack.instance.getSetmgr().rSetting(this.speedY);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player.isRiding()) {
            player.getRidingEntity().motionY = mc.gameSettings.keyBindJump.pressed ? this.speedY.getValFloat() : 0.0f;
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                MovementUtil.speedlodka(this.speed.getValFloat());
            }
            if (mc.gameSettings.keyBindBack.isKeyDown()) {
                MovementUtil.speedlodka(-this.speed.getValFloat());
            }
        }
    }

    @Override
    public void onDisable() {
    }
}


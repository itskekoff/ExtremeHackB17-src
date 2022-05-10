package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.MathUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;

public class ChitKrutiklka
extends Module {
    static int delay;
    private int Delaytimer;
    public float yaw;
    public float pitch;
    public Boolean sneak;
    float[] lastAngles;
    public static float rotationPitch;
    private boolean fake;
    private boolean fake1;
    private boolean shouldsneak;
    ArrayList<String> options;

    public ChitKrutiklka() {
        super("\u041a\u0440\u0443\u0442\u0438\u043b\u043a\u0430", "\u041d\u0430\u0436\u043c\u0438 \u043d\u0430 \u04445", -1, Category.MISC, true);
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("SpinSpeed", this, 5.0, 0.0, 50.0, true));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.keyBindUseItem.pressed) {
            boolean fake;
            if (this.lastAngles == null) {
                float[] arrf = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
                this.lastAngles = arrf;
            }
            this.fake = fake = !this.fake;
            float sp2 = 30.0f;
            float yaw8 = this.lastAngles[0] + sp2;
            this.lastAngles = new float[]{yaw8, this.lastAngles[1]};
            float sens = this.getSensitivityMultiplier();
            float yaw = yaw8 + (float)MathUtils.getRandomInRange(1, -3);
            float yawGCD = (float)Math.round(yaw / sens) * sens;
            this.yaw = yaw;
            Minecraft.getMinecraft();
            Minecraft.getMinecraft().player.rotationYawHead = yaw;
            Minecraft.getMinecraft();
            Minecraft.getMinecraft().player.renderYawOffset = yaw;
            for (NetHandlerPlayClient player : NetHandlerPlayClient.bots) {
                player.sendPacket(new CPacketPlayer.Rotation(yaw, mc.player.rotationPitch, mc.player.onGround));
            }
            this.updateAngles(yaw8, this.lastAngles[1]);
        }
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().player.rotationYawHead = yaw;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().player.renderYawOffset = yaw;
    }

    private float getSensitivityMultiplier() {
        Minecraft mc = Minecraft.getMinecraft();
        float f2 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        return f2 * f2 * f2 * 8.0f * 0.15f;
    }

    public void updateAngles(float n2, float rotationPitch) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.thirdPersonView != 0) {
            ChitKrutiklka.rotationPitch = rotationPitch;
            mc.player.rotationYawHead = n2;
            mc.player.renderYawOffset = n2;
        }
    }

    @Override
    public void onDisable() {
    }
}


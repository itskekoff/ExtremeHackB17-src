package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventOpenScreen;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketPlayer;

public class MPEScript
extends Module {
    public File mpe = new File("ExtremeHack/algorithm.mpe");
    public TimerUtils time = new TimerUtils();

    public MPEScript() {
        super("mpeScript", "\u0441\u043a\u0440\u0438\u043f\u0442\u044b .minecraft/ExtremeHack/algorithm.mpe", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        if (!this.mpe.exists()) {
            ChatUtils.success("MPE not found!");
            try {
                this.mpe.createNewFile();
                ChatUtils.success("MPE created to .minecraft/ExtremeHack/");
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
    }

    @EventTarget
    public void onGAGA(EventOpenScreen ev2) {
        if (ev2.getScreen() instanceof GuiDisconnected && this.isEnabled()) {
            this.toggle();
        }
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.mpe.exists()) {
            try {
                int wait1;
                FileReader fr = new FileReader(this.mpe);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                if (line.contains("delay=") && this.time.check((long)(wait1 = Integer.parseInt(line.split("delay=")[1])) + 0L)) {
                    while (line != null) {
                        int kd2;
                        int kd32;
                        int i2;
                        int grad;
                        line = reader.readLine();
                        if (line.contains("#")) continue;
                        if (line.contains("rotate=")) {
                            grad = Integer.parseInt(line.split("rotate=")[1]);
                            mc.player.rotationYaw += (float)grad;
                        }
                        if (line.contains("rotation=")) {
                            grad = Integer.parseInt(line.split("rotation=")[1]);
                            mc.player.rotationYaw = grad;
                        }
                        if (line.contains("wait=")) {
                            int wait = Integer.parseInt(line.split("wait=")[1]);
                            Thread.sleep((long)wait + 0L);
                        }
                        if (line.contains("motionY=")) {
                            i2 = Integer.parseInt(line.split("motionY=")[1]);
                            mc.player.motionY += (double)i2;
                        }
                        if (line.contains("slot=")) {
                            int slod = Integer.parseInt(line.split("slot=")[1]);
                            if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerChest) {
                                ContainerChest containerchest = (ContainerChest)mc.player.openContainer;
                                mc.playerController.windowClick(containerchest.windowId, slod, 0, ClickType.QUICK_MOVE, mc.player);
                            }
                        }
                        if (line.contains("packetY=")) {
                            i2 = Integer.parseInt(line.split("packetY=")[1]);
                            this.sendAllHandlers(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (double)i2, mc.player.posZ, true));
                        }
                        if (line.contains("closegui")) {
                            mc.displayGuiScreen(null);
                        }
                        if (line.contains("kd=") && this.time.check((long)(kd32 = Integer.parseInt(line.split("kd=")[1])) + 0L)) {
                            this.time.reset();
                        }
                        if (line.contains("connect=")) {
                            String kd32 = line.split("connect=")[1];
                        }
                        if (line.contains("kd=") && this.time.check((long)(kd2 = Integer.parseInt(line.split("kd=")[1])) + 0L)) {
                            this.time.reset();
                        }
                        if (line.contains("forward=")) {
                            double i3 = Double.parseDouble(line.split("forward=")[1]);
                            MovementUtil.setSpeed2(i3);
                        }
                        if (line.contains("kd=") && this.time.check((long)(kd = Integer.parseInt(line.split("kd=")[1])) + 0L)) {
                            this.time.reset();
                        }
                        if (line.contains("sneak=on")) {
                            mc.gameSettings.keyBindSneak.pressed = true;
                        }
                        if (line.contains("kd=") && this.time.check((long)(kd = Integer.parseInt(line.split("kd=")[1])) + 0L)) {
                            this.time.reset();
                        }
                        if (line.contains("sneak=off")) {
                            mc.gameSettings.keyBindSneak.pressed = false;
                        }
                        if (line.contains("kd=") && this.time.check((long)(kd = Integer.parseInt(line.split("kd=")[1])) + 0L)) {
                            this.time.reset();
                        }
                        if (line.contains("pitch=")) {
                            int grad2 = Integer.parseInt(line.split("pitch=")[1]);
                            mc.player.rotationPitch = grad2;
                        }
                        if (line.contains("jump")) {
                            mc.player.jump();
                        }
                        if (line.contains("chat=")) {
                            String msg = line.split("chat=")[1];
                            msg = msg.replace("LL", RandomUtils.randomString(5));
                            this.sendAllHandlers(new CPacketChatMessage(msg));
                        }
                        if (line.contains("break")) {
                            this.toggle();
                        }
                        this.time.reset();
                    }
                }
            }
            catch (NullPointerException fr) {
            }
            catch (InterruptedException ie2) {
                ie2.printStackTrace();
            }
            catch (FileNotFoundException eg2) {
                eg2.printStackTrace();
            }
            catch (IOException eg2) {
                eg2.printStackTrace();
            }
        }
    }

    public void sendAllHandlers(Packet packet) {
        for (NetHandlerPlayClient client : NetHandlerPlayClient.bots) {
            client.sendPacket(packet);
        }
    }
}


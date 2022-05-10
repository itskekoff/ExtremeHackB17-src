package ShwepSS.B17;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.hacks.bot.ProxyManager;
import ShwepSS.B17.modules.hacks.bot.b16AutoReg;
import ShwepSS.B17.modules.hacks.bot.b16Follow;
import ShwepSS.B17.modules.hacks.bot.b16Kilka;
import ShwepSS.B17.modules.hacks.bot.b16OnChat;
import ShwepSS.B17.modules.hacks.bot.b16Proxy;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class Bots {
    private static Color[] MapColors;
    private static IndexColorModel MapColorModel;
    private int slotId = 0;
    public static Session bot;
    public static Client Cbot;
    static int connected;
    public static ArrayList<Session> sessions;
    public static boolean headless;
    public static boolean autoregister;
    public static Proxy p;
    protected Random rand;
    private TimerUtils time;
    private Lock sequential_lock = new ReentrantLock();
    public static int delay;
    private boolean onGround = true;
    public float moveStrafing;
    public float moveForward;
    private int OwnerID;
    public static double lastPosY;
    public static ArrayList<Integer> entId;
    private int entId1;
    public static double posX;
    public static double posY;
    public static double posZ;
    public float getYaw;
    public float getPitch;
    public int tpId = 0;
    public GameProfile gameProfile;
    public static int spamms;
    public static boolean spamenabled;
    public static String spammsg;
    public float fallDistance;

    static {
        entId = new ArrayList();
        sessions = new ArrayList();
        autoregister = false;
        p = Proxy.NO_PROXY;
    }

    public Session getSession() {
        return bot;
    }

    public Bots() {
        this.time = new TimerUtils();
    }

    public String getName() {
        Session s2 = this.getSession();
        GameProfile gameProfile = (GameProfile)s2.getFlag("profile");
        String name = gameProfile.getName();
        return name;
    }

    public void login(final String username, String host, int port) {
        MinecraftProtocol protocol = null;
        this.OwnerID = Minecraft.getMinecraft().player.entityId;
        protocol = new MinecraftProtocol(username);
        Client client = new Client(host, port, protocol, new TcpSessionFactory(p));
        if (b16Proxy.ins.isEnabled()) {
            ChatUtils.emessage("\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0431\u043e\u0442\u0430 \u0441 \u043f\u0440\u043e\u043a\u0441\u0438...");
            client = new Client(host, port, protocol, new TcpSessionFactory(ProxyManager.getRandomProxy()));
        } else {
            client = new Client(host, port, protocol, new TcpSessionFactory(p));
        }
        bot = client.getSession();
        client.getSession().addListener(new SessionAdapter(){

            @Override
            public void packetReceived(PacketReceivedEvent event) {
                try {
                    if (event.getPacket() instanceof ServerJoinGamePacket) {
                        ServerJoinGamePacket pack = (ServerJoinGamePacket)event.getPacket();
                        Bots.this.entId1 = pack.getEntityId();
                        entId.add(pack.getEntityId());
                        ChatUtils.message(String.valueOf(pack.getDifficulty().name()) + " " + pack.getDimension() + " " + pack.getMaxPlayers() + " " + pack.getGameMode() + " Reduced debug info " + pack.getReducedDebugInfo());
                        ClientSettingsPacket packet = new ClientSettingsPacket("en_US", 8, ChatVisibility.FULL, true, SkinPart.values(), Hand.MAIN_HAND);
                        event.getSession().send(packet);
                    } else if (event.getPacket() instanceof LoginSuccessPacket) {
                        LoginSuccessPacket pack = (LoginSuccessPacket)event.getPacket();
                        sessions.add(bot);
                        ChatUtils.message(String.valueOf(ChatUtils.ehack) + "Connected " + username + "!");
                    } else if (event.getPacket() instanceof ServerChatPacket) {
                        ServerChatPacket p2 = (ServerChatPacket)event.getPacket();
                        if (b16OnChat.ins.isEnabled()) {
                            GameProfile gameProfile = (GameProfile)event.getSession().getFlag("profile");
                            String name = gameProfile.getName();
                            ChatUtils.message(String.valueOf(ChatUtils.gray) + "[" + ChatUtils.cyan + name + ChatUtils.gray + "]" + ChatUtils.white + " | " + p2.getMessage().getFullText());
                        }
                        if (b16AutoReg.ins.isEnabled()) {
                            if (p2.getMessage().toString().toLowerCase().contains("/reg")) {
                                event.getSession().send(new ClientChatPacket("/register qazwsx123 qazwsx123"));
                                ChatUtils.message(String.valueOf(ChatUtils.ehack) + "\u0411\u043e\u0442 \u0437\u0430\u0440\u0435\u0433\u0430\u043d\u0142! pass qazwsx123");
                            }
                            if (p2.getMessage().toString().toLowerCase().contains("/login ")) {
                                event.getSession().send(new ClientChatPacket("/login qazwsx123"));
                                ChatUtils.message(String.valueOf(ChatUtils.ehack) + "\u0411\u043e\u0442 \u0437\u0430\u043b\u043e\u0433\u0438\u043d\u0435\u043d\u0142! pass qazwsx123");
                            }
                        }
                    } else if (event.getPacket() instanceof ServerPlayerHealthPacket) {
                        ServerPlayerHealthPacket p2 = (ServerPlayerHealthPacket)event.getPacket();
                        if (p2.getHealth() == 0.0f) {
                            event.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                        }
                    } else if (event.getPacket() instanceof ServerOpenWindowPacket) {
                        ServerOpenWindowPacket pc2 = (ServerOpenWindowPacket)event.getPacket();
                        ChatUtils.success("Window chest oppened");
                        event.getSession().send(new ClientWindowActionPacket(pc2.getWindowId(), 1, 2, new ItemStack(355), WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK));
                        event.getSession().send(new ClientWindowActionPacket(pc2.getWindowId(), 1, 3, new ItemStack(355), WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK));
                        event.getSession().send(new ClientCloseWindowPacket(pc2.getWindowId()));
                    } else if (event.getPacket() instanceof ServerResourcePackSendPacket) {
                        ClientResourcePackStatusPacket packet1 = new ClientResourcePackStatusPacket(ResourcePackStatus.ACCEPTED);
                        ClientResourcePackStatusPacket packet2 = new ClientResourcePackStatusPacket(ResourcePackStatus.SUCCESSFULLY_LOADED);
                        event.getSession().send(packet1);
                        event.getSession().send(packet2);
                    } else if (event.getPacket() instanceof ServerPlayerPositionRotationPacket) {
                        ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket)event.getPacket();
                        posX = packet.getX();
                        posY = packet.getY();
                        posZ = packet.getZ();
                        Bots.this.getYaw = packet.getYaw();
                        Bots.this.getPitch = packet.getPitch();
                        Bots.this.tpId = packet.getTeleportId();
                        GameProfile gameProfile = (GameProfile)event.getSession().getFlag("profile");
                        String name = gameProfile.getName();
                        ClientTeleportConfirmPacket confirm = new ClientTeleportConfirmPacket(Bots.this.tpId);
                        Bots.sendbot(confirm);
                        ClientRequestPacket p2 = new ClientRequestPacket(ClientRequest.STATS);
                        event.getSession().send(p2);
                    } else if (event.getPacket() instanceof ServerSetSlotPacket) {
                        ServerSetSlotPacket slot = (ServerSetSlotPacket)event.getPacket();
                        ItemStack cap2 = new ItemStack(358);
                        ItemStack nulId = new ItemStack(0);
                        try {
                            if (slot.getSlot() == -1) {
                                return;
                            }
                            if (slot.getSlot() == 0) {
                                return;
                            }
                            Bots.this.slotId = slot.getItem().getId();
                        }
                        catch (Exception exception) {}
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                String powod = event.getReason();
                ChatUtils.message(String.valueOf(ChatUtils.ehack) + ChatUtils.red + "Disconnected: " + username + " " + powod);
                sessions.remove(bot);
            }
        });
        TickThread thread = new TickThread(this);
        thread.start();
        client.getSession().connect();
    }

    public void onTickAS() {
        this.sequential_lock.lock();
        this.onTick();
        this.sequential_lock.unlock();
    }

    public void setPosition(double posX2, double d2, double posZ2) {
        posX = posX2;
        posY = d2;
        posZ = posZ2;
        Bots.sendbot(new ClientPlayerPositionRotationPacket(true, posX2, d2, posZ2, this.getYaw, this.getPitch));
    }

    protected void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (b16Follow.ins.isEnabled()) {
            Bots.sendbot(new ClientPlayerPositionRotationPacket(true, mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch));
        }
        if (b16Kilka.ins.isEnabled()) {
            for (Entity en2 : mc.world.loadedEntityList) {
                if (!(en2 instanceof EntityLivingBase)) continue;
                for (int i2 : entId) {
                    if (en2.getEntityId() == i2 || en2.getEntityId() == this.OwnerID || !(this.getDistanceToEntity(en2) < 6.0f) || !this.time.check(500.0f)) continue;
                    Bots.sendbot(new ClientPlayerInteractEntityPacket(en2.getEntityId(), InteractAction.ATTACK, en2.getPosition().getX(), en2.getPosition().getY(), en2.getPosition().getZ(), Hand.MAIN_HAND));
                    Bots.sendbot(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
                    this.time.reset();
                }
            }
        }
    }

    public float getDistanceToEntity(Entity entityIn) {
        float f2 = (float)(posX - entityIn.posX);
        float f1 = (float)(posY - entityIn.posY);
        float f22 = (float)(posZ - entityIn.posZ);
        return MathHelper.sqrt(f2 * f2 + f1 * f1 + f22 * f22);
    }

    public static void sendbot(Packet p2) {
        for (Session bot2 : sessions) {
            bot2.send(p2);
        }
    }

    private static class TickThread
    extends Thread {
        private Bots bot;
        private boolean is_dead;

        public TickThread(Bots bot2) {
            this.bot = bot2;
            this.is_dead = false;
        }

        private boolean isDead() {
            return this.is_dead;
        }

        public void setDead() {
            this.is_dead = true;
        }

        @Override
        public void run() {
            while (!this.is_dead) {
                long start = System.currentTimeMillis();
                this.bot.onTickAS();
                long end = System.currentTimeMillis();
                long tick_duration = end - start;
                if (tick_duration >= 50L) continue;
                try {
                    Thread.sleep(50L - tick_duration);
                }
                catch (InterruptedException e2) {
                    break;
                }
            }
        }
    }
}


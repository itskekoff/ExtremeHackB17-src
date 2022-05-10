package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.hacks.bot.ProxyManager;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.RandomUtils;

public class JoinerBot {
    private String username;
    private Client client;
    public static String ipAddress;
    public static int portKek;
    public static JoinerBot instance;
    public double posX;
    public double posY;
    public double posZ;
    public float yaw;
    public float pitch;
    public static CopyOnWriteArrayList<JoinerBot> bots;

    static {
        bots = new CopyOnWriteArrayList();
    }

    public JoinerBot(String username) {
        this(username, "");
        instance = this;
    }

    public JoinerBot(String username, String password) {
        this.username = username;
    }

    public void connect(String host) {
        try {
            if (host.contains(":")) {
                this.connect(host.split(":")[0], Integer.valueOf(host.split(":")[1]));
            } else {
                this.connect(host, 25565);
            }
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    public void connect(String host, int port) {
        try {
            portKek = port;
            ipAddress = host;
            this.username = "Extreme" + RandomUtils.nextInt(1, 999999);
            MinecraftProtocol protocol = null;
            protocol = new MinecraftProtocol(this.username);
            bots.add(this);
            this.client = new Client(host, port, protocol, new TcpSessionFactory(ProxyManager.getRandomProxy()));
            this.client.getSession().addListener(new SessionAdapter(){

                @Override
                public void disconnected(DisconnectedEvent event) {
                    bots.remove(instance);
                }
            });
            this.client.getSession().addListener(new SessionAdapter(){

                @Override
                public void packetReceived(PacketReceivedEvent event) {
                    if (event.getPacket() instanceof ServerPlayerPositionRotationPacket) {
                        ServerPlayerPositionRotationPacket pos = (ServerPlayerPositionRotationPacket)event.getPacket();
                        JoinerBot.this.posX = pos.getX();
                        JoinerBot.this.posY = pos.getY();
                        JoinerBot.this.posZ = pos.getZ();
                        event.getSession().send(new ClientTeleportConfirmPacket(pos.getTeleportId()));
                    }
                    if (event.getPacket() instanceof ServerChatPacket) {
                        ServerChatPacket packet = (ServerChatPacket)event.getPacket();
                        if (packet.getMessage().getFullText().contains("/reg")) {
                            event.getSession().send(new ClientChatPacket("/register qwert12346 qwert12346"));
                            ChatUtils.emessage("\u0411\u0430\u0442 \u0437\u0430\u0440\u0435\u0433\u0430\u043d!");
                        }
                        if (packet.getMessage().getFullText().contains("/l")) {
                            event.getSession().send(new ClientChatPacket("/login qwert12346"));
                            ChatUtils.emessage("\u0411\u0430\u0442 \u0437\u0430\u043b\u043e\u0433\u0438\u043d\u0435\u043d!");
                        }
                    }
                }
            });
            this.client.getSession().connect();
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    public void disconnect(String reason) {
        if (this.client.getSession().isConnected()) {
            this.client.getSession().disconnect(reason);
        }
    }

    public void sendMessage(String message) {
        if (this.client.getSession().isConnected()) {
            this.client.getSession().send(new ClientChatPacket(message));
        }
    }

    public boolean isConnected() {
        if (this.client != null) {
            return this.client.getSession().isConnected();
        }
        return false;
    }

    public void sendPacket(Packet packet) {
        if (this.isConnected()) {
            this.client.getSession().send(packet);
        }
    }

    public void respawnBot() {
        if (this.isConnected()) {
            this.sendPacket(new ClientRequestPacket(ClientRequest.RESPAWN));
        }
    }
}


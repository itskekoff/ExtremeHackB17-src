package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.modules.hacks.bot.JoinerBot;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

public class BotListener
extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        block9: {
            block11: {
                block10: {
                    if (event.getPacket() instanceof ServerJoinGamePacket) break block9;
                    if (!(event.getPacket() instanceof ServerPlayerHealthPacket)) break block10;
                    ServerPlayerHealthPacket health = (ServerPlayerHealthPacket)event.getPacket();
                    if (!(health.getHealth() <= 0.0f)) break block9;
                    event.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                    break block9;
                }
                if (!(event.getPacket() instanceof ServerChatPacket)) break block11;
                ServerChatPacket packet = (ServerChatPacket)event.getPacket();
                String msg = packet.getMessage().getText();
                if (!msg.contains("\u0441\u043a\u0430\u0436\u0438")) break block9;
                String kek = msg.split("\u0441\u043a\u0430\u0436\u0438 ")[1];
                event.getSession().send(new ClientChatPacket(kek));
                break block9;
            }
            if (event.getPacket() instanceof ServerKeepAlivePacket) {
                ServerKeepAlivePacket packet = (ServerKeepAlivePacket)event.getPacket();
            } else if (event.getPacket() instanceof ServerEntityHeadLookPacket) {
                ServerEntityHeadLookPacket packet = (ServerEntityHeadLookPacket)event.getPacket();
            } else if (event.getPacket() instanceof ServerUpdateTimePacket) {
                for (JoinerBot bat2 : JoinerBot.bots) {
                    bat2.sendPacket(new ClientPlayerPositionPacket(true, bat2.posX, bat2.posY, bat2.posZ));
                }
            } else if (event.getPacket() instanceof ServerEntityMovementPacket) {
                ServerEntityMovementPacket packet = (ServerEntityMovementPacket)event.getPacket();
                for (JoinerBot bot2 : JoinerBot.bots) {
                    bot2.posX += packet.getMovementX();
                    bot2.posY += packet.getMovementY();
                    bot2.posZ += packet.getMovementZ();
                    bot2.sendPacket(new ClientPlayerPositionPacket(true, bot2.posX, bot2.posY, bot2.posZ));
                }
            }
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
    }
}


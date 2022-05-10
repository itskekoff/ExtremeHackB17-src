package net.minecraft.network.play.server;

import ShwepSS.B17.modules.hacks.AutoUnban;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class SPacketChat
implements Packet<INetHandlerPlayClient> {
    private ITextComponent chatComponent;
    private ChatType type;

    public SPacketChat() {
    }

    public SPacketChat(ITextComponent componentIn) {
        this(componentIn, ChatType.SYSTEM);
    }

    public SPacketChat(ITextComponent p_i47428_1_, ChatType p_i47428_2_) {
        this.chatComponent = p_i47428_1_;
        this.type = p_i47428_2_;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.chatComponent = buf2.readTextComponent();
        this.type = ChatType.func_192582_a(buf2.readByte());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeTextComponent(this.chatComponent);
        buf2.writeByte(this.type.func_192583_a());
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        if (this.chatComponent.getUnformattedText().contains("Delegator")) {
            return;
        }
        if (this.chatComponent.getUnformattedText().contains("${")) {
            return;
        }
        if (AutoUnban.autoUnban) {
            Minecraft mc = Minecraft.getMinecraft();
            if (this.chatComponent.getUnformattedText().contains("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd")) {
                String nick = this.chatComponent.getUnformattedText().split("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd ")[1];
                mc.player.sendChatMessage("/unban " + nick);
            }
        }
        handler.handleChat(this);
    }

    public ITextComponent getChatComponent() {
        return this.chatComponent;
    }

    public boolean isSystem() {
        return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
    }

    public ChatType func_192590_c() {
        return this.type;
    }
}


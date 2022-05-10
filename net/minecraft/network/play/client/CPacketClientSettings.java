package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHandSide;

public class CPacketClientSettings
implements Packet<INetHandlerPlayServer> {
    private String lang;
    private int view;
    private EntityPlayer.EnumChatVisibility chatVisibility;
    private boolean enableColors;
    private int modelPartFlags;
    private EnumHandSide mainHand;

    public CPacketClientSettings() {
    }

    public CPacketClientSettings(String langIn, int renderDistanceIn, EntityPlayer.EnumChatVisibility chatVisibilityIn, boolean chatColorsIn, int modelPartsIn, EnumHandSide mainHandIn) {
        this.lang = langIn;
        this.view = renderDistanceIn;
        this.chatVisibility = chatVisibilityIn;
        this.enableColors = chatColorsIn;
        this.modelPartFlags = modelPartsIn;
        this.mainHand = mainHandIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.lang = buf2.readStringFromBuffer(16);
        this.view = buf2.readByte();
        this.chatVisibility = buf2.readEnumValue(EntityPlayer.EnumChatVisibility.class);
        this.enableColors = buf2.readBoolean();
        this.modelPartFlags = buf2.readUnsignedByte();
        this.mainHand = buf2.readEnumValue(EnumHandSide.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(this.lang);
        buf2.writeByte(this.view);
        buf2.writeEnumValue(this.chatVisibility);
        buf2.writeBoolean(this.enableColors);
        buf2.writeByte(this.modelPartFlags);
        buf2.writeEnumValue(this.mainHand);
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClientSettings(this);
    }

    public String getLang() {
        return this.lang;
    }

    public EntityPlayer.EnumChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public boolean isColorsEnabled() {
        return this.enableColors;
    }

    public int getModelPartFlags() {
        return this.modelPartFlags;
    }

    public EnumHandSide getMainHand() {
        return this.mainHand;
    }
}


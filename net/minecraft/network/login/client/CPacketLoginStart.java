package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class CPacketLoginStart
implements Packet<INetHandlerLoginServer> {
    private GameProfile profile;

    public CPacketLoginStart() {
    }

    public CPacketLoginStart(GameProfile profileIn) {
        this.profile = profileIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.profile = new GameProfile(null, buf2.readStringFromBuffer(16));
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(this.profile.getName());
    }

    @Override
    public void processPacket(INetHandlerLoginServer handler) {
        handler.processLoginStart(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}


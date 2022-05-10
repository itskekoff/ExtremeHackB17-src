package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketLoginSuccess
implements Packet<INetHandlerLoginClient> {
    private GameProfile profile;

    public SPacketLoginSuccess() {
    }

    public SPacketLoginSuccess(GameProfile profileIn) {
        this.profile = profileIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        String s2 = buf2.readStringFromBuffer(36);
        String s1 = buf2.readStringFromBuffer(16);
        UUID uuid = UUID.fromString(s2);
        this.profile = new GameProfile(uuid, s1);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        UUID uuid = this.profile.getId();
        buf2.writeString(uuid == null ? "" : uuid.toString());
        buf2.writeString(this.profile.getName());
    }

    @Override
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleLoginSuccess(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}


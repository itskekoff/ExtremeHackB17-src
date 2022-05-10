package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class SPacketEncryptionRequest
implements Packet<INetHandlerLoginClient> {
    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    public SPacketEncryptionRequest() {
    }

    public SPacketEncryptionRequest(String serverIdIn, PublicKey publicKeyIn, byte[] verifyTokenIn) {
        this.hashedServerId = serverIdIn;
        this.publicKey = publicKeyIn;
        this.verifyToken = verifyTokenIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.hashedServerId = buf2.readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(buf2.readByteArray());
        this.verifyToken = buf2.readByteArray();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(this.hashedServerId);
        buf2.writeByteArray(this.publicKey.getEncoded());
        buf2.writeByteArray(this.verifyToken);
    }

    @Override
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleEncryptionRequest(this);
    }

    public String getServerId() {
        return this.hashedServerId;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}


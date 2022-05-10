package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public class SPacketRespawn
implements Packet<INetHandlerPlayClient> {
    private int dimensionID;
    private EnumDifficulty difficulty;
    private GameType gameType;
    private WorldType worldType;

    public SPacketRespawn() {
    }

    public SPacketRespawn(int dimensionIdIn, EnumDifficulty difficultyIn, WorldType worldTypeIn, GameType gameModeIn) {
        this.dimensionID = dimensionIdIn;
        this.difficulty = difficultyIn;
        this.gameType = gameModeIn;
        this.worldType = worldTypeIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleRespawn(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.dimensionID = buf2.readInt();
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf2.readUnsignedByte());
        this.gameType = GameType.getByID(buf2.readUnsignedByte());
        this.worldType = WorldType.parseWorldType(buf2.readStringFromBuffer(16));
        if (this.worldType == null) {
            this.worldType = WorldType.DEFAULT;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.dimensionID);
        buf2.writeByte(this.difficulty.getDifficultyId());
        buf2.writeByte(this.gameType.getID());
        buf2.writeString(this.worldType.getWorldTypeName());
    }

    public int getDimensionID() {
        return this.dimensionID;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }
}


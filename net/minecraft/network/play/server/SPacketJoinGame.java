package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public class SPacketJoinGame
implements Packet<INetHandlerPlayClient> {
    private int playerId;
    private boolean hardcoreMode;
    private GameType gameType;
    private int dimension;
    private EnumDifficulty difficulty;
    private int maxPlayers;
    private WorldType worldType;
    private boolean reducedDebugInfo;

    public SPacketJoinGame() {
    }

    public SPacketJoinGame(int playerIdIn, GameType gameTypeIn, boolean hardcoreModeIn, int dimensionIn, EnumDifficulty difficultyIn, int maxPlayersIn, WorldType worldTypeIn, boolean reducedDebugInfoIn) {
        this.playerId = playerIdIn;
        this.dimension = dimensionIn;
        this.difficulty = difficultyIn;
        this.gameType = gameTypeIn;
        this.maxPlayers = maxPlayersIn;
        this.hardcoreMode = hardcoreModeIn;
        this.worldType = worldTypeIn;
        this.reducedDebugInfo = reducedDebugInfoIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.playerId = buf2.readInt();
        int i2 = buf2.readUnsignedByte();
        this.hardcoreMode = (i2 & 8) == 8;
        this.gameType = GameType.getByID(i2 &= 0xFFFFFFF7);
        this.dimension = buf2.readInt();
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf2.readUnsignedByte());
        this.maxPlayers = buf2.readUnsignedByte();
        this.worldType = WorldType.parseWorldType(buf2.readStringFromBuffer(16));
        if (this.worldType == null) {
            this.worldType = WorldType.DEFAULT;
        }
        this.reducedDebugInfo = buf2.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.playerId);
        int i2 = this.gameType.getID();
        if (this.hardcoreMode) {
            i2 |= 8;
        }
        buf2.writeByte(i2);
        buf2.writeInt(this.dimension);
        buf2.writeByte(this.difficulty.getDifficultyId());
        buf2.writeByte(this.maxPlayers);
        buf2.writeString(this.worldType.getWorldTypeName());
        buf2.writeBoolean(this.reducedDebugInfo);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleJoinGame(this);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public boolean isHardcoreMode() {
        return this.hardcoreMode;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public int getDimension() {
        return this.dimension;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }
}


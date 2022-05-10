package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunkMapEntry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PlayerChunkMap playerChunkMap;
    private final List<EntityPlayerMP> players = Lists.newArrayList();
    private final ChunkPos pos;
    private final short[] changedBlocks = new short[64];
    @Nullable
    private Chunk chunk;
    private int changes;
    private int changedSectionFilter;
    private long lastUpdateInhabitedTime;
    private boolean sentToPlayers;

    public PlayerChunkMapEntry(PlayerChunkMap mapIn, int chunkX, int chunkZ) {
        this.playerChunkMap = mapIn;
        this.pos = new ChunkPos(chunkX, chunkZ);
        this.chunk = mapIn.getWorldServer().getChunkProvider().loadChunk(chunkX, chunkZ);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public void addPlayer(EntityPlayerMP player) {
        if (this.players.contains(player)) {
            LOGGER.debug("Failed to add player. {} already is in chunk {}, {}", (Object)player, (Object)this.pos.chunkXPos, (Object)this.pos.chunkZPos);
        } else {
            if (this.players.isEmpty()) {
                this.lastUpdateInhabitedTime = this.playerChunkMap.getWorldServer().getTotalWorldTime();
            }
            this.players.add(player);
            if (this.sentToPlayers) {
                this.sendNearbySpecialEntities(player);
            }
        }
    }

    public void removePlayer(EntityPlayerMP player) {
        if (this.players.contains(player)) {
            if (this.sentToPlayers) {
                player.connection.sendPacket(new SPacketUnloadChunk(this.pos.chunkXPos, this.pos.chunkZPos));
            }
            this.players.remove(player);
            if (this.players.isEmpty()) {
                this.playerChunkMap.removeEntry(this);
            }
        }
    }

    public boolean providePlayerChunk(boolean canGenerate) {
        if (this.chunk != null) {
            return true;
        }
        this.chunk = canGenerate ? this.playerChunkMap.getWorldServer().getChunkProvider().provideChunk(this.pos.chunkXPos, this.pos.chunkZPos) : this.playerChunkMap.getWorldServer().getChunkProvider().loadChunk(this.pos.chunkXPos, this.pos.chunkZPos);
        return this.chunk != null;
    }

    public boolean sendToPlayers() {
        if (this.sentToPlayers) {
            return true;
        }
        if (this.chunk == null) {
            return false;
        }
        if (!this.chunk.isPopulated()) {
            return false;
        }
        this.changes = 0;
        this.changedSectionFilter = 0;
        this.sentToPlayers = true;
        SPacketChunkData packet = new SPacketChunkData(this.chunk, 65535);
        for (EntityPlayerMP entityplayermp : this.players) {
            entityplayermp.connection.sendPacket(packet);
            this.playerChunkMap.getWorldServer().getEntityTracker().sendLeashedEntitiesInChunk(entityplayermp, this.chunk);
        }
        return true;
    }

    public void sendNearbySpecialEntities(EntityPlayerMP player) {
        if (this.sentToPlayers) {
            player.connection.sendPacket(new SPacketChunkData(this.chunk, 65535));
            this.playerChunkMap.getWorldServer().getEntityTracker().sendLeashedEntitiesInChunk(player, this.chunk);
        }
    }

    public void updateChunkInhabitedTime() {
        long i2 = this.playerChunkMap.getWorldServer().getTotalWorldTime();
        if (this.chunk != null) {
            this.chunk.setInhabitedTime(this.chunk.getInhabitedTime() + i2 - this.lastUpdateInhabitedTime);
        }
        this.lastUpdateInhabitedTime = i2;
    }

    public void blockChanged(int x2, int y2, int z2) {
        if (this.sentToPlayers) {
            if (this.changes == 0) {
                this.playerChunkMap.addEntry(this);
            }
            this.changedSectionFilter |= 1 << (y2 >> 4);
            if (this.changes < 64) {
                short short1 = (short)(x2 << 12 | z2 << 8 | y2);
                for (int i2 = 0; i2 < this.changes; ++i2) {
                    if (this.changedBlocks[i2] != short1) continue;
                    return;
                }
                this.changedBlocks[this.changes++] = short1;
            }
        }
    }

    public void sendPacket(Packet<?> packetIn) {
        if (this.sentToPlayers) {
            for (int i2 = 0; i2 < this.players.size(); ++i2) {
                this.players.get((int)i2).connection.sendPacket(packetIn);
            }
        }
    }

    public void update() {
        if (this.sentToPlayers && this.chunk != null && this.changes != 0) {
            if (this.changes == 1) {
                int i2 = (this.changedBlocks[0] >> 12 & 0xF) + this.pos.chunkXPos * 16;
                int j2 = this.changedBlocks[0] & 0xFF;
                int k2 = (this.changedBlocks[0] >> 8 & 0xF) + this.pos.chunkZPos * 16;
                BlockPos blockpos = new BlockPos(i2, j2, k2);
                this.sendPacket(new SPacketBlockChange(this.playerChunkMap.getWorldServer(), blockpos));
                if (this.playerChunkMap.getWorldServer().getBlockState(blockpos).getBlock().hasTileEntity()) {
                    this.sendBlockEntity(this.playerChunkMap.getWorldServer().getTileEntity(blockpos));
                }
            } else if (this.changes == 64) {
                this.sendPacket(new SPacketChunkData(this.chunk, this.changedSectionFilter));
            } else {
                this.sendPacket(new SPacketMultiBlockChange(this.changes, this.changedBlocks, this.chunk));
                for (int l2 = 0; l2 < this.changes; ++l2) {
                    int i1 = (this.changedBlocks[l2] >> 12 & 0xF) + this.pos.chunkXPos * 16;
                    int j1 = this.changedBlocks[l2] & 0xFF;
                    int k1 = (this.changedBlocks[l2] >> 8 & 0xF) + this.pos.chunkZPos * 16;
                    BlockPos blockpos1 = new BlockPos(i1, j1, k1);
                    if (!this.playerChunkMap.getWorldServer().getBlockState(blockpos1).getBlock().hasTileEntity()) continue;
                    this.sendBlockEntity(this.playerChunkMap.getWorldServer().getTileEntity(blockpos1));
                }
            }
            this.changes = 0;
            this.changedSectionFilter = 0;
        }
    }

    private void sendBlockEntity(@Nullable TileEntity be2) {
        SPacketUpdateTileEntity spacketupdatetileentity;
        if (be2 != null && (spacketupdatetileentity = be2.getUpdatePacket()) != null) {
            this.sendPacket(spacketupdatetileentity);
        }
    }

    public boolean containsPlayer(EntityPlayerMP player) {
        return this.players.contains(player);
    }

    public boolean hasPlayerMatching(Predicate<EntityPlayerMP> predicate) {
        return Iterables.tryFind(this.players, predicate).isPresent();
    }

    public boolean hasPlayerMatchingInRange(double range, Predicate<EntityPlayerMP> predicate) {
        int j2 = this.players.size();
        for (int i2 = 0; i2 < j2; ++i2) {
            EntityPlayerMP entityplayermp = this.players.get(i2);
            if (!predicate.apply(entityplayermp) || !(this.pos.getDistanceSq(entityplayermp) < range * range)) continue;
            return true;
        }
        return false;
    }

    public boolean isSentToPlayers() {
        return this.sentToPlayers;
    }

    @Nullable
    public Chunk getChunk() {
        return this.chunk;
    }

    public double getClosestPlayerDistance() {
        double d0 = Double.MAX_VALUE;
        for (EntityPlayerMP entityplayermp : this.players) {
            double d1 = this.pos.getDistanceSq(entityplayermp);
            if (!(d1 < d0)) continue;
            d0 = d1;
        }
        return d0;
    }
}


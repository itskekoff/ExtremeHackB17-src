package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class SPacketMaps
implements Packet<INetHandlerPlayClient> {
    private int mapId;
    private byte mapScale;
    private boolean trackingPosition;
    private MapDecoration[] icons;
    private int minX;
    private int minZ;
    private int columns;
    private int rows;
    private byte[] mapDataBytes;

    public SPacketMaps() {
    }

    public SPacketMaps(int mapIdIn, byte mapScaleIn, boolean trackingPositionIn, Collection<MapDecoration> iconsIn, byte[] p_i46937_5_, int minXIn, int minZIn, int columnsIn, int rowsIn) {
        this.mapId = mapIdIn;
        this.mapScale = mapScaleIn;
        this.trackingPosition = trackingPositionIn;
        this.icons = iconsIn.toArray(new MapDecoration[iconsIn.size()]);
        this.minX = minXIn;
        this.minZ = minZIn;
        this.columns = columnsIn;
        this.rows = rowsIn;
        this.mapDataBytes = new byte[columnsIn * rowsIn];
        for (int i2 = 0; i2 < columnsIn; ++i2) {
            for (int j2 = 0; j2 < rowsIn; ++j2) {
                this.mapDataBytes[i2 + j2 * columnsIn] = p_i46937_5_[minXIn + i2 + (minZIn + j2) * 128];
            }
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.mapId = buf2.readVarIntFromBuffer();
        this.mapScale = buf2.readByte();
        this.trackingPosition = buf2.readBoolean();
        this.icons = new MapDecoration[buf2.readVarIntFromBuffer()];
        for (int i2 = 0; i2 < this.icons.length; ++i2) {
            short short1 = buf2.readByte();
            this.icons[i2] = new MapDecoration(MapDecoration.Type.func_191159_a((byte)(short1 >> 4 & 0xF)), buf2.readByte(), buf2.readByte(), (byte)(short1 & 0xF));
        }
        this.columns = buf2.readUnsignedByte();
        if (this.columns > 0) {
            this.rows = buf2.readUnsignedByte();
            this.minX = buf2.readUnsignedByte();
            this.minZ = buf2.readUnsignedByte();
            this.mapDataBytes = buf2.readByteArray();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.mapId);
        buf2.writeByte(this.mapScale);
        buf2.writeBoolean(this.trackingPosition);
        buf2.writeVarIntToBuffer(this.icons.length);
        MapDecoration[] arrmapDecoration = this.icons;
        int n2 = this.icons.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            MapDecoration mapdecoration = arrmapDecoration[i2];
            buf2.writeByte((mapdecoration.getType() & 0xF) << 4 | mapdecoration.getRotation() & 0xF);
            buf2.writeByte(mapdecoration.getX());
            buf2.writeByte(mapdecoration.getY());
        }
        buf2.writeByte(this.columns);
        if (this.columns > 0) {
            buf2.writeByte(this.rows);
            buf2.writeByte(this.minX);
            buf2.writeByte(this.minZ);
            buf2.writeByteArray(this.mapDataBytes);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMaps(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void setMapdataTo(MapData mapdataIn) {
        mapdataIn.scale = this.mapScale;
        mapdataIn.trackingPosition = this.trackingPosition;
        mapdataIn.mapDecorations.clear();
        for (int i2 = 0; i2 < this.icons.length; ++i2) {
            MapDecoration mapdecoration = this.icons[i2];
            mapdataIn.mapDecorations.put("icon-" + i2, mapdecoration);
        }
        for (int j2 = 0; j2 < this.columns; ++j2) {
            for (int k2 = 0; k2 < this.rows; ++k2) {
                mapdataIn.colors[this.minX + j2 + (this.minZ + k2) * 128] = this.mapDataBytes[j2 + k2 * this.columns];
            }
        }
    }
}


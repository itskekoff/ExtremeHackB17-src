package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
    private static final byte[] EMPTY_SECTOR = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private List<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified;

    public RegionFile(File fileNameIn) {
        this.fileName = fileNameIn;
        this.sizeDelta = 0;
        try {
            if (fileNameIn.exists()) {
                this.lastModified = fileNameIn.lastModified();
            }
            this.dataFile = new RandomAccessFile(fileNameIn, "rw");
            if (this.dataFile.length() < 4096L) {
                this.dataFile.write(EMPTY_SECTOR);
                this.dataFile.write(EMPTY_SECTOR);
                this.sizeDelta += 8192;
            }
            if ((this.dataFile.length() & 0xFFFL) != 0L) {
                int i2 = 0;
                while ((long)i2 < (this.dataFile.length() & 0xFFFL)) {
                    this.dataFile.write(0);
                    ++i2;
                }
            }
            int i1 = (int)this.dataFile.length() / 4096;
            this.sectorFree = Lists.newArrayListWithCapacity(i1);
            for (int j2 = 0; j2 < i1; ++j2) {
                this.sectorFree.add(true);
            }
            this.sectorFree.set(0, false);
            this.sectorFree.set(1, false);
            this.dataFile.seek(0L);
            for (int j1 = 0; j1 < 1024; ++j1) {
                int k2;
                this.offsets[j1] = k2 = this.dataFile.readInt();
                if (k2 == 0 || (k2 >> 8) + (k2 & 0xFF) > this.sectorFree.size()) continue;
                for (int l2 = 0; l2 < (k2 & 0xFF); ++l2) {
                    this.sectorFree.set((k2 >> 8) + l2, false);
                }
            }
            for (int k1 = 0; k1 < 1024; ++k1) {
                int l1;
                this.chunkTimestamps[k1] = l1 = this.dataFile.readInt();
            }
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(int x2, int z2) {
        int l2;
        block12: {
            block11: {
                int k2;
                int j2;
                block10: {
                    int i2;
                    block9: {
                        if (this.outOfBounds(x2, z2)) {
                            return null;
                        }
                        try {
                            i2 = this.getOffset(x2, z2);
                            if (i2 != 0) break block9;
                            return null;
                        }
                        catch (IOException var9) {
                            return null;
                        }
                    }
                    j2 = i2 >> 8;
                    k2 = i2 & 0xFF;
                    if (j2 + k2 <= this.sectorFree.size()) break block10;
                    return null;
                }
                this.dataFile.seek(j2 * 4096);
                l2 = this.dataFile.readInt();
                if (l2 <= 4096 * k2) break block11;
                return null;
            }
            if (l2 > 0) break block12;
            return null;
        }
        byte b0 = this.dataFile.readByte();
        if (b0 == 1) {
            byte[] abyte1 = new byte[l2 - 1];
            this.dataFile.read(abyte1);
            return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte1))));
        }
        if (b0 == 2) {
            byte[] abyte = new byte[l2 - 1];
            this.dataFile.read(abyte);
            return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
        }
        return null;
    }

    @Nullable
    public DataOutputStream getChunkDataOutputStream(int x2, int z2) {
        return this.outOfBounds(x2, z2) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ChunkBuffer(x2, z2))));
    }

    protected synchronized void write(int x2, int z2, byte[] data, int length) {
        try {
            int i2 = this.getOffset(x2, z2);
            int j2 = i2 >> 8;
            int k2 = i2 & 0xFF;
            int l2 = (length + 5) / 4096 + 1;
            if (l2 >= 256) {
                return;
            }
            if (j2 != 0 && k2 == l2) {
                this.write(j2, data, length);
            } else {
                for (int i1 = 0; i1 < k2; ++i1) {
                    this.sectorFree.set(j2 + i1, true);
                }
                int l1 = this.sectorFree.indexOf(true);
                int j1 = 0;
                if (l1 != -1) {
                    for (int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                        if (j1 != 0) {
                            j1 = this.sectorFree.get(k1).booleanValue() ? ++j1 : 0;
                        } else if (this.sectorFree.get(k1).booleanValue()) {
                            l1 = k1;
                            j1 = 1;
                        }
                        if (j1 >= l2) break;
                    }
                }
                if (j1 >= l2) {
                    j2 = l1;
                    this.setOffset(x2, z2, l1 << 8 | l2);
                    for (int j22 = 0; j22 < l2; ++j22) {
                        this.sectorFree.set(j2 + j22, false);
                    }
                    this.write(j2, data, length);
                } else {
                    this.dataFile.seek(this.dataFile.length());
                    j2 = this.sectorFree.size();
                    for (int i22 = 0; i22 < l2; ++i22) {
                        this.dataFile.write(EMPTY_SECTOR);
                        this.sectorFree.add(false);
                    }
                    this.sizeDelta += 4096 * l2;
                    this.write(j2, data, length);
                    this.setOffset(x2, z2, j2 << 8 | l2);
                }
            }
            this.setChunkTimestamp(x2, z2, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        this.dataFile.seek(sectorNumber * 4096);
        this.dataFile.writeInt(length + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(data, 0, length);
    }

    private boolean outOfBounds(int x2, int z2) {
        return x2 < 0 || x2 >= 32 || z2 < 0 || z2 >= 32;
    }

    private int getOffset(int x2, int z2) {
        return this.offsets[x2 + z2 * 32];
    }

    public boolean isChunkSaved(int x2, int z2) {
        return this.getOffset(x2, z2) != 0;
    }

    private void setOffset(int x2, int z2, int offset) throws IOException {
        this.offsets[x2 + z2 * 32] = offset;
        this.dataFile.seek((x2 + z2 * 32) * 4);
        this.dataFile.writeInt(offset);
    }

    private void setChunkTimestamp(int x2, int z2, int timestamp) throws IOException {
        this.chunkTimestamps[x2 + z2 * 32] = timestamp;
        this.dataFile.seek(4096 + (x2 + z2 * 32) * 4);
        this.dataFile.writeInt(timestamp);
    }

    public void close() throws IOException {
        if (this.dataFile != null) {
            this.dataFile.close();
        }
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
        private final int chunkX;
        private final int chunkZ;

        public ChunkBuffer(int x2, int z2) {
            super(8096);
            this.chunkX = x2;
            this.chunkZ = z2;
        }

        @Override
        public void close() throws IOException {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}


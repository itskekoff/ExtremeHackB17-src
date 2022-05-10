package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.WorldSavedData;

public class MapData
extends WorldSavedData {
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public boolean trackingPosition;
    public boolean field_191096_f;
    public byte scale;
    public byte[] colors = new byte[16384];
    public List<MapInfo> playersArrayList = Lists.newArrayList();
    private final Map<EntityPlayer, MapInfo> playersHashMap = Maps.newHashMap();
    public Map<String, MapDecoration> mapDecorations = Maps.newLinkedHashMap();

    public MapData(String mapname) {
        super(mapname);
    }

    public void calculateMapCenter(double x2, double z2, int mapScale) {
        int i2 = 128 * (1 << mapScale);
        int j2 = MathHelper.floor((x2 + 64.0) / (double)i2);
        int k2 = MathHelper.floor((z2 + 64.0) / (double)i2);
        this.xCenter = j2 * i2 + i2 / 2 - 64;
        this.zCenter = k2 * i2 + i2 / 2 - 64;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dimension = nbt.getByte("dimension");
        this.xCenter = nbt.getInteger("xCenter");
        this.zCenter = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        this.scale = (byte)MathHelper.clamp(this.scale, 0, 4);
        this.trackingPosition = nbt.hasKey("trackingPosition", 1) ? nbt.getBoolean("trackingPosition") : true;
        this.field_191096_f = nbt.getBoolean("unlimitedTracking");
        int i2 = nbt.getShort("width");
        int j2 = nbt.getShort("height");
        if (i2 == 128 && j2 == 128) {
            this.colors = nbt.getByteArray("colors");
        } else {
            byte[] abyte = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            int k2 = (128 - i2) / 2;
            int l2 = (128 - j2) / 2;
            for (int i1 = 0; i1 < j2; ++i1) {
                int j1 = i1 + l2;
                if (j1 < 0 && j1 >= 128) continue;
                for (int k1 = 0; k1 < i2; ++k1) {
                    int l1 = k1 + k2;
                    if (l1 < 0 && l1 >= 128) continue;
                    this.colors[l1 + j1 * 128] = abyte[k1 + i1 * i2];
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setByte("dimension", this.dimension);
        compound.setInteger("xCenter", this.xCenter);
        compound.setInteger("zCenter", this.zCenter);
        compound.setByte("scale", this.scale);
        compound.setShort("width", (short)128);
        compound.setShort("height", (short)128);
        compound.setByteArray("colors", this.colors);
        compound.setBoolean("trackingPosition", this.trackingPosition);
        compound.setBoolean("unlimitedTracking", this.field_191096_f);
        return compound;
    }

    public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
        if (!this.playersHashMap.containsKey(player)) {
            MapInfo mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        if (!player.inventory.hasItemStack(mapStack)) {
            this.mapDecorations.remove(player.getName());
        }
        for (int i2 = 0; i2 < this.playersArrayList.size(); ++i2) {
            MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i2);
            if (!mapdata$mapinfo1.entityplayerObj.isDead && (mapdata$mapinfo1.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
                if (mapStack.isOnItemFrame() || mapdata$mapinfo1.entityplayerObj.dimension != this.dimension || !this.trackingPosition) continue;
                this.func_191095_a(MapDecoration.Type.PLAYER, mapdata$mapinfo1.entityplayerObj.world, mapdata$mapinfo1.entityplayerObj.getName(), mapdata$mapinfo1.entityplayerObj.posX, mapdata$mapinfo1.entityplayerObj.posZ, mapdata$mapinfo1.entityplayerObj.rotationYaw);
                continue;
            }
            this.playersHashMap.remove(mapdata$mapinfo1.entityplayerObj);
            this.playersArrayList.remove(mapdata$mapinfo1);
        }
        if (mapStack.isOnItemFrame() && this.trackingPosition) {
            EntityItemFrame entityitemframe = mapStack.getItemFrame();
            BlockPos blockpos = entityitemframe.getHangingPosition();
            this.func_191095_a(MapDecoration.Type.FRAME, player.world, "frame-" + entityitemframe.getEntityId(), blockpos.getX(), blockpos.getZ(), entityitemframe.facingDirection.getHorizontalIndex() * 90);
        }
        if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
            NBTTagList nbttaglist = mapStack.getTagCompound().getTagList("Decorations", 10);
            for (int j2 = 0; j2 < nbttaglist.tagCount(); ++j2) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j2);
                if (this.mapDecorations.containsKey(nbttagcompound.getString("id"))) continue;
                this.func_191095_a(MapDecoration.Type.func_191159_a(nbttagcompound.getByte("type")), player.world, nbttagcompound.getString("id"), nbttagcompound.getDouble("x"), nbttagcompound.getDouble("z"), nbttagcompound.getDouble("rot"));
            }
        }
    }

    public static void func_191094_a(ItemStack p_191094_0_, BlockPos p_191094_1_, String p_191094_2_, MapDecoration.Type p_191094_3_) {
        NBTTagList nbttaglist;
        if (p_191094_0_.hasTagCompound() && p_191094_0_.getTagCompound().hasKey("Decorations", 9)) {
            nbttaglist = p_191094_0_.getTagCompound().getTagList("Decorations", 10);
        } else {
            nbttaglist = new NBTTagList();
            p_191094_0_.setTagInfo("Decorations", nbttaglist);
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setByte("type", p_191094_3_.func_191163_a());
        nbttagcompound.setString("id", p_191094_2_);
        nbttagcompound.setDouble("x", p_191094_1_.getX());
        nbttagcompound.setDouble("z", p_191094_1_.getZ());
        nbttagcompound.setDouble("rot", 180.0);
        nbttaglist.appendTag(nbttagcompound);
        if (p_191094_3_.func_191162_c()) {
            NBTTagCompound nbttagcompound1 = p_191094_0_.func_190925_c("display");
            nbttagcompound1.setInteger("MapColor", p_191094_3_.func_191161_d());
        }
    }

    private void func_191095_a(MapDecoration.Type p_191095_1_, World p_191095_2_, String p_191095_3_, double p_191095_4_, double p_191095_6_, double p_191095_8_) {
        byte b2;
        int i2 = 1 << this.scale;
        float f2 = (float)(p_191095_4_ - (double)this.xCenter) / (float)i2;
        float f1 = (float)(p_191095_6_ - (double)this.zCenter) / (float)i2;
        byte b0 = (byte)((double)(f2 * 2.0f) + 0.5);
        byte b1 = (byte)((double)(f1 * 2.0f) + 0.5);
        int j2 = 63;
        if (f2 >= -63.0f && f1 >= -63.0f && f2 <= 63.0f && f1 <= 63.0f) {
            b2 = (byte)((p_191095_8_ += p_191095_8_ < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension < 0) {
                int l2 = (int)(p_191095_2_.getWorldInfo().getWorldTime() / 10L);
                b2 = (byte)(l2 * l2 * 34187121 + l2 * 121 >> 15 & 0xF);
            }
        } else {
            if (p_191095_1_ != MapDecoration.Type.PLAYER) {
                this.mapDecorations.remove(p_191095_3_);
                return;
            }
            int k2 = 320;
            if (Math.abs(f2) < 320.0f && Math.abs(f1) < 320.0f) {
                p_191095_1_ = MapDecoration.Type.PLAYER_OFF_MAP;
            } else {
                if (!this.field_191096_f) {
                    this.mapDecorations.remove(p_191095_3_);
                    return;
                }
                p_191095_1_ = MapDecoration.Type.PLAYER_OFF_LIMITS;
            }
            b2 = 0;
            if (f2 <= -63.0f) {
                b0 = -128;
            }
            if (f1 <= -63.0f) {
                b1 = -128;
            }
            if (f2 >= 63.0f) {
                b0 = 127;
            }
            if (f1 >= 63.0f) {
                b1 = 127;
            }
        }
        this.mapDecorations.put(p_191095_3_, new MapDecoration(p_191095_1_, b0, b1, b2));
    }

    @Nullable
    public Packet<?> getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
    }

    public void updateMapData(int x2, int y2) {
        super.markDirty();
        for (MapInfo mapdata$mapinfo : this.playersArrayList) {
            mapdata$mapinfo.update(x2, y2);
        }
    }

    public MapInfo getMapInfo(EntityPlayer player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        if (mapdata$mapinfo == null) {
            mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        return mapdata$mapinfo;
    }

    public class MapInfo {
        public final EntityPlayer entityplayerObj;
        private boolean isDirty = true;
        private int minX;
        private int minY;
        private int maxX = 127;
        private int maxY = 127;
        private int tick;
        public int step;

        public MapInfo(EntityPlayer player) {
            this.entityplayerObj = player;
        }

        @Nullable
        public Packet<?> getPacket(ItemStack stack) {
            if (this.isDirty) {
                this.isDirty = false;
                return new SPacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            }
            return this.tick++ % 5 == 0 ? new SPacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
        }

        public void update(int x2, int y2) {
            if (this.isDirty) {
                this.minX = Math.min(this.minX, x2);
                this.minY = Math.min(this.minY, y2);
                this.maxX = Math.max(this.maxX, x2);
                this.maxY = Math.max(this.maxY, y2);
            } else {
                this.isDirty = true;
                this.minX = x2;
                this.minY = y2;
                this.maxX = x2;
                this.maxY = y2;
            }
        }
    }
}


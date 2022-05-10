package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;

public class Village {
    private World worldObj;
    private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
    private BlockPos centerHelper = BlockPos.ORIGIN;
    private BlockPos center = BlockPos.ORIGIN;
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numVillagers;
    private int noBreedTicks;
    private final Map<String, Integer> playerReputation = Maps.newHashMap();
    private final List<VillageAggressor> villageAgressors = Lists.newArrayList();
    private int numIronGolems;

    public Village() {
    }

    public Village(World worldIn) {
        this.worldObj = worldIn;
    }

    public void setWorld(World worldIn) {
        this.worldObj = worldIn;
    }

    public void tick(int tickCounterIn) {
        Vec3d vec3d;
        int i2;
        this.tickCounter = tickCounterIn;
        this.removeDeadAndOutOfRangeDoors();
        this.removeDeadAndOldAgressors();
        if (tickCounterIn % 20 == 0) {
            this.updateNumVillagers();
        }
        if (tickCounterIn % 30 == 0) {
            this.updateNumIronGolems();
        }
        if (this.numIronGolems < (i2 = this.numVillagers / 10) && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0 && (vec3d = this.findRandomSpawnPos(this.center, 2, 4, 2)) != null) {
            EntityIronGolem entityirongolem = new EntityIronGolem(this.worldObj);
            entityirongolem.setPosition(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
            this.worldObj.spawnEntityInWorld(entityirongolem);
            ++this.numIronGolems;
        }
    }

    private Vec3d findRandomSpawnPos(BlockPos pos, int x2, int y2, int z2) {
        for (int i2 = 0; i2 < 10; ++i2) {
            BlockPos blockpos = pos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
            if (!this.isBlockPosWithinSqVillageRadius(blockpos) || !this.isAreaClearAround(new BlockPos(x2, y2, z2), blockpos)) continue;
            return new Vec3d(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        }
        return null;
    }

    private boolean isAreaClearAround(BlockPos blockSize, BlockPos blockLocation) {
        if (!this.worldObj.getBlockState(blockLocation.down()).isFullyOpaque()) {
            return false;
        }
        int i2 = blockLocation.getX() - blockSize.getX() / 2;
        int j2 = blockLocation.getZ() - blockSize.getZ() / 2;
        for (int k2 = i2; k2 < i2 + blockSize.getX(); ++k2) {
            for (int l2 = blockLocation.getY(); l2 < blockLocation.getY() + blockSize.getY(); ++l2) {
                for (int i1 = j2; i1 < j2 + blockSize.getZ(); ++i1) {
                    if (!this.worldObj.getBlockState(new BlockPos(k2, l2, i1)).isNormalCube()) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private void updateNumIronGolems() {
        List<EntityIronGolem> list = this.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, new AxisAlignedBB(this.center.getX() - this.villageRadius, this.center.getY() - 4, this.center.getZ() - this.villageRadius, this.center.getX() + this.villageRadius, this.center.getY() + 4, this.center.getZ() + this.villageRadius));
        this.numIronGolems = list.size();
    }

    private void updateNumVillagers() {
        List<EntityVillager> list = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(this.center.getX() - this.villageRadius, this.center.getY() - 4, this.center.getZ() - this.villageRadius, this.center.getX() + this.villageRadius, this.center.getY() + 4, this.center.getZ() + this.villageRadius));
        this.numVillagers = list.size();
        if (this.numVillagers == 0) {
            this.playerReputation.clear();
        }
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public int getVillageRadius() {
        return this.villageRadius;
    }

    public int getNumVillageDoors() {
        return this.villageDoorInfoList.size();
    }

    public int getTicksSinceLastDoorAdding() {
        return this.tickCounter - this.lastAddDoorTimestamp;
    }

    public int getNumVillagers() {
        return this.numVillagers;
    }

    public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
        return this.center.distanceSq(pos) < (double)(this.villageRadius * this.villageRadius);
    }

    public List<VillageDoorInfo> getVillageDoorInfoList() {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo getNearestDoor(BlockPos pos) {
        VillageDoorInfo villagedoorinfo = null;
        int i2 = Integer.MAX_VALUE;
        for (VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
            int j2 = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
            if (j2 >= i2) continue;
            villagedoorinfo = villagedoorinfo1;
            i2 = j2;
        }
        return villagedoorinfo;
    }

    public VillageDoorInfo getDoorInfo(BlockPos pos) {
        VillageDoorInfo villagedoorinfo = null;
        int i2 = Integer.MAX_VALUE;
        for (VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
            EnumFacing enumfacing;
            BlockPos blockpos;
            int j2 = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
            j2 = j2 > 256 ? (j2 *= 1000) : villagedoorinfo1.getDoorOpeningRestrictionCounter();
            if (j2 >= i2 || !this.worldObj.getBlockState((blockpos = villagedoorinfo1.getDoorBlockPos()).offset(enumfacing = villagedoorinfo1.getInsideDirection(), 1)).getBlock().isPassable(this.worldObj, blockpos.offset(enumfacing, 1)) || !this.worldObj.getBlockState(blockpos.offset(enumfacing, -1)).getBlock().isPassable(this.worldObj, blockpos.offset(enumfacing, -1)) || !this.worldObj.getBlockState(blockpos.up().offset(enumfacing, 1)).getBlock().isPassable(this.worldObj, blockpos.up().offset(enumfacing, 1)) || !this.worldObj.getBlockState(blockpos.up().offset(enumfacing, -1)).getBlock().isPassable(this.worldObj, blockpos.up().offset(enumfacing, -1))) continue;
            villagedoorinfo = villagedoorinfo1;
            i2 = j2;
        }
        return villagedoorinfo;
    }

    @Nullable
    public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
        if (this.center.distanceSq(doorBlock) > (double)(this.villageRadius * this.villageRadius)) {
            return null;
        }
        for (VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            if (villagedoorinfo.getDoorBlockPos().getX() != doorBlock.getX() || villagedoorinfo.getDoorBlockPos().getZ() != doorBlock.getZ() || Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) > 1) continue;
            return villagedoorinfo;
        }
        return null;
    }

    public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
        this.villageDoorInfoList.add(doorInfo);
        this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
        this.updateVillageRadiusAndCenter();
        this.lastAddDoorTimestamp = doorInfo.getInsidePosY();
    }

    public boolean isAnnihilated() {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
        for (VillageAggressor village$villageaggressor : this.villageAgressors) {
            if (village$villageaggressor.agressor != entitylivingbaseIn) continue;
            village$villageaggressor.agressionTime = this.tickCounter;
            return;
        }
        this.villageAgressors.add(new VillageAggressor(entitylivingbaseIn, this.tickCounter));
    }

    @Nullable
    public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
        double d0 = Double.MAX_VALUE;
        VillageAggressor village$villageaggressor = null;
        for (int i2 = 0; i2 < this.villageAgressors.size(); ++i2) {
            VillageAggressor village$villageaggressor1 = this.villageAgressors.get(i2);
            double d1 = village$villageaggressor1.agressor.getDistanceSqToEntity(entitylivingbaseIn);
            if (!(d1 <= d0)) continue;
            village$villageaggressor = village$villageaggressor1;
            d0 = d1;
        }
        return village$villageaggressor == null ? null : village$villageaggressor.agressor;
    }

    public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
        double d0 = Double.MAX_VALUE;
        EntityPlayer entityplayer = null;
        for (String s2 : this.playerReputation.keySet()) {
            double d1;
            EntityPlayer entityplayer1;
            if (!this.isPlayerReputationTooLow(s2) || (entityplayer1 = this.worldObj.getPlayerEntityByName(s2)) == null || !((d1 = entityplayer1.getDistanceSqToEntity(villageDefender)) <= d0)) continue;
            entityplayer = entityplayer1;
            d0 = d1;
        }
        return entityplayer;
    }

    private void removeDeadAndOldAgressors() {
        Iterator<VillageAggressor> iterator = this.villageAgressors.iterator();
        while (iterator.hasNext()) {
            VillageAggressor village$villageaggressor = iterator.next();
            if (village$villageaggressor.agressor.isEntityAlive() && Math.abs(this.tickCounter - village$villageaggressor.agressionTime) <= 300) continue;
            iterator.remove();
        }
    }

    private void removeDeadAndOutOfRangeDoors() {
        boolean flag = false;
        boolean flag1 = this.worldObj.rand.nextInt(50) == 0;
        Iterator<VillageDoorInfo> iterator = this.villageDoorInfoList.iterator();
        while (iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo = iterator.next();
            if (flag1) {
                villagedoorinfo.resetDoorOpeningRestrictionCounter();
            }
            if (this.isWoodDoor(villagedoorinfo.getDoorBlockPos()) && Math.abs(this.tickCounter - villagedoorinfo.getInsidePosY()) <= 1200) continue;
            this.centerHelper = this.centerHelper.subtract(villagedoorinfo.getDoorBlockPos());
            flag = true;
            villagedoorinfo.setIsDetachedFromVillageFlag(true);
            iterator.remove();
        }
        if (flag) {
            this.updateVillageRadiusAndCenter();
        }
    }

    private boolean isWoodDoor(BlockPos pos) {
        IBlockState iblockstate = this.worldObj.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (block instanceof BlockDoor) {
            return iblockstate.getMaterial() == Material.WOOD;
        }
        return false;
    }

    private void updateVillageRadiusAndCenter() {
        int i2 = this.villageDoorInfoList.size();
        if (i2 == 0) {
            this.center = BlockPos.ORIGIN;
            this.villageRadius = 0;
        } else {
            this.center = new BlockPos(this.centerHelper.getX() / i2, this.centerHelper.getY() / i2, this.centerHelper.getZ() / i2);
            int j2 = 0;
            for (VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
                j2 = Math.max(villagedoorinfo.getDistanceToDoorBlockSq(this.center), j2);
            }
            this.villageRadius = Math.max(32, (int)Math.sqrt(j2) + 1);
        }
    }

    public int getPlayerReputation(String playerName) {
        Integer integer = this.playerReputation.get(playerName);
        return integer == null ? 0 : integer;
    }

    public int modifyPlayerReputation(String playerName, int reputation) {
        int i2 = this.getPlayerReputation(playerName);
        int j2 = MathHelper.clamp(i2 + reputation, -30, 10);
        this.playerReputation.put(playerName, j2);
        return j2;
    }

    public boolean isPlayerReputationTooLow(String playerName) {
        return this.getPlayerReputation(playerName) <= -15;
    }

    public void readVillageDataFromNBT(NBTTagCompound compound) {
        this.numVillagers = compound.getInteger("PopSize");
        this.villageRadius = compound.getInteger("Radius");
        this.numIronGolems = compound.getInteger("Golems");
        this.lastAddDoorTimestamp = compound.getInteger("Stable");
        this.tickCounter = compound.getInteger("Tick");
        this.noBreedTicks = compound.getInteger("MTick");
        this.center = new BlockPos(compound.getInteger("CX"), compound.getInteger("CY"), compound.getInteger("CZ"));
        this.centerHelper = new BlockPos(compound.getInteger("ACX"), compound.getInteger("ACY"), compound.getInteger("ACZ"));
        NBTTagList nbttaglist = compound.getTagList("Doors", 10);
        for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            VillageDoorInfo villagedoorinfo = new VillageDoorInfo(new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z")), nbttagcompound.getInteger("IDX"), nbttagcompound.getInteger("IDZ"), nbttagcompound.getInteger("TS"));
            this.villageDoorInfoList.add(villagedoorinfo);
        }
        NBTTagList nbttaglist1 = compound.getTagList("Players", 10);
        for (int j2 = 0; j2 < nbttaglist1.tagCount(); ++j2) {
            NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(j2);
            if (nbttagcompound1.hasKey("UUID") && this.worldObj != null && this.worldObj.getMinecraftServer() != null) {
                PlayerProfileCache playerprofilecache = this.worldObj.getMinecraftServer().getPlayerProfileCache();
                GameProfile gameprofile = playerprofilecache.getProfileByUUID(UUID.fromString(nbttagcompound1.getString("UUID")));
                if (gameprofile == null) continue;
                this.playerReputation.put(gameprofile.getName(), nbttagcompound1.getInteger("S"));
                continue;
            }
            this.playerReputation.put(nbttagcompound1.getString("Name"), nbttagcompound1.getInteger("S"));
        }
    }

    public void writeVillageDataToNBT(NBTTagCompound compound) {
        compound.setInteger("PopSize", this.numVillagers);
        compound.setInteger("Radius", this.villageRadius);
        compound.setInteger("Golems", this.numIronGolems);
        compound.setInteger("Stable", this.lastAddDoorTimestamp);
        compound.setInteger("Tick", this.tickCounter);
        compound.setInteger("MTick", this.noBreedTicks);
        compound.setInteger("CX", this.center.getX());
        compound.setInteger("CY", this.center.getY());
        compound.setInteger("CZ", this.center.getZ());
        compound.setInteger("ACX", this.centerHelper.getX());
        compound.setInteger("ACY", this.centerHelper.getY());
        compound.setInteger("ACZ", this.centerHelper.getZ());
        NBTTagList nbttaglist = new NBTTagList();
        for (VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("X", villagedoorinfo.getDoorBlockPos().getX());
            nbttagcompound.setInteger("Y", villagedoorinfo.getDoorBlockPos().getY());
            nbttagcompound.setInteger("Z", villagedoorinfo.getDoorBlockPos().getZ());
            nbttagcompound.setInteger("IDX", villagedoorinfo.getInsideOffsetX());
            nbttagcompound.setInteger("IDZ", villagedoorinfo.getInsideOffsetZ());
            nbttagcompound.setInteger("TS", villagedoorinfo.getInsidePosY());
            nbttaglist.appendTag(nbttagcompound);
        }
        compound.setTag("Doors", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        for (String s2 : this.playerReputation.keySet()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            PlayerProfileCache playerprofilecache = this.worldObj.getMinecraftServer().getPlayerProfileCache();
            try {
                GameProfile gameprofile = playerprofilecache.getGameProfileForUsername(s2);
                if (gameprofile == null) continue;
                nbttagcompound1.setString("UUID", gameprofile.getId().toString());
                nbttagcompound1.setInteger("S", this.playerReputation.get(s2));
                nbttaglist1.appendTag(nbttagcompound1);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        compound.setTag("Players", nbttaglist1);
    }

    public void endMatingSeason() {
        this.noBreedTicks = this.tickCounter;
    }

    public boolean isMatingSeason() {
        return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
    }

    public void setDefaultPlayerReputation(int defaultReputation) {
        for (String s2 : this.playerReputation.keySet()) {
            this.modifyPlayerReputation(s2, defaultReputation);
        }
    }

    class VillageAggressor {
        public EntityLivingBase agressor;
        public int agressionTime;

        VillageAggressor(EntityLivingBase agressorIn, int agressionTimeIn) {
            this.agressor = agressorIn;
            this.agressionTime = agressionTimeIn;
        }
    }
}


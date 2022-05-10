package net.minecraft.world.gen.structure;

import java.util.Map;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureMineshaftStart;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenMineshaft
extends MapGenStructure {
    private double chance = 0.004;

    public MapGenMineshaft() {
    }

    @Override
    public String getStructureName() {
        return "Mineshaft";
    }

    public MapGenMineshaft(Map<String, String> p_i2034_1_) {
        for (Map.Entry<String, String> entry : p_i2034_1_.entrySet()) {
            if (!entry.getKey().equals("chance")) continue;
            this.chance = MathHelper.getDouble(entry.getValue(), this.chance);
        }
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return this.rand.nextDouble() < this.chance && this.rand.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
    }

    @Override
    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean p_180706_3_) {
        int i2 = 1000;
        int j2 = pos.getX() >> 4;
        int k2 = pos.getZ() >> 4;
        for (int l2 = 0; l2 <= 1000; ++l2) {
            for (int i1 = -l2; i1 <= l2; ++i1) {
                boolean flag = i1 == -l2 || i1 == l2;
                for (int j1 = -l2; j1 <= l2; ++j1) {
                    boolean flag1;
                    boolean bl2 = flag1 = j1 == -l2 || j1 == l2;
                    if (!flag && !flag1) continue;
                    int k1 = j2 + i1;
                    int l1 = k2 + j1;
                    this.rand.setSeed((long)(k1 ^ l1) ^ worldIn.getSeed());
                    this.rand.nextInt();
                    if (!this.canSpawnStructureAtCoords(k1, l1) || p_180706_3_ && worldIn.func_190526_b(k1, l1)) continue;
                    return new BlockPos((k1 << 4) + 8, 64, (l1 << 4) + 8);
                }
            }
        }
        return null;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        Biome biome = this.worldObj.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
        Type mapgenmineshaft$type = biome instanceof BiomeMesa ? Type.MESA : Type.NORMAL;
        return new StructureMineshaftStart(this.worldObj, this.rand, chunkX, chunkZ, mapgenmineshaft$type);
    }

    public static enum Type {
        NORMAL,
        MESA;


        public static Type byId(int id2) {
            return id2 >= 0 && id2 < Type.values().length ? Type.values()[id2] : NORMAL;
        }
    }
}


package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomePlains
extends Biome {
    protected boolean sunflowers;

    protected BiomePlains(boolean p_i46699_1_, Biome.BiomeProperties properties) {
        super(properties);
        this.sunflowers = p_i46699_1_;
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityDonkey.class, 1, 1, 3));
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.extraTreeChance = 0.05f;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
    }

    @Override
    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
        double d0 = GRASS_COLOR_NOISE.getValue((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0);
        if (d0 < -0.8) {
            int j2 = rand.nextInt(4);
            switch (j2) {
                case 0: {
                    return BlockFlower.EnumFlowerType.ORANGE_TULIP;
                }
                case 1: {
                    return BlockFlower.EnumFlowerType.RED_TULIP;
                }
                case 2: {
                    return BlockFlower.EnumFlowerType.PINK_TULIP;
                }
            }
            return BlockFlower.EnumFlowerType.WHITE_TULIP;
        }
        if (rand.nextInt(3) > 0) {
            int i2 = rand.nextInt(3);
            if (i2 == 0) {
                return BlockFlower.EnumFlowerType.POPPY;
            }
            return i2 == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY;
        }
        return BlockFlower.EnumFlowerType.DANDELION;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        double d0 = GRASS_COLOR_NOISE.getValue((double)(pos.getX() + 8) / 200.0, (double)(pos.getZ() + 8) / 200.0);
        if (d0 < -0.8) {
            this.theBiomeDecorator.flowersPerChunk = 15;
            this.theBiomeDecorator.grassPerChunk = 5;
        } else {
            this.theBiomeDecorator.flowersPerChunk = 4;
            this.theBiomeDecorator.grassPerChunk = 10;
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);
            for (int i2 = 0; i2 < 7; ++i2) {
                int j2 = rand.nextInt(16) + 8;
                int k2 = rand.nextInt(16) + 8;
                int l2 = rand.nextInt(worldIn.getHeight(pos.add(j2, 0, k2)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j2, l2, k2));
            }
        }
        if (this.sunflowers) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);
            for (int i1 = 0; i1 < 10; ++i1) {
                int j1 = rand.nextInt(16) + 8;
                int k1 = rand.nextInt(16) + 8;
                int l1 = rand.nextInt(worldIn.getHeight(pos.add(j1, 0, k1)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j1, l1, k1));
            }
        }
        super.decorate(worldIn, rand, pos);
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return rand.nextInt(3) == 0 ? BIG_TREE_FEATURE : TREE_FEATURE;
    }
}


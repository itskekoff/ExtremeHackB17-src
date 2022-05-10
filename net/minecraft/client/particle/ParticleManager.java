package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleDragonBreath;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.client.particle.ParticleEmitter;
import net.minecraft.client.particle.ParticleEnchantmentTable;
import net.minecraft.client.particle.ParticleEndRod;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleExplosionHuge;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.client.particle.ParticleFallingDust;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleFootStep;
import net.minecraft.client.particle.ParticleHeart;
import net.minecraft.client.particle.ParticleLava;
import net.minecraft.client.particle.ParticleMobAppearance;
import net.minecraft.client.particle.ParticleNote;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.client.particle.ParticleSmokeLarge;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSnowShovel;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.particle.ParticleSpit;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.client.particle.ParticleSuspend;
import net.minecraft.client.particle.ParticleSuspendedTown;
import net.minecraft.client.particle.ParticleSweepAttack;
import net.minecraft.client.particle.ParticleTotem;
import net.minecraft.client.particle.ParticleWaterWake;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import optifine.Config;
import optifine.Reflector;

public class ParticleManager {
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
    protected World worldObj;
    private final ArrayDeque<Particle>[][] fxLayers = new ArrayDeque[4][];
    private final Queue<ParticleEmitter> particleEmitters = Queues.newArrayDeque();
    private final TextureManager renderer;
    private final Random rand = new Random();
    private final Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();
    private final Queue<Particle> queueEntityFX = Queues.newArrayDeque();

    public ParticleManager(World worldIn, TextureManager rendererIn) {
        this.worldObj = worldIn;
        this.renderer = rendererIn;
        for (int i2 = 0; i2 < 4; ++i2) {
            this.fxLayers[i2] = new ArrayDeque[2];
            for (int j2 = 0; j2 < 2; ++j2) {
                this.fxLayers[i2][j2] = Queues.newArrayDeque();
            }
        }
        this.registerVanillaParticles();
    }

    private void registerVanillaParticles() {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new ParticleExplosion.Factory());
        this.registerParticle(EnumParticleTypes.SPIT.getParticleID(), new ParticleSpit.Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new ParticleBubble.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new ParticleSplash.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new ParticleWaterWake.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new ParticleRain.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new ParticleSuspend.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new ParticleSuspendedTown.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new ParticleCrit.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new ParticleCrit.MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new ParticleSmokeNormal.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new ParticleSmokeLarge.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new ParticleSpell.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new ParticleSpell.InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new ParticleSpell.MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new ParticleSpell.AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new ParticleSpell.WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new ParticleDrip.WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new ParticleDrip.LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new ParticleHeart.AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new ParticleSuspendedTown.HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new ParticleSuspendedTown.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new ParticleNote.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new ParticlePortal.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new ParticleEnchantmentTable.EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new ParticleFlame.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new ParticleLava.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new ParticleFootStep.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new ParticleCloud.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new ParticleRedstone.Factory());
        this.registerParticle(EnumParticleTypes.FALLING_DUST.getParticleID(), new ParticleFallingDust.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new ParticleBreaking.SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new ParticleSnowShovel.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new ParticleBreaking.SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new ParticleHeart.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new ParticleBreaking.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new ParticleDigging.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new ParticleBlockDust.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new ParticleExplosionHuge.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new ParticleExplosionLarge.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new ParticleFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new ParticleMobAppearance.Factory());
        this.registerParticle(EnumParticleTypes.DRAGON_BREATH.getParticleID(), new ParticleDragonBreath.Factory());
        this.registerParticle(EnumParticleTypes.END_ROD.getParticleID(), new ParticleEndRod.Factory());
        this.registerParticle(EnumParticleTypes.DAMAGE_INDICATOR.getParticleID(), new ParticleCrit.DamageIndicatorFactory());
        this.registerParticle(EnumParticleTypes.SWEEP_ATTACK.getParticleID(), new ParticleSweepAttack.Factory());
        this.registerParticle(EnumParticleTypes.TOTEM.getParticleID(), new ParticleTotem.Factory());
    }

    public void registerParticle(int id2, IParticleFactory particleFactory) {
        this.particleTypes.put(id2, particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
        this.particleEmitters.add(new ParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    public void func_191271_a(Entity p_191271_1_, EnumParticleTypes p_191271_2_, int p_191271_3_) {
        this.particleEmitters.add(new ParticleEmitter(this.worldObj, p_191271_1_, p_191271_2_, p_191271_3_));
    }

    @Nullable
    public Particle spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        Particle particle;
        IParticleFactory iparticlefactory = this.particleTypes.get(particleId);
        if (iparticlefactory != null && (particle = iparticlefactory.createParticle(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters)) != null) {
            this.addEffect(particle);
            return particle;
        }
        return null;
    }

    public void addEffect(Particle effect) {
        if (effect != null && (!(effect instanceof ParticleFirework.Spark) || Config.isFireworkParticles())) {
            this.queueEntityFX.add(effect);
        }
    }

    public void updateEffects() {
        for (int i2 = 0; i2 < 4; ++i2) {
            this.updateEffectLayer(i2);
        }
        if (!this.particleEmitters.isEmpty()) {
            ArrayList<ParticleEmitter> list = Lists.newArrayList();
            for (ParticleEmitter particleemitter : this.particleEmitters) {
                particleemitter.onUpdate();
                if (particleemitter.isAlive()) continue;
                list.add(particleemitter);
            }
            this.particleEmitters.removeAll(list);
        }
        if (!this.queueEntityFX.isEmpty()) {
            Particle particle = this.queueEntityFX.poll();
            while (particle != null) {
                int k2;
                int j2 = particle.getFXLayer();
                int n2 = k2 = particle.isTransparent() ? 0 : 1;
                if (this.fxLayers[j2][k2].size() >= 16384) {
                    this.fxLayers[j2][k2].removeFirst();
                }
                if (!(particle instanceof Barrier) || !this.reuseBarrierParticle(particle, this.fxLayers[j2][k2])) {
                    this.fxLayers[j2][k2].add(particle);
                }
                particle = this.queueEntityFX.poll();
            }
        }
    }

    private void updateEffectLayer(int layer) {
        this.worldObj.theProfiler.startSection(String.valueOf(layer));
        for (int i2 = 0; i2 < 2; ++i2) {
            this.worldObj.theProfiler.startSection(String.valueOf(i2));
            this.tickParticleList(this.fxLayers[layer][i2]);
            this.worldObj.theProfiler.endSection();
        }
        this.worldObj.theProfiler.endSection();
    }

    private void tickParticleList(Queue<Particle> p_187240_1_) {
        if (!p_187240_1_.isEmpty()) {
            Iterator iterator = p_187240_1_.iterator();
            while (iterator.hasNext()) {
                Particle particle = (Particle)iterator.next();
                this.tickParticle(particle);
                if (particle.isAlive()) continue;
                iterator.remove();
            }
        }
    }

    private void tickParticle(final Particle particle) {
        try {
            particle.onUpdate();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
            final int i2 = particle.getFXLayer();
            crashreportcategory.setDetail("Particle", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    return particle.toString();
                }
            });
            crashreportcategory.setDetail("Particle Type", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    if (i2 == 0) {
                        return "MISC_TEXTURE";
                    }
                    if (i2 == 1) {
                        return "TERRAIN_TEXTURE";
                    }
                    return i2 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i2;
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    public void renderParticles(Entity entityIn, float partialTicks) {
        float f2 = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f22 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();
        Particle.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        Particle.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        Particle.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        Particle.cameraViewDir = entityIn.getLook(partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569f);
        for (int i2 = 0; i2 < 3; ++i2) {
            final int j2 = i2;
            for (int k2 = 0; k2 < 2; ++k2) {
                if (this.fxLayers[j2][k2].isEmpty()) continue;
                switch (k2) {
                    case 0: {
                        GlStateManager.depthMask(false);
                        break;
                    }
                    case 1: {
                        GlStateManager.depthMask(true);
                    }
                }
                switch (j2) {
                    default: {
                        this.renderer.bindTexture(PARTICLE_TEXTURES);
                        break;
                    }
                    case 1: {
                        this.renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    }
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                for (final Particle particle : this.fxLayers[j2][k2]) {
                    try {
                        particle.renderParticle(bufferbuilder, entityIn, partialTicks, f2, f4, f1, f22, f3);
                    }
                    catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                        crashreportcategory.setDetail("Particle", new ICrashReportDetail<String>(){

                            @Override
                            public String call() throws Exception {
                                return particle.toString();
                            }
                        });
                        crashreportcategory.setDetail("Particle Type", new ICrashReportDetail<String>(){

                            @Override
                            public String call() throws Exception {
                                if (j2 == 0) {
                                    return "MISC_TEXTURE";
                                }
                                if (j2 == 1) {
                                    return "TERRAIN_TEXTURE";
                                }
                                return j2 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + j2;
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                }
                tessellator.draw();
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
    }

    public void renderLitParticles(Entity entityIn, float partialTick) {
        float f2 = (float)Math.PI / 180;
        float f1 = MathHelper.cos(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f22 = MathHelper.sin(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f3 = -f22 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f5 = MathHelper.cos(entityIn.rotationPitch * ((float)Math.PI / 180));
        for (int i2 = 0; i2 < 2; ++i2) {
            ArrayDeque<Particle> queue = this.fxLayers[3][i2];
            if (queue.isEmpty()) continue;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            for (Particle particle : queue) {
                particle.renderParticle(bufferbuilder, entityIn, partialTick, f1, f5, f22, f3, f4);
            }
        }
    }

    public void clearEffects(@Nullable World worldIn) {
        this.worldObj = worldIn;
        for (int i2 = 0; i2 < 4; ++i2) {
            for (int j2 = 0; j2 < 2; ++j2) {
                this.fxLayers[i2][j2].clear();
            }
        }
        this.particleEmitters.clear();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        boolean flag;
        if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
            Block block = state.getBlock();
            if (Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, state, this.worldObj, pos)) return;
            if (Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this)) return;
            boolean bl2 = true;
            flag = bl2;
        } else {
            if (state.getMaterial() == Material.AIR) return;
            boolean bl3 = true;
            flag = bl3;
        }
        if (!flag) return;
        state = state.getActualState(this.worldObj, pos);
        int l2 = 4;
        for (int i2 = 0; i2 < 4; ++i2) {
            for (int j2 = 0; j2 < 4; ++j2) {
                for (int k2 = 0; k2 < 4; ++k2) {
                    double d0 = ((double)i2 + 0.5) / 4.0;
                    double d1 = ((double)j2 + 0.5) / 4.0;
                    double d2 = ((double)k2 + 0.5) / 4.0;
                    this.addEffect(new ParticleDigging(this.worldObj, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, d0 - 0.5, d1 - 0.5, d2 - 0.5, state).setBlockPos(pos));
                }
            }
        }
    }

    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = this.worldObj.getBlockState(pos);
        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            int i2 = pos.getX();
            int j2 = pos.getY();
            int k2 = pos.getZ();
            float f2 = 0.1f;
            AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(this.worldObj, pos);
            double d0 = (double)i2 + this.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2f) + (double)0.1f + axisalignedbb.minX;
            double d1 = (double)j2 + this.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2f) + (double)0.1f + axisalignedbb.minY;
            double d2 = (double)k2 + this.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2f) + (double)0.1f + axisalignedbb.minZ;
            if (side == EnumFacing.DOWN) {
                d1 = (double)j2 + axisalignedbb.minY - (double)0.1f;
            }
            if (side == EnumFacing.UP) {
                d1 = (double)j2 + axisalignedbb.maxY + (double)0.1f;
            }
            if (side == EnumFacing.NORTH) {
                d2 = (double)k2 + axisalignedbb.minZ - (double)0.1f;
            }
            if (side == EnumFacing.SOUTH) {
                d2 = (double)k2 + axisalignedbb.maxZ + (double)0.1f;
            }
            if (side == EnumFacing.WEST) {
                d0 = (double)i2 + axisalignedbb.minX - (double)0.1f;
            }
            if (side == EnumFacing.EAST) {
                d0 = (double)i2 + axisalignedbb.maxX + (double)0.1f;
            }
            this.addEffect(new ParticleDigging(this.worldObj, d0, d1, d2, 0.0, 0.0, 0.0, iblockstate).setBlockPos(pos).multiplyVelocity(0.2f).multipleParticleScaleBy(0.6f));
        }
    }

    public String getStatistics() {
        int i2 = 0;
        for (int j2 = 0; j2 < 4; ++j2) {
            for (int k2 = 0; k2 < 2; ++k2) {
                i2 += this.fxLayers[j2][k2].size();
            }
        }
        return "" + i2;
    }

    private boolean reuseBarrierParticle(Particle p_reuseBarrierParticle_1_, ArrayDeque<Particle> p_reuseBarrierParticle_2_) {
        for (Particle particle : p_reuseBarrierParticle_2_) {
            if (!(particle instanceof Barrier) || p_reuseBarrierParticle_1_.prevPosX != particle.prevPosX || p_reuseBarrierParticle_1_.prevPosY != particle.prevPosY || p_reuseBarrierParticle_1_.prevPosZ != particle.prevPosZ) continue;
            particle.particleAge = 0;
            return true;
        }
        return false;
    }

    public void addBlockHitEffects(BlockPos p_addBlockHitEffects_1_, RayTraceResult p_addBlockHitEffects_2_) {
        IBlockState iblockstate = this.worldObj.getBlockState(p_addBlockHitEffects_1_);
        if (iblockstate != null) {
            boolean flag = Reflector.callBoolean(iblockstate.getBlock(), Reflector.ForgeBlock_addHitEffects, iblockstate, this.worldObj, p_addBlockHitEffects_2_, this);
            if (iblockstate != null && !flag) {
                this.addBlockHitEffects(p_addBlockHitEffects_1_, p_addBlockHitEffects_2_.sideHit);
            }
        }
    }
}


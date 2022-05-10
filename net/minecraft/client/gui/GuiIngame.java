package net.minecraft.client.gui;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Hook;
import ShwepSS.B17.cg.ClickGuiScreen;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.gui.HackArray;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.ModuleManager;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.border.WorldBorder;
import optifine.Config;
import optifine.CustomColors;
import optifine.CustomItems;
import optifine.Reflector;
import optifine.ReflectorForge;
import optifine.TextureAnimations;

public class GuiIngame
extends Gui {
    private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;
    private String recordPlaying = "";
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    public float prevVignetteBrightness = 1.0f;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack = ItemStack.field_190927_a;
    private final GuiOverlayDebug overlayDebug;
    private final GuiSubtitleOverlay overlaySubtitle;
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    private final GuiBossOverlay overlayBoss;
    private int titlesTimer;
    private String displayedTitle = "";
    private String displayedSubTitle = "";
    private int titleFadeIn;
    private int titleDisplayTime;
    private int titleFadeOut;
    private int playerHealth;
    private int lastPlayerHealth;
    private long lastSystemTime;
    private long healthUpdateCounter;
    private final Map<ChatType, List<IChatListener>> field_191743_I = Maps.newHashMap();
    public static long prevMs = -1L;
    public static int ivar1 = 0;
    public static int ivar2 = 1;
    public static int ivar3 = 0;
    public static List<Integer> iarr = new ArrayList<Integer>();
    public static float fvar1 = 0.0f;
    public static float fvar2 = 0.0f;
    public static float fvar3 = 0.0f;
    public static List<Float> farr = new ArrayList<Float>();
    public static double dvar1 = 0.0;
    public static double dvar2 = 0.0;
    public static double dvar3 = 0.0;
    public static List<Double> darr = new ArrayList<Double>();
    public static long lvar1 = 0L;
    public static long lvar2 = 0L;
    public static long lvar3 = 0L;
    public static List<Long> larr = new ArrayList<Long>();
    public static String svar1 = "";
    public static String svar2 = "";
    public static String svar3 = "";
    public static List<String> sarr = new ArrayList<String>();
    public static Object obj1 = null;
    public static Object obj2 = null;
    public static Object obj3 = null;
    public static List<Object> oarr = new ArrayList<Object>();

    public GuiIngame(Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.overlayBoss = new GuiBossOverlay(mcIn);
        this.overlaySubtitle = new GuiSubtitleOverlay(mcIn);
        for (ChatType chattype : ChatType.values()) {
            this.field_191743_I.put(chattype, Lists.newArrayList());
        }
        NarratorChatListener ichatlistener = NarratorChatListener.field_193643_a;
        this.field_191743_I.get((Object)ChatType.CHAT).add(new NormalChatListener(mcIn));
        this.field_191743_I.get((Object)ChatType.CHAT).add(ichatlistener);
        this.field_191743_I.get((Object)ChatType.SYSTEM).add(new NormalChatListener(mcIn));
        this.field_191743_I.get((Object)ChatType.SYSTEM).add(ichatlistener);
        this.field_191743_I.get((Object)ChatType.GAME_INFO).add(new OverlayChatListener(mcIn));
        this.setDefaultTitlesTimes();
    }

    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
        ModuleManager.modules.sort(Comparator.comparingInt(m1 -> FontUtil.elegant_16.getStringWidth(((Module)m1).name)).reversed());
        ExtremeHack.instance.cg = new ClickGuiScreen();
    }

    public void renderGameOverlay(float partialTicks) {
        ScoreObjective scoreobjective1;
        int i1;
        float f2;
        Hook.onRenderIngame();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        HackArray.renderArrayListGui();
        int i2 = scaledresolution.getScaledWidth();
        int j2 = scaledresolution.getScaledHeight();
        FontRenderer fontrenderer = this.getFontRenderer();
        GlStateManager.enableBlend();
        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.player.getBrightness(), scaledresolution);
        } else {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        ItemStack itemstack = this.mc.player.inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
            this.renderPumpkinOverlay(scaledresolution);
        }
        if (!this.mc.player.isPotionActive(MobEffects.NAUSEA) && (f2 = this.mc.player.prevTimeInPortal + (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks) > 0.0f) {
            this.renderPortal(f2, scaledresolution);
        }
        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
        } else {
            this.renderHotbar(scaledresolution, partialTicks);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(ICONS);
        GlStateManager.enableBlend();
        this.renderAttackIndicator(partialTicks, scaledresolution);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.mc.mcProfiler.startSection("bossHealth");
        this.overlayBoss.renderBossHealth();
        this.mc.mcProfiler.endSection();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(ICONS);
        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(scaledresolution);
        }
        this.renderMountHealth(scaledresolution);
        GlStateManager.disableBlend();
        if (this.mc.player.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int j1 = this.mc.player.getSleepTimer();
            float f1 = (float)j1 / 100.0f;
            if (f1 > 1.0f) {
                f1 = 1.0f - (float)(j1 - 100) / 10.0f;
            }
            int k2 = (int)(220.0f * f1) << 24 | 0x101020;
            GuiIngame.drawRect(0.0, 0.0, i2, j2, k2);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int k1 = i2 / 2 - 91;
        if (this.mc.player.isRidingHorse()) {
            this.renderHorseJumpBar(scaledresolution, k1);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(scaledresolution, k1);
        }
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(scaledresolution);
        } else if (this.mc.player.isSpectator()) {
            this.spectatorGui.renderSelectedItem(scaledresolution);
        }
        if (this.mc.isDemo()) {
            this.renderDemo(scaledresolution);
        }
        this.renderPotionEffects(scaledresolution);
        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledresolution);
        }
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f22 = (float)this.recordPlayingUpFor - partialTicks;
            int l1 = (int)(f22 * 255.0f / 20.0f);
            if (l1 > 255) {
                l1 = 255;
            }
            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 - 68, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int l2 = 0xFFFFFF;
                if (this.recordIsPlaying) {
                    l2 = MathHelper.hsvToRGB(f22 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                }
                fontrenderer.drawString(this.recordPlaying, -fontrenderer.getStringWidth(this.recordPlaying) / 2, -4, l2 + (l1 << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        this.overlaySubtitle.renderSubtitles(scaledresolution);
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f3 = (float)this.titlesTimer - partialTicks;
            int i22 = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f4 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
                i22 = (int)(f4 * 255.0f / (float)this.titleFadeIn);
            }
            if (this.titlesTimer <= this.titleFadeOut) {
                i22 = (int)(f3 * 255.0f / (float)this.titleFadeOut);
            }
            if ((i22 = MathHelper.clamp(i22, 0, 255)) > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(i2 / 2, j2 / 2, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0f, 4.0f, 4.0f);
                int j22 = i22 << 24 & 0xFF000000;
                fontrenderer.drawString(this.displayedTitle, -fontrenderer.getStringWidth(this.displayedTitle) / 2, -10.0f, 0xFFFFFF | j22, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                fontrenderer.drawString(this.displayedSubTitle, -fontrenderer.getStringWidth(this.displayedSubTitle) / 2, 5.0f, 0xFFFFFF | j22, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        Scoreboard scoreboard = this.mc.world.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.player.getName());
        if (scoreplayerteam != null && (i1 = scoreplayerteam.getChatFormat().getColorIndex()) >= 0) {
            scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
        }
        ScoreObjective scoreObjective = scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreobjective1 != null) {
            this.renderScoreboard(scoreobjective1, scaledresolution);
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, j2 - 48, 0.0f);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);
        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.player.connection.getPlayerInfoMap().size() > 1 || scoreobjective1 != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(i2, scoreboard, scoreobjective1);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    private void renderAttackIndicator(float p_184045_1_, ScaledResolution p_184045_2_) {
        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.thirdPersonView == 0) {
            if (this.mc.playerController.isSpectator() && this.mc.pointedEntity == null) {
                RayTraceResult raytraceresult = this.mc.objectMouseOver;
                if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                    return;
                }
                BlockPos blockpos = raytraceresult.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
                if (!ReflectorForge.blockHasTileEntity(iblockstate) || !(this.mc.world.getTileEntity(blockpos) instanceof IInventory)) {
                    return;
                }
            }
            int l2 = p_184045_2_.getScaledWidth();
            int i1 = p_184045_2_.getScaledHeight();
            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(l2 / 2, i1 / 2, zLevel);
                Entity entity = this.mc.getRenderViewEntity();
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_184045_1_, -1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_184045_1_, 0.0f, 1.0f, 0.0f);
                GlStateManager.scale(-1.0f, -1.0f, -1.0f);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();
                this.drawTexturedModalRect(l2 / 2 - 7, i1 / 2 - 7, 0, 0, 16, 16);
                if (this.mc.gameSettings.attackIndicator == 1) {
                    float f2 = this.mc.player.getCooledAttackStrength(0.0f);
                    boolean flag = false;
                    if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof EntityLivingBase && f2 >= 1.0f) {
                        flag = this.mc.player.getCooldownPeriod() > 5.0f;
                        flag &= ((EntityLivingBase)this.mc.pointedEntity).isEntityAlive();
                    }
                    int i2 = i1 / 2 - 7 + 16;
                    int j2 = l2 / 2 - 8;
                    if (flag) {
                        this.drawTexturedModalRect(j2, i2, 68, 94, 16, 16);
                    } else if (f2 < 1.0f) {
                        int k2 = (int)(f2 * 17.0f);
                        this.drawTexturedModalRect(j2, i2, 36, 94, 16, 4);
                        this.drawTexturedModalRect(j2, i2, 52, 94, k2, 4);
                    }
                }
            }
        }
    }

    protected void renderPotionEffects(ScaledResolution resolution) {
        Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
        if (!collection.isEmpty()) {
            this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            GlStateManager.enableBlend();
            int i2 = 0;
            int j2 = 0;
            Iterator<PotionEffect> iterator = Ordering.natural().reverse().sortedCopy(collection).iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    return;
                }
                PotionEffect potioneffect = iterator.next();
                Potion potion = potioneffect.getPotion();
                boolean flag = potion.hasStatusIcon();
                if (Reflector.ForgePotion_shouldRenderHUD.exists()) {
                    if (!Reflector.callBoolean(potion, Reflector.ForgePotion_shouldRenderHUD, potioneffect)) continue;
                    this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
                    flag = true;
                }
                if (!flag || !potioneffect.doesShowParticles()) continue;
                int k2 = resolution.getScaledWidth();
                int l2 = 1;
                if (this.mc.isDemo()) {
                    l2 += 15;
                }
                int i1 = potion.getStatusIconIndex();
                if (potion.isBeneficial()) {
                    k2 -= 25 * ++i2;
                } else {
                    k2 -= 25 * ++j2;
                    l2 += 26;
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                float f2 = 1.0f;
                if (potioneffect.getIsAmbient()) {
                    this.drawTexturedModalRect(k2, l2, 165, 166, 24, 24);
                } else {
                    this.drawTexturedModalRect(k2, l2, 141, 166, 24, 24);
                    if (potioneffect.getDuration() <= 200) {
                        int j1 = 10 - potioneffect.getDuration() / 20;
                        f2 = MathHelper.clamp((float)potioneffect.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)potioneffect.getDuration() * (float)Math.PI / 5.0f) * MathHelper.clamp((float)j1 / 10.0f * 0.25f, 0.0f, 0.25f);
                    }
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, f2);
                if (Reflector.ForgePotion_renderHUDEffect.exists()) {
                    if (potion.hasStatusIcon()) {
                        this.drawTexturedModalRect(k2 + 3, l2 + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    }
                    Reflector.call(potion, Reflector.ForgePotion_renderHUDEffect, k2, l2, potioneffect, this.mc, Float.valueOf(f2));
                    continue;
                }
                this.drawTexturedModalRect(k2 + 3, l2 + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
            }
        }
    }

    protected void renderHotbar(ScaledResolution sr2, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            float f1;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            ItemStack itemstack = entityplayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
            int i2 = sr2.getScaledWidth() / 2;
            float f2 = zLevel;
            int j2 = 182;
            int k2 = 91;
            zLevel = -90.0f;
            this.drawTexturedModalRect(i2 - 91, sr2.getScaledHeight() - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(i2 - 91 - 1 + entityplayer.inventory.currentItem * 20, sr2.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            if (!itemstack.func_190926_b()) {
                if (enumhandside == EnumHandSide.LEFT) {
                    this.drawTexturedModalRect(i2 - 91 - 29, sr2.getScaledHeight() - 23, 24, 22, 29, 24);
                } else {
                    this.drawTexturedModalRect(i2 + 91, sr2.getScaledHeight() - 23, 53, 22, 29, 24);
                }
            }
            zLevel = f2;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();
            CustomItems.setRenderOffHand(false);
            for (int l2 = 0; l2 < 9; ++l2) {
                int i1 = i2 - 90 + l2 * 20 + 2;
                int j1 = sr2.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l2));
            }
            if (!itemstack.func_190926_b()) {
                CustomItems.setRenderOffHand(true);
                int l1 = sr2.getScaledHeight() - 16 - 3;
                if (enumhandside == EnumHandSide.LEFT) {
                    this.renderHotbarItem(i2 - 91 - 26, l1, partialTicks, entityplayer, itemstack);
                } else {
                    this.renderHotbarItem(i2 + 91 + 10, l1, partialTicks, entityplayer, itemstack);
                }
                CustomItems.setRenderOffHand(false);
            }
            if (this.mc.gameSettings.attackIndicator == 2 && (f1 = this.mc.player.getCooledAttackStrength(0.0f)) < 1.0f) {
                int i22 = sr2.getScaledHeight() - 20;
                int j22 = i2 + 91 + 6;
                if (enumhandside == EnumHandSide.RIGHT) {
                    j22 = i2 - 91 - 22;
                }
                this.mc.getTextureManager().bindTexture(Gui.ICONS);
                int k1 = (int)(f1 * 19.0f);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                this.drawTexturedModalRect(j22, i22, 0, 94, 18, 18);
                this.drawTexturedModalRect(j22, i22 + 18 - k1, 18, 112 - k1, 18, k1);
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x2) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        float f2 = this.mc.player.getHorseJumpPower();
        int i2 = 182;
        int j2 = (int)(f2 * 183.0f);
        int k2 = scaledRes.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(x2, k2, 0, 84, 182, 5);
        if (j2 > 0) {
            this.drawTexturedModalRect(x2, k2, 0, 89, j2, 5);
        }
        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x2) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int i2 = this.mc.player.xpBarCap();
        if (i2 > 0) {
            int j2 = 182;
            int k2 = (int)(this.mc.player.experience * 183.0f);
            int l2 = scaledRes.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(x2, l2, 0, 64, 182, 5);
            if (k2 > 0) {
                this.drawTexturedModalRect(x2, l2, 0, 69, k2, 5);
            }
        }
        this.mc.mcProfiler.endSection();
        if (this.mc.player.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int j1 = 8453920;
            if (Config.isCustomColors()) {
                j1 = CustomColors.getExpBarTextColor(j1);
            }
            String s2 = "" + this.mc.player.experienceLevel;
            int k1 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int i1 = scaledRes.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(s2, k1 + 1, i1, 0);
            this.getFontRenderer().drawString(s2, k1 - 1, i1, 0);
            this.getFontRenderer().drawString(s2, k1, i1 + 1, 0);
            this.getFontRenderer().drawString(s2, k1, i1 - 1, 0);
            this.getFontRenderer().drawString(s2, k1, i1, j1);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.func_190926_b()) {
            int k2;
            String s2 = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                s2 = (Object)((Object)TextFormatting.ITALIC) + s2;
            }
            int i2 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s2)) / 2;
            int j2 = scaledRes.getScaledHeight() - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
                j2 += 14;
            }
            if ((k2 = (int)((float)this.remainingHighlightTicks * 256.0f / 10.0f)) > 255) {
                k2 = 255;
            }
            if (k2 > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.getFontRenderer().drawStringWithShadow(s2, i2, j2, 0xFFFFFF + (k2 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("demo");
        String s2 = this.mc.world.getTotalWorldTime() >= 120500L ? I18n.format("demo.demoExpired", new Object[0]) : I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.world.getTotalWorldTime())));
        int i2 = this.getFontRenderer().getStringWidth(s2);
        this.getFontRenderer().drawStringWithShadow(s2, scaledRes.getScaledWidth() - i2 - 10, 5.0f, 0xFFFFFF);
        this.mc.mcProfiler.endSection();
    }

    private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        ArrayList<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>(){

            @Override
            public boolean apply(@Nullable Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        int i2 = this.getFontRenderer().getStringWidth(objective.getDisplayName());
        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s2 = String.valueOf(ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName())) + ": " + (Object)((Object)TextFormatting.RED) + score.getScorePoints();
            i2 = Math.max(i2, this.getFontRenderer().getStringWidth(s2));
        }
        int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i2 - 3;
        int j2 = 0;
        for (Score score1 : collection) {
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = "" + (Object)((Object)TextFormatting.RED) + score1.getScorePoints();
            int k2 = j1 - ++j2 * this.getFontRenderer().FONT_HEIGHT;
            int l2 = scaledRes.getScaledWidth() - 3 + 2;
            GuiIngame.drawRect(l1 - 2, k2, l2, k2 + this.getFontRenderer().FONT_HEIGHT, 0x50000000);
            this.getFontRenderer().drawString(s1, l1, k2, 0x20FFFFFF);
            this.getFontRenderer().drawString(s2, l2 - this.getFontRenderer().getStringWidth(s2), k2, 0x20FFFFFF);
            if (j2 != collection.size()) continue;
            String s3 = objective.getDisplayName();
            GuiIngame.drawRect(l1 - 2, k2 - this.getFontRenderer().FONT_HEIGHT - 1, l2, k2 - 1, 0x60000000);
            GuiIngame.drawRect(l1 - 2, k2 - 1, l2, k2, 0x50000000);
            this.getFontRenderer().drawString(s3, l1 + i2 / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k2 - this.getFontRenderer().FONT_HEIGHT, 0x20FFFFFF);
        }
    }

    private void renderPlayerStats(ScaledResolution scaledRes) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            boolean flag;
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int i2 = MathHelper.ceil(entityplayer.getHealth());
            boolean bl2 = flag = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
            if (i2 < this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (i2 > this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }
            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = i2;
                this.lastPlayerHealth = i2;
                this.lastSystemTime = Minecraft.getSystemTime();
            }
            this.playerHealth = i2;
            int j2 = this.lastPlayerHealth;
            this.rand.setSeed(this.updateCounter * 312871);
            FoodStats foodstats = entityplayer.getFoodStats();
            int k2 = foodstats.getFoodLevel();
            IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            int l2 = scaledRes.getScaledWidth() / 2 - 91;
            int i1 = scaledRes.getScaledWidth() / 2 + 91;
            int j1 = scaledRes.getScaledHeight() - 39;
            float f2 = (float)iattributeinstance.getAttributeValue();
            int k1 = MathHelper.ceil(entityplayer.getAbsorptionAmount());
            int l1 = MathHelper.ceil((f2 + (float)k1) / 2.0f / 10.0f);
            int i22 = Math.max(10 - (l1 - 2), 3);
            int j22 = j1 - (l1 - 1) * i22 - 10;
            int k22 = j1 - 10;
            int l22 = k1;
            int i3 = entityplayer.getTotalArmorValue();
            int j3 = -1;
            if (entityplayer.isPotionActive(MobEffects.REGENERATION)) {
                j3 = this.updateCounter % MathHelper.ceil(f2 + 5.0f);
            }
            this.mc.mcProfiler.startSection("armor");
            for (int k3 = 0; k3 < 10; ++k3) {
                if (i3 <= 0) continue;
                int l3 = l2 + k3 * 8;
                if (k3 * 2 + 1 < i3) {
                    this.drawTexturedModalRect(l3, j22, 34, 9, 9, 9);
                }
                if (k3 * 2 + 1 == i3) {
                    this.drawTexturedModalRect(l3, j22, 25, 9, 9, 9);
                }
                if (k3 * 2 + 1 <= i3) continue;
                this.drawTexturedModalRect(l3, j22, 16, 9, 9, 9);
            }
            this.mc.mcProfiler.endStartSection("health");
            for (int j5 = MathHelper.ceil((f2 + (float)k1) / 2.0f) - 1; j5 >= 0; --j5) {
                int k5 = 16;
                if (entityplayer.isPotionActive(MobEffects.POISON)) {
                    k5 += 36;
                } else if (entityplayer.isPotionActive(MobEffects.WITHER)) {
                    k5 += 72;
                }
                int i4 = 0;
                if (flag) {
                    i4 = 1;
                }
                int j4 = MathHelper.ceil((float)(j5 + 1) / 10.0f) - 1;
                int k4 = l2 + j5 % 10 * 8;
                int l4 = j1 - j4 * i22;
                if (i2 <= 4) {
                    l4 += this.rand.nextInt(2);
                }
                if (l22 <= 0 && j5 == j3) {
                    l4 -= 2;
                }
                int i5 = 0;
                if (entityplayer.world.getWorldInfo().isHardcoreModeEnabled()) {
                    i5 = 5;
                }
                this.drawTexturedModalRect(k4, l4, 16 + i4 * 9, 9 * i5, 9, 9);
                if (flag) {
                    if (j5 * 2 + 1 < j2) {
                        this.drawTexturedModalRect(k4, l4, k5 + 54, 9 * i5, 9, 9);
                    }
                    if (j5 * 2 + 1 == j2) {
                        this.drawTexturedModalRect(k4, l4, k5 + 63, 9 * i5, 9, 9);
                    }
                }
                if (l22 > 0) {
                    if (l22 == k1 && k1 % 2 == 1) {
                        this.drawTexturedModalRect(k4, l4, k5 + 153, 9 * i5, 9, 9);
                        --l22;
                        continue;
                    }
                    this.drawTexturedModalRect(k4, l4, k5 + 144, 9 * i5, 9, 9);
                    l22 -= 2;
                    continue;
                }
                if (j5 * 2 + 1 < i2) {
                    this.drawTexturedModalRect(k4, l4, k5 + 36, 9 * i5, 9, 9);
                }
                if (j5 * 2 + 1 != i2) continue;
                this.drawTexturedModalRect(k4, l4, k5 + 45, 9 * i5, 9, 9);
            }
            Entity entity = entityplayer.getRidingEntity();
            if (entity == null || !(entity instanceof EntityLivingBase)) {
                this.mc.mcProfiler.endStartSection("food");
                for (int l5 = 0; l5 < 10; ++l5) {
                    int j6 = j1;
                    int l6 = 16;
                    int j7 = 0;
                    if (entityplayer.isPotionActive(MobEffects.HUNGER)) {
                        l6 += 36;
                        j7 = 13;
                    }
                    if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0f && this.updateCounter % (k2 * 3 + 1) == 0) {
                        j6 = j1 + (this.rand.nextInt(3) - 1);
                    }
                    int l7 = i1 - l5 * 8 - 9;
                    this.drawTexturedModalRect(l7, j6, 16 + j7 * 9, 27, 9, 9);
                    if (l5 * 2 + 1 < k2) {
                        this.drawTexturedModalRect(l7, j6, l6 + 36, 27, 9, 9);
                    }
                    if (l5 * 2 + 1 != k2) continue;
                    this.drawTexturedModalRect(l7, j6, l6 + 45, 27, 9, 9);
                }
            }
            this.mc.mcProfiler.endStartSection("air");
            if (entityplayer.isInsideOfMaterial(Material.WATER)) {
                int i6 = this.mc.player.getAir();
                int k6 = MathHelper.ceil((double)(i6 - 2) * 10.0 / 300.0);
                int i7 = MathHelper.ceil((double)i6 * 10.0 / 300.0) - k6;
                for (int k7 = 0; k7 < k6 + i7; ++k7) {
                    if (k7 < k6) {
                        this.drawTexturedModalRect(i1 - k7 * 8 - 9, k22, 16, 18, 9, 9);
                        continue;
                    }
                    this.drawTexturedModalRect(i1 - k7 * 8 - 9, k22, 25, 18, 9, 9);
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }

    private void renderMountHealth(ScaledResolution p_184047_1_) {
        EntityPlayer entityplayer;
        Entity entity;
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer && (entity = (entityplayer = (EntityPlayer)this.mc.getRenderViewEntity()).getRidingEntity()) instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            int i2 = (int)Math.ceil(entitylivingbase.getHealth());
            float f2 = entitylivingbase.getMaxHealth();
            int j2 = (int)(f2 + 0.5f) / 2;
            if (j2 > 30) {
                j2 = 30;
            }
            int k2 = p_184047_1_.getScaledHeight() - 39;
            int l2 = p_184047_1_.getScaledWidth() / 2 + 91;
            int i1 = k2;
            int j1 = 0;
            boolean flag = false;
            while (j2 > 0) {
                int k1 = Math.min(j2, 10);
                j2 -= k1;
                for (int l1 = 0; l1 < k1; ++l1) {
                    int i22 = 52;
                    int j22 = 0;
                    int k22 = l2 - l1 * 8 - 9;
                    this.drawTexturedModalRect(k22, i1, 52 + j22 * 9, 9, 9, 9);
                    if (l1 * 2 + 1 + j1 < i2) {
                        this.drawTexturedModalRect(k22, i1, 88, 9, 9, 9);
                    }
                    if (l1 * 2 + 1 + j1 != i2) continue;
                    this.drawTexturedModalRect(k22, i1, 97, 9, 9, 9);
                }
                i1 -= 10;
                j1 += 20;
            }
        }
    }

    private void renderPumpkinOverlay(ScaledResolution scaledRes) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        bufferbuilder.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        bufferbuilder.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        bufferbuilder.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        } else {
            lightLevel = 1.0f - lightLevel;
            lightLevel = MathHelper.clamp(lightLevel, 0.0f, 1.0f);
            WorldBorder worldborder = this.mc.world.getWorldBorder();
            float f2 = (float)worldborder.getClosestDistance(this.mc.player);
            double d0 = Math.min(worldborder.getResizeSpeed() * (double)worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
            double d1 = Math.max((double)worldborder.getWarningDistance(), d0);
            f2 = (double)f2 < d1 ? 1.0f - (float)((double)f2 / d1) : 0.0f;
            this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(lightLevel - this.prevVignetteBrightness) * 0.01);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (f2 > 0.0f) {
                GlStateManager.color(0.0f, f2, f2, 1.0f);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0f);
            }
            this.mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
            bufferbuilder.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
            bufferbuilder.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
            bufferbuilder.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
    }

    private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
        if (timeInPortal < 1.0f) {
            timeInPortal *= timeInPortal;
            timeInPortal *= timeInPortal;
            timeInPortal = timeInPortal * 0.8f + 0.2f;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, timeInPortal);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PORTAL.getDefaultState());
        float f2 = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f22 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(f2, f3).endVertex();
        bufferbuilder.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(f22, f3).endVertex();
        bufferbuilder.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(f22, f1).endVertex();
        bufferbuilder.pos(0.0, 0.0, -90.0).tex(f2, f1).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {
        if (!stack.func_190926_b()) {
            float f2 = (float)stack.func_190921_D() - p_184044_3_;
            if (f2 > 0.0f) {
                GlStateManager.pushMatrix();
                float f1 = 1.0f + f2 / 5.0f;
                GlStateManager.translate(p_184044_1_ + 8, p_184044_2_ + 12, 0.0f);
                GlStateManager.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(p_184044_1_ + 8), -(p_184044_2_ + 12), 0.0f);
            }
            this.itemRenderer.renderItemAndEffectIntoGUI(player, stack, p_184044_1_, p_184044_2_);
            if (f2 > 0.0f) {
                GlStateManager.popMatrix();
            }
            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, stack, p_184044_1_, p_184044_2_);
        }
    }

    public void updateTick() {
        if (this.mc.world == null) {
            TextureAnimations.updateAnimations();
        }
        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }
        if (this.titlesTimer > 0) {
            --this.titlesTimer;
            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }
        ++this.updateCounter;
        if (this.mc.player != null) {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();
            if (itemstack.func_190926_b()) {
                this.remainingHighlightTicks = 0;
            } else if (!this.highlightingItemStack.func_190926_b() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }
            this.highlightingItemStack = itemstack;
        }
    }

    public void setRecordPlayingMessage(String recordName) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying) {
        this.recordPlaying = message;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (timeFadeIn >= 0) {
                this.titleFadeIn = timeFadeIn;
            }
            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }
            if (timeFadeOut >= 0) {
                this.titleFadeOut = timeFadeOut;
            }
            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(ITextComponent component, boolean isPlaying) {
        this.setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    public void func_191742_a(ChatType p_191742_1_, ITextComponent p_191742_2_) {
        for (IChatListener ichatlistener : this.field_191743_I.get((Object)p_191742_1_)) {
            ichatlistener.func_192576_a(p_191742_1_, p_191742_2_);
        }
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void resetPlayersOverlayFooterHeader() {
        this.overlayPlayerList.resetFooterHeader();
        this.overlayBoss.clearBossInfos();
        this.mc.func_193033_an().func_191788_b();
    }

    public GuiBossOverlay getBossOverlay() {
        return this.overlayBoss;
    }
}


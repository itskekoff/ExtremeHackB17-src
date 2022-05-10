package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

public class GuiPlayerTabOverlay
extends Gui {
    public static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private ITextComponent footer;
    private ITextComponent header;
    private long lastTimeOpened;
    private boolean isBeingRendered;

    public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }
        this.isBeingRendered = willBeRendered;
    }

    public void renderPlayerlist(int width, Scoreboard scoreboardIn, @Nullable ScoreObjective scoreObjectiveIn) {
        boolean flag;
        int l3;
        NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
        List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int i2 = 0;
        int j2 = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            int k2 = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(networkplayerinfo));
            i2 = Math.max(i2, k2);
            if (scoreObjectiveIn == null || scoreObjectiveIn.getRenderType() == IScoreCriteria.EnumRenderType.HEARTS) continue;
            k2 = this.mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getOrCreateScore(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            j2 = Math.max(j2, k2);
        }
        list = list.subList(0, Math.min(list.size(), 5000));
        int i4 = l3 = list.size();
        int j4 = 1;
        while (i4 > 20) {
            i4 = (l3 + ++j4 - 1) / j4;
        }
        boolean bl2 = flag = this.mc.isIntegratedServerRunning() || this.mc.getConnection().getNetworkManager().isEncrypted();
        int l2 = scoreObjectiveIn != null ? (scoreObjectiveIn.getRenderType() == IScoreCriteria.EnumRenderType.HEARTS ? 90 : j2) : 0;
        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i2 + l2 + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        if (this.header != null) {
            list1 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
            for (String s2 : list1) {
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s2));
            }
        }
        List<String> list2 = null;
        if (this.footer != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            for (String s1 : list2) {
                l1 = Math.max(l1, this.mc.fontRendererObj.getStringWidth(s1));
            }
        }
        if (list1 != null) {
            GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s2 : list1) {
                int i22 = this.mc.fontRendererObj.getStringWidth(s2);
                this.mc.fontRendererObj.drawStringWithShadow(s2, width / 2 - i22 / 2, k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
            ++k1;
        }
        GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);
        for (int k4 = 0; k4 < l3; ++k4) {
            int k5;
            int l5;
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j22 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            GuiPlayerTabOverlay.drawRect(j22, k2, j22 + i1, k2 + 8, 0x20FFFFFF);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (k4 >= list.size()) continue;
            NetworkPlayerInfo networkplayerinfo1 = list.get(k4);
            GameProfile gameprofile = networkplayerinfo1.getGameProfile();
            EntityPlayer entityplayer = this.mc.world.getPlayerEntityByUUID(gameprofile.getId());
            boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
            this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
            int l22 = 8 + (flag1 ? 8 : 0);
            int i3 = 8 * (flag1 ? -1 : 1);
            Gui.drawScaledCustomSizeModalRect(j22, k2, 8.0f, l22, 8, i3, 8.0, 8.0, 64.0f, 64.0f);
            if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                int j3 = 8 + (flag1 ? 8 : 0);
                int k3 = 8 * (flag1 ? -1 : 1);
                Gui.drawScaledCustomSizeModalRect(j22, k2, 40.0f, j3, 8, k3, 8.0, 8.0, 64.0f, 64.0f);
            }
            j22 += 9;
            String s4 = this.getPlayerName(networkplayerinfo1);
            if (networkplayerinfo1.getGameType() == GameType.SPECTATOR) {
                this.mc.fontRendererObj.drawStringWithShadow((Object)((Object)TextFormatting.ITALIC) + s4, j22, k2, -1862270977);
            } else {
                this.mc.fontRendererObj.drawStringWithShadow(s4, j22, k2, -1);
            }
            if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != GameType.SPECTATOR && (l5 = (k5 = j22 + i2 + 1) + l2) - k5 > 5) {
                this.drawScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
            }
            this.drawPing(i1, j22 - (flag ? 9 : 0) - 10, k2, networkplayerinfo1);
        }
        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s3 : list2) {
                int j5 = this.mc.fontRendererObj.getStringWidth(s3);
                this.mc.fontRendererObj.drawStringWithShadow(s3, width / 2 - j5 / 2, k1, -1);
                k1 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(ICONS);
        boolean i2 = false;
        int j2 = networkPlayerInfoIn.getResponseTime() < 0 ? 5 : (networkPlayerInfoIn.getResponseTime() < 150 ? 0 : (networkPlayerInfoIn.getResponseTime() < 300 ? 1 : (networkPlayerInfoIn.getResponseTime() < 600 ? 2 : (networkPlayerInfoIn.getResponseTime() < 1000 ? 3 : 4))));
        zLevel += 100.0f;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + j2 * 8, 10, 8);
        zLevel -= 100.0f;
    }

    private void drawScoreboardValues(ScoreObjective objective, int p_175247_2_, String name, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo info) {
        int i2 = objective.getScoreboard().getOrCreateScore(name, objective).getScorePoints();
        if (objective.getRenderType() == IScoreCriteria.EnumRenderType.HEARTS) {
            boolean flag;
            this.mc.getTextureManager().bindTexture(ICONS);
            if (this.lastTimeOpened == info.getRenderVisibilityId()) {
                if (i2 < info.getLastHealth()) {
                    info.setLastHealthTime(Minecraft.getSystemTime());
                    info.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 20);
                } else if (i2 > info.getLastHealth()) {
                    info.setLastHealthTime(Minecraft.getSystemTime());
                    info.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 10);
                }
            }
            if (Minecraft.getSystemTime() - info.getLastHealthTime() > 1000L || this.lastTimeOpened != info.getRenderVisibilityId()) {
                info.setLastHealth(i2);
                info.setDisplayHealth(i2);
                info.setLastHealthTime(Minecraft.getSystemTime());
            }
            info.setRenderVisibilityId(this.lastTimeOpened);
            info.setLastHealth(i2);
            int j2 = MathHelper.ceil((float)Math.max(i2, info.getDisplayHealth()) / 2.0f);
            int k2 = Math.max(MathHelper.ceil(i2 / 2), Math.max(MathHelper.ceil(info.getDisplayHealth() / 2), 10));
            boolean bl2 = flag = info.getHealthBlinkTime() > (long)this.guiIngame.getUpdateCounter() && (info.getHealthBlinkTime() - (long)this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
            if (j2 > 0) {
                float f2 = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)k2, 9.0f);
                if (f2 > 3.0f) {
                    for (int l2 = j2; l2 < k2; ++l2) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)l2 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                    }
                    for (int j1 = 0; j1 < j2; ++j1) {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j1 * 2 + 1 < info.getDisplayHealth()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, 70, 0, 9, 9);
                            }
                            if (j1 * 2 + 1 == info.getDisplayHealth()) {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, 79, 0, 9, 9);
                            }
                        }
                        if (j1 * 2 + 1 < i2) {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (j1 * 2 + 1 != i2) continue;
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f2, (float)p_175247_2_, j1 >= 10 ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f1 = MathHelper.clamp((float)i2 / 20.0f, 0.0f, 1.0f);
                    int i1 = (int)((1.0f - f1) * 255.0f) << 16 | (int)(f1 * 255.0f) << 8;
                    String s2 = "" + (float)i2 / 2.0f;
                    if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(String.valueOf(s2) + "hp") >= p_175247_4_) {
                        s2 = String.valueOf(s2) + "hp";
                    }
                    this.mc.fontRendererObj.drawStringWithShadow(s2, (p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRendererObj.getStringWidth(s2) / 2, p_175247_2_, i1);
                }
            }
        } else {
            String s1 = "" + (Object)((Object)TextFormatting.YELLOW) + i2;
            this.mc.fontRendererObj.drawStringWithShadow(s1, p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s1), p_175247_2_, 0xFFFFFF);
        }
    }

    public void setFooter(@Nullable ITextComponent footerIn) {
        this.footer = footerIn;
    }

    public void setHeader(@Nullable ITextComponent headerIn) {
        this.header = headerIn;
    }

    public void resetFooterHeader() {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator
    implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        @Override
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare((Comparable<?>)((Object)(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "")), (Comparable<?>)((Object)(scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : ""))).compare((Comparable<?>)((Object)p_compare_1_.getGameProfile().getName()), (Comparable<?>)((Object)p_compare_2_.getGameProfile().getName())).result();
        }
    }
}


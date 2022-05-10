package ShwepSS.B17.cg;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.cg.Translate;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.util.ParticleEngine;
import ShwepSS.B17.modules.Category;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class ClickGuiScreen
extends GuiScreen {
    private static ClickGuiScreen INSTANCE;
    private final List panels = Lists.newArrayList();
    public Translate translate;
    public static double scaling;
    private float curAlpha;
    private ParticleEngine en;
    public static String description;

    static {
        description = "\u0417\u0434\u0435\u0441\u044c \u0431\u0443\u0434\u0435\u0442 \u043e\u043f\u0438\u0441\u0430\u043d\u0438\u0435 \u043a\u0430\u0436\u0434\u043e\u0433\u043e \u043c\u043e\u0434\u0443\u043b\u044f \u043f\u0440\u0438 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0438";
    }

    public ClickGuiScreen() {
        Category[] category = Category.values();
        scaling = 0.0;
        for (int i2 = category.length - 1; i2 >= 0; --i2) {
            this.panels.add(new Panel(category[i2], 5 + 110 * i2, 10));
            this.translate = new Translate(0.0f, 0.0f);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ExtremeHack.instance.getSetmgr().getSettingByName("Particle1").getValue()) {
            this.en.render(mouseX, mouseY);
        }
        ClickGuiScreen.drawGradientRect(0, height / 2 + 40, width, height, HackConfigs.ThemeColorGui * 1000, HackConfigs.ThemeColor);
        int i2 = 0;
        if (ExtremeHack.mc.player != null && this.mc.world != null) {
            ScaledResolution sr2 = new ScaledResolution(Minecraft.getMinecraft());
            float alpha = 150.0f;
            int step = (int)(alpha / 100.0f);
            if (this.curAlpha < alpha - (float)step) {
                this.curAlpha += (float)step;
            } else if (this.curAlpha > alpha - (float)step && this.curAlpha != alpha) {
                this.curAlpha = (int)alpha;
            } else if (this.curAlpha != alpha) {
                this.curAlpha = (int)alpha;
            }
            Color c2 = new Color(0, 0, 4, (int)this.curAlpha);
            Color none = new Color(0, 0, 0, 0);
            ClickGuiScreen.drawGradientRect(0, 0, sr2.getScaledWidth(), sr2.getScaledHeight(), c2.getRGB(), none.getRGB());
            GL11.glPushMatrix();
            GL11.glScaled(1.3, 1.3, 1.3);
            GuiRenderUtils.drawBorderedRect2(sr2.getScaledWidth() / 5 - this.mc.fontRendererObj.getStringWidth(description), sr2.getScaledHeight() / 2 + 50, this.mc.fontRendererObj.getStringWidth(description) + sr2.getScaledWidth() / 5, sr2.getScaledHeight() / 2 + 70, partialTicks, HackConfigs.ThemeColor, HackConfigs.ThemeColorGui);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(description, sr2.getScaledWidth() / 5 - this.mc.fontRendererObj.getStringWidth(description) + 10, sr2.getScaledHeight() / 2 + 55, -1);
            GL11.glPopMatrix();
            int panelsSize = this.panels.size();
            while (i2 < panelsSize) {
                ((Panel)this.panels.get(i2)).onDraw(mouseX, mouseY);
                this.updateMouseWheel();
                ++i2;
            }
        }
    }

    public void updateMouseWheel() {
        int scrollWheel = Mouse.getDWheel();
        int panelsSize = this.panels.size();
        for (int i2 = 0; i2 < panelsSize; ++i2) {
            if (scrollWheel < 0) {
                ((Panel)this.panels.get(i2)).setY(((Panel)this.panels.get(i2)).getY() - 15);
                continue;
            }
            if (scrollWheel <= 0) continue;
            ((Panel)this.panels.get(i2)).setY(((Panel)this.panels.get(i2)).getY() + 15);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int panelsSize = this.panels.size();
        for (int i2 = 0; i2 < panelsSize; ++i2) {
            ((Panel)this.panels.get(i2)).onMouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int panelsSize = this.panels.size();
        for (int i2 = 0; i2 < panelsSize; ++i2) {
            ((Panel)this.panels.get(i2)).onMouseRelease(mouseX, mouseY, state);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.mc.entityRenderer.isShaderActive()) {
            this.mc.entityRenderer.theShaderGroup = null;
        }
    }

    @Override
    public void initGui() {
        this.en = new ParticleEngine();
        if (!this.mc.gameSettings.ofFastRender && !this.mc.entityRenderer.isShaderActive()) {
            this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/deconverge.json"));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int panelsSize = this.panels.size();
        for (int i2 = 0; i2 < panelsSize; ++i2) {
            ((Panel)this.panels.get(i2)).onKeyPress(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static ClickGuiScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGuiScreen();
        }
        return INSTANCE;
    }

    public void drawToolTip(String text, int mouseX, int mouseY) {
        FontUtil fu2 = new FontUtil();
        GuiRenderUtils.drawBorderedRect2(mouseX, mouseY, FontUtil.roboto_14.getStringWidth(text) - mouseX, mouseY + 10, 1.0f, HackConfigs.ThemeColorGui, HackConfigs.ThemeColor);
        FontUtil.roboto_14.drawStringWithOutline(text, mouseX, mouseY, HackConfigs.ThemeColor);
    }
}


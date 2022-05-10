package net.minecraft.client.gui;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.event.RenderTTEvent;
import ShwepSS.eventapi.EventManager;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiScreen
extends Gui
implements GuiYesNoCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    protected Minecraft mc;
    protected RenderItem itemRender;
    public static int width;
    public static int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRendererObj;
    protected GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;
    private boolean field_193977_u;
    public static String pathToPic;
    public static String path;

    static {
        path = "textures/backs/b" + RandomUtils.nextInt(1, 17) + ".jpg";
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (int i2 = 0; i2 < this.buttonList.size(); ++i2) {
            this.buttonList.get(i2).func_191745_a(this.mc, mouseX, mouseY, partialTicks);
        }
        for (int j2 = 0; j2 < this.labelList.size(); ++j2) {
            this.labelList.get(j2).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    protected <T extends GuiButton> T addButton(T p_189646_1_) {
        this.buttonList.add(p_189646_1_);
        return p_189646_1_;
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x2, int y2) {
        RenderTTEvent renderToolTipEvent = new RenderTTEvent(stack, x2, y2);
        EventManager.call(renderToolTipEvent);
        if (renderToolTipEvent.isCancelled()) {
            return;
        }
        this.drawHoveringText(this.func_191927_a(stack), x2, y2);
    }

    public List<String> func_191927_a(ItemStack p_191927_1_) {
        List<String> list = p_191927_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        for (int i2 = 0; i2 < list.size(); ++i2) {
            if (i2 == 0) {
                list.set(i2, (Object)((Object)p_191927_1_.getRarity().rarityColor) + list.get(i2));
                continue;
            }
            list.set(i2, (Object)((Object)TextFormatting.GRAY) + list.get(i2));
        }
        return list;
    }

    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    public void func_193975_a(boolean p_193975_1_) {
        this.field_193977_u = p_193975_1_;
    }

    public boolean func_193976_p() {
        return this.field_193977_u;
    }

    public void drawHoveringText(List<String> textLines, int x2, int y2) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i2 = 0;
            for (String s2 : textLines) {
                int j2 = this.fontRendererObj.getStringWidth(s2);
                if (j2 <= i2) continue;
                i2 = j2;
            }
            int l1 = x2 + 12;
            int i22 = y2 - 12;
            int k2 = 8;
            if (textLines.size() > 1) {
                k2 += 2 + (textLines.size() - 1) * 10;
            }
            if (l1 + i2 > width) {
                l1 -= 28 + i2;
            }
            if (i22 + k2 + 6 > height) {
                i22 = height - k2 - 6;
            }
            zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            int l2 = -267386864;
            GuiScreen.drawGradientRect(l1 - 3, i22 - 4, l1 + i2 + 3, i22 - 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(l1 - 3, i22 + k2 + 3, l1 + i2 + 3, i22 + k2 + 4, -267386864, -267386864);
            GuiScreen.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 + k2 + 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(l1 - 4, i22 - 3, l1 - 3, i22 + k2 + 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(l1 + i2 + 3, i22 - 3, l1 + i2 + 4, i22 + k2 + 3, -267386864, -267386864);
            int i1 = 0x505000FF;
            int j1 = 1344798847;
            GuiScreen.drawGradientRect(l1 - 3, i22 - 3 + 1, l1 - 3 + 1, i22 + k2 + 3 - 1, 0x505000FF, 1344798847);
            GuiScreen.drawGradientRect(l1 + i2 + 2, i22 - 3 + 1, l1 + i2 + 3, i22 + k2 + 3 - 1, 0x505000FF, 1344798847);
            GuiScreen.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 - 3 + 1, 0x505000FF, 0x505000FF);
            GuiScreen.drawGradientRect(l1 - 3, i22 + k2 + 2, l1 + i2 + 3, i22 + k2 + 3, 1344798847, 1344798847);
            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, l1, i22, -1);
                if (k1 == 0) {
                    i22 += 2;
                }
                i22 += 10;
            }
            zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(ITextComponent component, int x2, int y2) {
        if (component != null && component.getStyle().getHoverEvent() != null) {
            HoverEvent hoverevent = component.getStyle().getHoverEvent();
            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = ItemStack.field_190927_a;
                try {
                    NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                    if (nbtbase instanceof NBTTagCompound) {
                        itemstack = new ItemStack(nbtbase);
                    }
                }
                catch (NBTException nbtbase) {
                    // empty catch block
                }
                if (itemstack.func_190926_b()) {
                    this.drawCreativeTabHoveringText((Object)((Object)TextFormatting.RED) + "Invalid Item!", x2, y2);
                } else {
                    this.renderToolTip(itemstack, x2, y2);
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                if (this.mc.gameSettings.advancedItemTooltips) {
                    try {
                        NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                        ArrayList<String> list = Lists.newArrayList();
                        list.add(nbttagcompound.getString("name"));
                        if (nbttagcompound.hasKey("type", 8)) {
                            String s2 = nbttagcompound.getString("type");
                            list.add("Type: " + s2);
                        }
                        list.add(nbttagcompound.getString("id"));
                        this.drawHoveringText(list, x2, y2);
                    }
                    catch (NBTException var8) {
                        this.drawCreativeTabHoveringText((Object)((Object)TextFormatting.RED) + "Invalid Entity!", x2, y2);
                    }
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                this.drawHoveringText(this.mc.fontRendererObj.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(width / 2, 200)), x2, y2);
            }
            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {
    }

    public boolean handleComponentClick(ITextComponent component) {
        if (component == null) {
            return false;
        }
        ClickEvent clickevent = component.getStyle().getClickEvent();
        if (GuiScreen.isShiftKeyDown()) {
            if (component.getStyle().getInsertion() != null) {
                this.setText(component.getStyle().getInsertion(), false);
            }
        } else if (clickevent != null) {
            block19: {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s2 = uri.getScheme();
                        if (s2 == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }
                        if (!PROTOCOLS.contains(s2.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s2.toLowerCase(Locale.ROOT));
                        }
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink((GuiYesNoCallback)this, clickevent.getValue(), 31102009, false));
                            break block19;
                        }
                        this.openWebLink(uri);
                    }
                    catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for {}", (Object)clickevent, (Object)urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = new File(clickevent.getValue()).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickevent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat) {
        if (addToChat) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        this.mc.player.sendChatMessage(msg);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (int i2 = 0; i2 < this.buttonList.size(); ++i2) {
                GuiButton guibutton = this.buttonList.get(i2);
                if (!guibutton.mousePressed(this.mc, mouseX, mouseY)) continue;
                this.selectedButton = guibutton;
                guibutton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(guibutton);
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    protected void actionPerformed(GuiButton button) throws IOException {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        GuiScreen.width = width;
        GuiScreen.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void setGuiSize(int w2, int h2) {
        width = w2;
        height = h2;
    }

    public void initGui() {
        pathToPic = "textures/backs/b" + RandomUtils.nextInt(1, 17) + ".jpg";
    }

    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException {
        int i2 = Mouse.getEventX() * width / this.mc.displayWidth;
        int j2 = height - Mouse.getEventY() * height / this.mc.displayHeight - 1;
        int k2 = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }
            this.eventButton = k2;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i2, j2, this.eventButton);
        } else if (k2 != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }
            this.eventButton = -1;
            this.mouseReleased(i2, j2, k2);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long l2 = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i2, j2, this.eventButton, l2);
        }
    }

    public void handleKeyboardInput() throws IOException {
        char c0 = Keyboard.getEventCharacter();
        if (Keyboard.getEventKey() == 0 && c0 >= ' ' || Keyboard.getEventKeyState()) {
            this.keyTyped(c0, Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    public void updateScreen() {
    }

    public void onGuiClosed() {
    }

    public static void drawDefaultBackground() {
        TimerUtils utils = new TimerUtils();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GuiScreen.drawBack(pathToPic);
        GuiScreen.drawScaledCustomSizeModalRect(0.0f, 0.0f, 0.0f, 0.0f, width, height, width, height, width, height);
    }

    public void drawWorldBackground(int tint) {
        if (this.mc.world != null) {
            GuiScreen.drawGradientRect(0, 0, width, height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, height, 0.0).tex(0.0, (float)height / 32.0f + (float)tint).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(width, height, 0.0).tex((float)width / 32.0f, (float)height / 32.0f + (float)tint).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(width, 0.0, 0.0).tex((float)width / 32.0f, tint).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public static void drawBack(String kek) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        if (HackConfigs.ThemeColor == new Color(0, 100, 0).getRGB() * 10) {
            GlStateManager.color(0.7f, 0.9f, 0.2f, 1.0f);
        } else if (HackConfigs.ThemeColor == new Color(0, 0, 100).getRGB() * 10) {
            GlStateManager.color(0.6f, 0.2f, 0.7f, 1.0f);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(kek));
        GuiScreen.drawScaledCustomSizeModalRect(0.0f, 0.0f, 0.0f, 0.0f, width, height, width, height, width, height);
    }

    public void drawB17() {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        if (HackConfigs.ThemeColor == new Color(100, 0, 0).getRGB() * 10) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GuiScreen.drawBack("textures/misc/B17red.jpg");
        } else {
            GuiScreen.drawBack("textures/misc/B17.jpg");
            GuiScreen.drawScaledCustomSizeModalRect(0.0f, 0.0f, 0.0f, 0.0f, width, height, width, height, width, height);
        }
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (id2 == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }
            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        }
        catch (Throwable throwable1) {
            Throwable throwable = throwable1.getCause();
            LOGGER.error("Couldn't open link: {}", (Object)(throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
        }
    }

    public static boolean isCtrlKeyDown() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
        }
        return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID) {
        return keyID == 45 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID) {
        return keyID == 47 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID) {
        return keyID == 46 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID) {
        return keyID == 30 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w2, int h2) {
        this.setWorldAndResolution(mcIn, w2, h2);
    }
}


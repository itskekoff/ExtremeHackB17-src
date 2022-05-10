package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;

public class GuiLanguage
extends GuiScreen {
    protected GuiScreen parentScreen;
    private List list;
    private final GameSettings game_settings_3;
    private final LanguageManager languageManager;
    private GuiOptionButton forceUnicodeFontBtn;
    private GuiOptionButton confirmSettingsBtn;

    public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager) {
        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    @Override
    public void initGui() {
        this.forceUnicodeFontBtn = this.addButton(new GuiOptionButton(100, width / 2 - 155, height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
        this.confirmSettingsBtn = this.addButton(new GuiOptionButton(6, width / 2 - 155 + 160, height - 38, I18n.format("gui.done", new Object[0])));
        this.list = new List(this.mc);
        this.list.registerScrollButtons(7, 8);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            switch (button.id) {
                case 5: {
                    break;
                }
                case 6: {
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;
                }
                case 100: {
                    if (!(button instanceof GuiOptionButton)) break;
                    this.game_settings_3.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                    button.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                    int i2 = scaledresolution.getScaledWidth();
                    int j2 = scaledresolution.getScaledHeight();
                    this.setWorldAndResolution(this.mc, i2, j2);
                    break;
                }
                default: {
                    this.list.actionPerformed(button);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("options.language", new Object[0]), width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "(" + I18n.format("options.languageWarning", new Object[0]) + ")", width / 2, height - 56, 0x808080);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class List
    extends GuiSlot {
        private final java.util.List<String> langCodeList;
        private final Map<String, Language> languageMap;

        public List(Minecraft mcIn) {
            super(mcIn, width, height, 32, height - 65 + 4, 18);
            this.langCodeList = Lists.newArrayList();
            this.languageMap = Maps.newHashMap();
            for (Language language : GuiLanguage.this.languageManager.getLanguages()) {
                this.languageMap.put(language.getLanguageCode(), language);
                this.langCodeList.add(language.getLanguageCode());
            }
        }

        @Override
        protected int getSize() {
            return this.langCodeList.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            Language language = this.languageMap.get(this.langCodeList.get(slotIndex));
            GuiLanguage.this.languageManager.setCurrentLanguage(language);
            ((GuiLanguage)GuiLanguage.this).game_settings_3.language = language.getLanguageCode();
            this.mc.refreshResources();
            GuiLanguage.this.fontRendererObj.setUnicodeFlag(GuiLanguage.this.languageManager.isCurrentLocaleUnicode() || ((GuiLanguage)GuiLanguage.this).game_settings_3.forceUnicodeFont);
            GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
            ((GuiLanguage)GuiLanguage.this).confirmSettingsBtn.displayString = I18n.format("gui.done", new Object[0]);
            ((GuiLanguage)GuiLanguage.this).forceUnicodeFontBtn.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.game_settings_3.saveOptions();
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return this.langCodeList.get(slotIndex).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
        }

        @Override
        protected int getContentHeight() {
            return this.getSize() * 18;
        }

        @Override
        protected void drawBackground() {
            GuiLanguage.drawDefaultBackground();
        }

        @Override
        protected void func_192637_a(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
            GuiLanguage.this.fontRendererObj.setBidiFlag(true);
            GuiLanguage.this.drawCenteredString(GuiLanguage.this.fontRendererObj, this.languageMap.get(this.langCodeList.get(p_192637_1_)).toString(), this.width / 2, p_192637_3_ + 1, 0xFFFFFF);
            GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
        }
    }
}


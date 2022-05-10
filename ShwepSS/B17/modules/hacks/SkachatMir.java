package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.hacks.Downloader;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class SkachatMir
extends GuiScreen {
    private int saveStep;
    private int visibleTime;
    private GuiTextField chunksize;

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void initGui() {
        Minecraft mc = Minecraft.getMinecraft();
        Keyboard.enableRepeatEvents(true);
        this.saveStep = 0;
        this.buttonList.clear();
        int i2 = -16;
        int j2 = 98;
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + -16, I18n.format("\u041d\u0430\u0447\u0430\u0442\u044c \u0432\u044b\u043a\u0430\u0447\u043a\u0443 \u0447\u0430\u043d\u043a\u043e\u0432", new Object[0])));
        if (mc.isSingleplayer()) {
            this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 180 + -16, I18n.format("\u0432\u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0447\u0430\u043d\u043a\u0438", new Object[0])));
        } else {
            this.buttonList.add(new GuiButton(100, width / 2 - 100, height / 4 + 180 + -16, I18n.format("\u0417\u0430\u0439\u0434\u0438\u0442\u0435 \u0432 \u043f\u043b\u043e\u0441\u043a\u0438\u0439 \u043c\u0438\u0440 \u0447\u0442\u043e\u0431\u044b \u0432\u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0447\u0430\u043d\u043a\u0438", new Object[0])));
        }
        this.chunksize = new GuiTextField(2, this.fontRendererObj, width / 2 - 100, 146, 200, 20);
        this.chunksize.setText("200");
    }

    @Override
    protected void keyTyped(char c2, int i2) throws IOException {
        this.chunksize.textboxKeyTyped(c2, i2);
        if (c2 == '\t' && this.chunksize.isFocused()) {
            this.chunksize.setFocused(false);
        }
        if (c2 == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int i2, int j2, int k2) throws IOException {
        super.mouseClicked(i2, j2, k2);
        this.chunksize.mouseClicked(i2, j2, k2);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        final Minecraft mc = Minecraft.getMinecraft();
        switch (button.id) {
            case 1: {
                Downloader.enabled = true;
                Downloader.blocks.clear();
                Downloader.loadedBlockPos.clear();
                try {
                    new Thread(){

                        @Override
                        public void run() {
                            try {
                                Downloader.downloadBlocks(Integer.parseInt(SkachatMir.this.chunksize.getText().toString()));
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }.start();
                }
                catch (Exception exception) {}
                break;
            }
            case 2: {
                Downloader.enabled = false;
                mc.renderGlobal.loadRenderers();
                ChatUtils.emessage("\u0421\u043a\u0430\u0447\u043a\u0430 \u0447\u0430\u043d\u043a\u043e\u0432 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430 \u0432\u0430\u043c\u0438. \u0422\u0435\u043f\u0435\u0440\u044c \u0437\u0430\u0439\u0434\u0438\u0442\u0435 \u0432 \u043e\u0434\u0438\u043d\u043e\u0447\u043d\u044b\u0439 \u043f\u043b\u043e\u0441\u043a\u0438\u0439 \u043c\u0438\u0440");
                break;
            }
            case 3: {
                try {
                    ChatUtils.emessage("\u0412\u0441\u0442\u0430\u0432\u043a\u0430 \u0431\u043b\u043e\u043a\u043e\u0432....");
                    new Thread(){

                        @Override
                        public void run() {
                            try {
                                if (!Downloader.blocks.isEmpty()) {
                                    for (Downloader.WorldBlock block : Downloader.blocks) {
                                        mc.theIntegratedServer.getEntityWorld().setBlockState(block.b, block.s);
                                    }
                                    mc.world.getSaveHandler().flush();
                                    BlockPos firstBP = Downloader.blocks.get((int)0).b;
                                    ChatUtils.emessage(String.valueOf(ChatUtils.green) + "\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d \u0451\u0431\u0430\u043d\u044b\u0439 \u043c\u0438\u0440, \u0442\u0435\u043f\u043d\u0438\u0441\u044c \u043d\u0430 " + firstBP.getX() + " " + firstBP.getY() + " " + firstBP.getZ());
                                    mc.theIntegratedServer.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerPosLook(firstBP.getX(), firstBP.getY() + 10, firstBP.getZ(), 0.0f, 0.0f, null, 0));
                                } else {
                                    ChatUtils.emessage(String.valueOf(ChatUtils.red) + "\u0411\u043b\u043e\u043a\u0438 \u043f\u0443\u0441\u0442\u044b\u0435! \u043f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u0441\u043d\u043e\u0432\u0430 \u0432\u044b\u043a\u0430\u0447\u0430\u0442\u044c :(");
                                }
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }.start();
                }
                catch (Exception exception) {}
                break;
            }
            case 100: {
                mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    public void updateScreen() {
        this.chunksize.updateCursorCounter();
        super.updateScreen();
        ++this.visibleTime;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Keyboard.isKeyDown(1)) {
            this.mc.displayGuiScreen(null);
        }
        this.chunksize.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, I18n.format(String.valueOf(ChatUtils.cyan) + ChatUtils.l + "ExtremeHack \u0432\u044b\u043a\u0430\u0447\u043a\u0430 \u0442\u0435\u0440\u0440\u0438\u0442\u043e\u0440\u0438\u0438", new Object[0]), width / 2, 40, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}


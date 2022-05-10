package viamcp.gui;

import ShwepSS.B17.ChatUtils;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

public class GuiProtocolSelector
extends GuiScreen {
    private GuiScreen parent;
    public SlotList list;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 27, 200, 20, "Back"));
        this.list = new SlotList(this.mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        this.list.actionPerformed(p_actionPerformed_1_);
        if (p_actionPerformed_1_.id == 1) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        this.list.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.fontRendererObj, String.valueOf(ChatUtils.cyan) + (Object)((Object)TextFormatting.BOLD) + "EHack. B17 version", width / 4, 6, 0xFFFFFF);
        GL11.glPopMatrix();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    class SlotList
    extends GuiSlot {
        public SlotList(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
            super(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_);
        }

        @Override
        protected int getSize() {
            return ProtocolCollection.values().length;
        }

        @Override
        protected void elementClicked(int i2, boolean b2, int i1, int i22) {
            ViaMCP.getInstance().setVersion(ProtocolCollection.values()[i2].getVersion().getVersion());
        }

        @Override
        protected boolean isSelected(int i2) {
            return false;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void func_192637_a(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
            GuiProtocolSelector.this.drawCenteredString(this.mc.fontRendererObj, String.valueOf(ViaMCP.getInstance().getVersion() == ProtocolCollection.values()[p_192637_1_].getVersion().getVersion() ? String.valueOf(ChatUtils.cyan) + (Object)((Object)TextFormatting.BOLD) : TextFormatting.GRAY.toString()) + ProtocolCollection.getProtocolById(ProtocolCollection.values()[p_192637_1_].getVersion().getVersion()).getName(), this.width / 2, p_192637_3_ + 2, -1);
        }
    }
}


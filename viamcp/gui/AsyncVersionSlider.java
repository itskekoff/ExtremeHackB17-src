package viamcp.gui;

import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

public class AsyncVersionSlider
extends GuiButton {
    private float dragValue = (float)(ProtocolCollection.values().length - Arrays.asList(ProtocolCollection.values()).indexOf((Object)ProtocolCollection.getProtocolCollectionById(340))) / (float)ProtocolCollection.values().length;
    private final ProtocolCollection[] values = ProtocolCollection.values();
    private float sliderValue;
    public boolean dragging;

    public AsyncVersionSlider(int buttonId, int x2, int y2, int widthIn, int heightIn) {
        super(buttonId, x2, y2, Math.max(widthIn, 110), heightIn, "");
        Collections.reverse(Arrays.asList(this.values));
        this.sliderValue = this.dragValue;
        this.displayString = this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getName();
    }

    @Override
    public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.func_191745_a(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.dragValue = this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0f, 1.0f);
                this.displayString = this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getName();
                ViaMCP.getInstance().setVersion(this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getVersion());
            }
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.dragValue = this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0f, 1.0f);
            this.displayString = this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getName();
            ViaMCP.getInstance().setVersion(this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getVersion());
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    public void setVersion(int protocol) {
        this.sliderValue = this.dragValue = (float)(ProtocolCollection.values().length - Arrays.asList(ProtocolCollection.values()).indexOf((Object)ProtocolCollection.getProtocolCollectionById(protocol))) / (float)ProtocolCollection.values().length;
        this.displayString = this.values[(int)(this.sliderValue * (float)(this.values.length - 1))].getVersion().getName();
    }
}


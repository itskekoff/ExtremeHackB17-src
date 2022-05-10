package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory
extends GuiContainer {
    private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/horse.png");
    private final IInventory playerInventory;
    private final IInventory horseInventory;
    private final AbstractHorse horseEntity;
    private float mousePosx;
    private float mousePosY;

    public GuiScreenHorseInventory(IInventory playerInv, IInventory horseInv, AbstractHorse horse) {
        super(new ContainerHorseInventory(playerInv, horseInv, horse, Minecraft.getMinecraft().player));
        this.playerInventory = playerInv;
        this.horseInventory = horseInv;
        this.horseEntity = horse;
        this.allowUserInput = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 0x404040);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        AbstractChestHorse abstractchesthorse;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i2 = (width - this.xSize) / 2;
        int j2 = (height - this.ySize) / 2;
        this.drawTexturedModalRect(i2, j2, 0, 0, this.xSize, this.ySize);
        if (this.horseEntity instanceof AbstractChestHorse && (abstractchesthorse = (AbstractChestHorse)this.horseEntity).func_190695_dh()) {
            this.drawTexturedModalRect(i2 + 79, j2 + 17, 0, this.ySize, abstractchesthorse.func_190696_dl() * 18, 54);
        }
        if (this.horseEntity.func_190685_dA()) {
            this.drawTexturedModalRect(i2 + 7, j2 + 35 - 18, 18, this.ySize + 54, 18, 18);
        }
        if (this.horseEntity.func_190677_dK()) {
            if (this.horseEntity instanceof EntityLlama) {
                this.drawTexturedModalRect(i2 + 7, j2 + 35, 36, this.ySize + 54, 18, 18);
            } else {
                this.drawTexturedModalRect(i2 + 7, j2 + 35, 0, this.ySize + 54, 18, 18);
            }
        }
        GuiInventory.drawEntityOnScreen(i2 + 51, j2 + 60, 17, (float)(i2 + 51) - this.mousePosx, (float)(j2 + 75 - 50) - this.mousePosY, this.horseEntity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mousePosx = mouseX;
        this.mousePosY = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
    }
}


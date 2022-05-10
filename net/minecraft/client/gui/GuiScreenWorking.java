package net.minecraft.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IProgressUpdate;

public class GuiScreenWorking
extends GuiScreen
implements IProgressUpdate {
    private String title = "";
    private String stage = "";
    private int progress;
    private boolean doneWorking;

    @Override
    public void displaySavingString(String message) {
        this.resetProgressAndMessage(message);
    }

    @Override
    public void resetProgressAndMessage(String message) {
        this.title = message;
        this.displayLoadingString("Working...");
    }

    @Override
    public void displayLoadingString(String message) {
        this.stage = message;
        this.setLoadingProgress(0);
    }

    @Override
    public void setLoadingProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public void setDoneWorking() {
        this.doneWorking = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.doneWorking) {
            if (!this.mc.isConnectedToRealms()) {
                this.mc.displayGuiScreen(null);
            }
        } else {
            GuiScreenWorking.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, this.title, width / 2, 70, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, String.valueOf(this.stage) + " " + this.progress + "%", width / 2, 90, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}


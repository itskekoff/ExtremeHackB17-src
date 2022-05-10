package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class GuiCommandBlock
extends GuiScreen
implements ITabCompleter {
    private GuiTextField commandTextField;
    private GuiTextField previousOutputTextField;
    private final TileEntityCommandBlock commandBlock;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton outputBtn;
    private GuiButton modeBtn;
    private GuiButton conditionalBtn;
    private GuiButton autoExecBtn;
    private boolean trackOutput;
    private TileEntityCommandBlock.Mode commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
    private TabCompleter tabCompleter;
    private boolean conditional;
    private boolean automatic;

    public GuiCommandBlock(TileEntityCommandBlock commandBlockIn) {
        this.commandBlock = commandBlockIn;
    }

    @Override
    public void updateScreen() {
        this.commandTextField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        final CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.doneBtn = this.addButton(new GuiButton(0, width / 2 - 4 - 150, height / 4 + 120 + 12, 150, 20, I18n.format("gui.done", new Object[0])));
        this.cancelBtn = this.addButton(new GuiButton(1, width / 2 + 4, height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.outputBtn = this.addButton(new GuiButton(4, width / 2 + 150 - 20, 135, 20, 20, "O"));
        this.modeBtn = this.addButton(new GuiButton(5, width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.format("advMode.mode.sequence", new Object[0])));
        this.conditionalBtn = this.addButton(new GuiButton(6, width / 2 - 50, 165, 100, 20, I18n.format("advMode.mode.unconditional", new Object[0])));
        this.autoExecBtn = this.addButton(new GuiButton(7, width / 2 + 50 + 4, 165, 100, 20, I18n.format("advMode.mode.redstoneTriggered", new Object[0])));
        this.commandTextField = new GuiTextField(2, this.fontRendererObj, width / 2 - 150, 50, 300, 20);
        this.commandTextField.setMaxStringLength(32500);
        this.commandTextField.setFocused(true);
        this.previousOutputTextField = new GuiTextField(3, this.fontRendererObj, width / 2 - 150, 135, 276, 20);
        this.previousOutputTextField.setMaxStringLength(32500);
        this.previousOutputTextField.setEnabled(false);
        this.previousOutputTextField.setText("-");
        this.doneBtn.enabled = false;
        this.outputBtn.enabled = false;
        this.modeBtn.enabled = false;
        this.conditionalBtn.enabled = false;
        this.autoExecBtn.enabled = false;
        this.tabCompleter = new TabCompleter(this.commandTextField, true){

            @Override
            @Nullable
            public BlockPos getTargetBlockPos() {
                return commandblockbaselogic.getPosition();
            }
        };
    }

    public void updateGui() {
        CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
        this.commandTextField.setText(commandblockbaselogic.getCommand());
        this.trackOutput = commandblockbaselogic.shouldTrackOutput();
        this.commandBlockMode = this.commandBlock.getMode();
        this.conditional = this.commandBlock.isConditional();
        this.automatic = this.commandBlock.isAuto();
        this.updateCmdOutput();
        this.updateMode();
        this.updateConditional();
        this.updateAutoExec();
        this.doneBtn.enabled = true;
        this.outputBtn.enabled = true;
        this.modeBtn.enabled = true;
        this.conditionalBtn.enabled = true;
        this.autoExecBtn.enabled = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
            if (button.id == 1) {
                commandblockbaselogic.setTrackOutput(this.trackOutput);
                this.mc.displayGuiScreen(null);
            } else if (button.id == 0) {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                commandblockbaselogic.fillInInfo(packetbuffer);
                packetbuffer.writeString(this.commandTextField.getText());
                packetbuffer.writeBoolean(commandblockbaselogic.shouldTrackOutput());
                packetbuffer.writeString(this.commandBlockMode.name());
                packetbuffer.writeBoolean(this.conditional);
                packetbuffer.writeBoolean(this.automatic);
                this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|AutoCmd", packetbuffer));
                if (!commandblockbaselogic.shouldTrackOutput()) {
                    commandblockbaselogic.setLastOutput(null);
                }
                this.mc.displayGuiScreen(null);
            } else if (button.id == 4) {
                commandblockbaselogic.setTrackOutput(!commandblockbaselogic.shouldTrackOutput());
                this.updateCmdOutput();
            } else if (button.id == 5) {
                this.nextMode();
                this.updateMode();
            } else if (button.id == 6) {
                this.conditional = !this.conditional;
                this.updateConditional();
            } else if (button.id == 7) {
                this.automatic = !this.automatic;
                this.updateAutoExec();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.tabCompleter.resetRequested();
        if (keyCode == 15) {
            this.tabCompleter.complete();
        } else {
            this.tabCompleter.resetDidComplete();
        }
        this.commandTextField.textboxKeyTyped(typedChar, keyCode);
        this.previousOutputTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 1) {
                this.actionPerformed(this.cancelBtn);
            }
        } else {
            this.actionPerformed(this.doneBtn);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.commandTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.previousOutputTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawCenteredString(this.fontRendererObj, I18n.format("advMode.setCommand", new Object[0]), width / 2, 20, 0xFFFFFF);
        this.drawString(this.fontRendererObj, I18n.format("advMode.command", new Object[0]), width / 2 - 150, 40, 0xA0A0A0);
        this.commandTextField.drawTextBox();
        int i2 = 75;
        int j2 = 0;
        this.drawString(this.fontRendererObj, I18n.format("advMode.nearestPlayer", new Object[0]), width / 2 - 140, i2 + j2++ * this.fontRendererObj.FONT_HEIGHT, 0xA0A0A0);
        this.drawString(this.fontRendererObj, I18n.format("advMode.randomPlayer", new Object[0]), width / 2 - 140, i2 + j2++ * this.fontRendererObj.FONT_HEIGHT, 0xA0A0A0);
        this.drawString(this.fontRendererObj, I18n.format("advMode.allPlayers", new Object[0]), width / 2 - 140, i2 + j2++ * this.fontRendererObj.FONT_HEIGHT, 0xA0A0A0);
        this.drawString(this.fontRendererObj, I18n.format("advMode.allEntities", new Object[0]), width / 2 - 140, i2 + j2++ * this.fontRendererObj.FONT_HEIGHT, 0xA0A0A0);
        this.drawString(this.fontRendererObj, I18n.format("advMode.self", new Object[0]), width / 2 - 140, i2 + j2++ * this.fontRendererObj.FONT_HEIGHT, 0xA0A0A0);
        if (!this.previousOutputTextField.getText().isEmpty()) {
            i2 = i2 + j2 * this.fontRendererObj.FONT_HEIGHT + 1;
            this.drawString(this.fontRendererObj, I18n.format("advMode.previousOutput", new Object[0]), width / 2 - 150, i2 + 4, 0xA0A0A0);
            this.previousOutputTextField.drawTextBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void updateCmdOutput() {
        CommandBlockBaseLogic commandblockbaselogic = this.commandBlock.getCommandBlockLogic();
        if (commandblockbaselogic.shouldTrackOutput()) {
            this.outputBtn.displayString = "O";
            if (commandblockbaselogic.getLastOutput() != null) {
                this.previousOutputTextField.setText(commandblockbaselogic.getLastOutput().getUnformattedText());
            }
        } else {
            this.outputBtn.displayString = "X";
            this.previousOutputTextField.setText("-");
        }
    }

    private void updateMode() {
        switch (this.commandBlockMode) {
            case SEQUENCE: {
                this.modeBtn.displayString = I18n.format("advMode.mode.sequence", new Object[0]);
                break;
            }
            case AUTO: {
                this.modeBtn.displayString = I18n.format("advMode.mode.auto", new Object[0]);
                break;
            }
            case REDSTONE: {
                this.modeBtn.displayString = I18n.format("advMode.mode.redstone", new Object[0]);
            }
        }
    }

    private void nextMode() {
        switch (this.commandBlockMode) {
            case SEQUENCE: {
                this.commandBlockMode = TileEntityCommandBlock.Mode.AUTO;
                break;
            }
            case AUTO: {
                this.commandBlockMode = TileEntityCommandBlock.Mode.REDSTONE;
                break;
            }
            case REDSTONE: {
                this.commandBlockMode = TileEntityCommandBlock.Mode.SEQUENCE;
            }
        }
    }

    private void updateConditional() {
        this.conditionalBtn.displayString = this.conditional ? I18n.format("advMode.mode.conditional", new Object[0]) : I18n.format("advMode.mode.unconditional", new Object[0]);
    }

    private void updateAutoExec() {
        this.autoExecBtn.displayString = this.automatic ? I18n.format("advMode.mode.autoexec.bat", new Object[0]) : I18n.format("advMode.mode.redstoneTriggered", new Object[0]);
    }

    @Override
    public void setCompletions(String ... newCompletions) {
        this.tabCompleter.setCompletions(newCompletions);
    }
}


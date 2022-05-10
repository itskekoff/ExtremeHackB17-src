package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;

public class MovementInputFromOptions
extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    @Override
    public void updatePlayerMoveState() {
        moveStrafe = 0.0f;
        this.field_192832_b = 0.0f;
        if (this.gameSettings.keyBindForward.isKeyDown()) {
            this.field_192832_b += 1.0f;
            this.forwardKeyDown = true;
        } else {
            this.forwardKeyDown = false;
        }
        if (this.gameSettings.keyBindBack.isKeyDown()) {
            this.field_192832_b -= 1.0f;
            this.backKeyDown = true;
        } else {
            this.backKeyDown = false;
        }
        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            moveStrafe += 1.0f;
            this.leftKeyDown = true;
        } else {
            this.leftKeyDown = false;
        }
        if (this.gameSettings.keyBindRight.isKeyDown()) {
            moveStrafe -= 1.0f;
            this.rightKeyDown = true;
        } else {
            this.rightKeyDown = false;
        }
        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();
        if (this.sneak) {
            moveStrafe = (float)((double)moveStrafe * 0.3);
            this.field_192832_b = (float)((double)this.field_192832_b * 0.3);
        }
    }
}


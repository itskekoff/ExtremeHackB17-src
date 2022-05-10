package net.minecraft.util;

import net.minecraft.util.math.Vec2f;

public class MovementInput {
    public static float moveStrafe;
    public float field_192832_b;
    public boolean forwardKeyDown;
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;

    public void updatePlayerMoveState() {
    }

    public Vec2f getMoveVector() {
        return new Vec2f(moveStrafe, this.field_192832_b);
    }
}


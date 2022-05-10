package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvWalk
extends Module {
    public InvWalk() {
        super("InvWalk", "\u0445\u043e\u0434\u0438\u043c \u0441 \u043e\u0442\u043a\u0440\u044b\u0442\u044b\u043c \u0438\u043d\u0432\u0435\u043d\u0442\u043e\u043c", -1, Category.Movement, true);
    }

    @Override
    public void onTick() {
        KeyBinding[] arrkeyBinding = new KeyBinding[]{Minecraft.getMinecraft().gameSettings.keyBindForward, Minecraft.getMinecraft().gameSettings.keyBindBack, Minecraft.getMinecraft().gameSettings.keyBindLeft, Minecraft.getMinecraft().gameSettings.keyBindRight, Minecraft.getMinecraft().gameSettings.keyBindJump, Minecraft.getMinecraft().gameSettings.keyBindSprint};
        KeyBinding[] keys = arrkeyBinding;
        if (Minecraft.getMinecraft().currentScreen != null && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            KeyBinding[] arrayOfKeyBinding = keys;
            int i2 = keys.length;
            for (int b2 = 0; b2 < i2; b2 = (int)((byte)(b2 + 1))) {
                KeyBinding bind = arrayOfKeyBinding[b2];
                bind.pressed = Keyboard.isKeyDown(bind.getKeyCode());
            }
        }
    }
}


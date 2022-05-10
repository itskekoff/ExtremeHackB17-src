package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.AWTException;
import java.awt.Robot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class Criperstack
extends Module {
    public Criperstack() {
        super("VerstakCrash", "Switches to the apropriate tool to do the job", 21, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            try {
                Criperstack.click1(442, 360);
                Criperstack.click1(498, 360);
            }
            catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void click1(int x2, int y2) throws AWTException {
        Robot bot2 = new Robot();
        bot2.mouseMove(x2, y2);
        bot2.mousePress(1024);
        bot2.mouseRelease(1024);
    }

    public static void click2(int x2, int y2) throws AWTException {
        Robot bot2 = new Robot();
        bot2.mouseMove(x2, y2);
        bot2.mousePress(1024);
        bot2.mouseRelease(1024);
    }

    @Override
    public void onDisable() {
    }
}


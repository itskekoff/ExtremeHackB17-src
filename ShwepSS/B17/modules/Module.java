package ShwepSS.B17.modules;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import java.util.ArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.play.server.SPacketChat;

public class Module {
    public static ArrayList<String> Predictions;
    protected Gui gui = new Gui();
    public boolean shown;
    public boolean enabled;
    public String name;
    public String command;
    public String desc;
    public Category category;
    public int color;
    public int key;
    public int slider;

    public Module(String name, String desc, int key, Category cat2, boolean shown) {
        this.name = name;
        this.desc = desc;
        this.key = key;
        this.category = cat2;
        this.shown = shown;
    }

    public void runCommand(String cmd) {
        this.onToggle();
        ChatUtils.message(String.valueOf(String.valueOf(this.name)) + (this.enabled ? " is now on." : " is now off."));
    }

    public void onToggle() {
        this.toggle();
    }

    public void externalCommand(String s2) {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.desc;
    }

    public String getCommand() {
        return this.command;
    }

    public int getColor() {
        return this.color;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int k2) {
        this.key = k2;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isShown() {
        return this.shown;
    }

    public void toggle() {
        boolean bl2 = this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
            this.slider = 0;
        } else {
            this.onDisable();
            this.slider = 0;
        }
    }

    public void setEnabled(boolean enabled) {
        boolean bl2 = this.enabled = !this.enabled;
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void onPredicitons(String s2) {
        Predictions.add(this.command);
    }

    public void onTick() {
    }

    public void onRender() {
    }

    public void preMotionUpdate() {
    }

    public void postMotionUpdate() {
    }

    public void onClickBlock(int x2, int y2, int z2) {
    }

    public void onChat(SPacketChat chat) {
    }

    public boolean onRightClick(int par1, int par2, int par3, int par4) {
        return false;
    }

    public int onRightClickDelayTimer(int i2) {
        return i2;
    }
}


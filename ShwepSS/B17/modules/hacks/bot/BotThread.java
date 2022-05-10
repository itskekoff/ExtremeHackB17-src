package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.modules.hacks.bot.JoinerBot;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.apache.commons.lang3.RandomUtils;

public class BotThread
implements Runnable {
    private boolean isActive = true;

    void disable() {
        this.isActive = false;
    }

    BotThread() {
    }

    void enable() {
        this.isActive = true;
    }

    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        int counter = 1;
        while (this.isActive) {
            System.out.println("\u041f\u043e\u0442\u043e\u043a \u0435\u0431\u0430\u0448\u0438\u0442 " + counter++);
            JoinerBot bot2 = new JoinerBot("Extreme" + RandomUtils.nextInt(0, 9999999));
            bot2.connect(String.valueOf(GuiConnecting.ip) + ":" + GuiConnecting.port);
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }
}


package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.bot.BotThread;
import com.github.steveice10.packetlib.Client;
import org.apache.commons.lang3.RandomUtils;

public class Joiner
extends Module {
    public static Module instance;
    public BotThread thread = new BotThread();
    private Client client;

    public Joiner() {
        super("==JoinerStart", "\u043c\u043e\u0436\u043d\u0430 \u0448\u043a\u0438\u043b\u043e\u0430\u0442\u0435\u0440\u043d\u043e\u0441\u044b \u0431\u043e\u0442\u043e\u0432\u0430\u0442\u044c", 0, Category.BOTS, true);
        instance = this;
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onDisable() {
        try {
            this.thread.disable();
            Thread.sleep(10L);
        }
        catch (InterruptedException e2) {
            System.out.println("Thread has been interrupted");
        }
        System.out.println("Main thread finished...");
        ChatUtils.emessage("Joiner Stopped. ");
    }

    @Override
    public void onEnable() {
        ChatUtils.emessage("Starting Joiner... ");
        for (int i2 = 0; i2 < 100; ++i2) {
            new Thread((Runnable)this.thread, String.valueOf(RandomUtils.nextInt(10, 99999))).start();
            this.thread.enable();
        }
    }
}


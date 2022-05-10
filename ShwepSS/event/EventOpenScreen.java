package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import ShwepSS.eventapi.events.callables.EventCancellable;
import net.minecraft.client.gui.GuiScreen;

public class EventOpenScreen
extends EventCancellable
implements Event {
    public GuiScreen screen;

    public EventOpenScreen(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return this.screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }
}


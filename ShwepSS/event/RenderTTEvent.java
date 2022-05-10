package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import ShwepSS.eventapi.events.callables.EventCancellable;
import net.minecraft.item.ItemStack;

public class RenderTTEvent
extends EventCancellable
implements Event {
    private ItemStack stack;
    private int x;
    private int y;

    public RenderTTEvent(ItemStack stack, int x2, int y2) {
        this.stack = stack;
        this.x = x2;
        this.y = y2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}


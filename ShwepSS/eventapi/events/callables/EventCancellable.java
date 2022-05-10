package ShwepSS.eventapi.events.callables;

import ShwepSS.eventapi.events.Cancellable;
import ShwepSS.eventapi.events.Event;

public abstract class EventCancellable
implements Event,
Cancellable {
    private boolean cancelled;

    protected EventCancellable() {
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }
}


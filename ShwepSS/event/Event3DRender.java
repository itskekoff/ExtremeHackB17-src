package ShwepSS.event;

import ShwepSS.eventapi.events.callables.EventCancellable;

public class Event3DRender
extends EventCancellable {
    public float pticks;

    public Event3DRender(float pticks) {
        this.pticks = pticks;
    }

    public float pticks() {
        return this.pticks;
    }
}


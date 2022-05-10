package ShwepSS.eventapi.events.callables;

import ShwepSS.eventapi.events.Event;
import ShwepSS.eventapi.events.Typed;

public abstract class EventTyped
implements Event,
Typed {
    private final byte type;

    protected EventTyped(byte eventType) {
        this.type = eventType;
    }

    @Override
    public byte getType() {
        return this.type;
    }
}


package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import ShwepSS.eventapi.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketRecieve
extends EventCancellable
implements Event {
    private Packet packet;

    public EventPacketRecieve(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}


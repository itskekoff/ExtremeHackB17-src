package ShwepSS.event;

import ShwepSS.eventapi.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketSend
extends EventCancellable {
    public Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }
}


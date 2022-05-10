package ShwepSS.event;

import ShwepSS.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityVelocity;

public class EventRecieveVelocity
extends EventCancellable {
    private Entity entity;
    private SPacketEntityVelocity packetIn;

    public EventRecieveVelocity(Entity entity, SPacketEntityVelocity packetIn) {
        this.entity = entity;
        this.packetIn = packetIn;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public SPacketEntityVelocity getPacket() {
        return this.packetIn;
    }
}


package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;

public class EventEntityRender
implements Event {
    public Render render;
    public EntityLivingBase entity;
    public double x;
    public double y;
    public double z;
    public Event.Type type;

    public EventEntityRender(Event.Type type, Render render, EntityLivingBase entity, double x2, double y2, double z2) {
        this.render = render;
        this.entity = entity;
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }
}


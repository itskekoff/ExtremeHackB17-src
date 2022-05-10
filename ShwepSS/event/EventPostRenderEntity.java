package ShwepSS.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventPostRenderEntity
implements Event {
    private Entity ent;
    private EntityPlayerSP playersp;

    public EventPostRenderEntity(Entity ent) {
        this.ent = ent;
    }

    public Entity getEntity() {
        return this.ent;
    }

    public Entity getPlayer() {
        return this.playersp;
    }

    @Override
    public boolean getBubbles() {
        return false;
    }

    @Override
    public boolean getCancelable() {
        return false;
    }

    @Override
    public EventTarget getCurrentTarget() {
        return null;
    }

    @Override
    public short getEventPhase() {
        return 0;
    }

    @Override
    public EventTarget getTarget() {
        return null;
    }

    @Override
    public long getTimeStamp() {
        return 0L;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void initEvent(String arg0, boolean arg1, boolean arg2) {
    }

    @Override
    public void preventDefault() {
    }

    @Override
    public void stopPropagation() {
    }
}


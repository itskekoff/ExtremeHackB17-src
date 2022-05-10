package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderGui
implements Event {
    private ScaledResolution resolution;

    public void fire(ScaledResolution resolution) {
        this.resolution = resolution;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }
}


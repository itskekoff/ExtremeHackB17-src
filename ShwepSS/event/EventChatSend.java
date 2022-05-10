package ShwepSS.event;

import ShwepSS.eventapi.events.Event;

public class EventChatSend
implements Event {
    public String streng;

    public EventChatSend(String str) {
        this.streng = str;
    }
}


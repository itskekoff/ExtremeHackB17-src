package ShwepSS.eventapi.events;

public interface Event {

    public static enum Type {
        PRE,
        POST,
        SEND,
        RECEIVE,
        CLICKL,
        CLICKM,
        CLICKR,
        PRESS,
        RELEASE,
        SCROLL,
        PACKET;

    }
}


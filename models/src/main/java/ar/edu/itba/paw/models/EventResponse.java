package ar.edu.itba.paw.models;

public class EventResponse {
    private final long userId;
    private final long eventId;
    private final String message;

    public EventResponse(long userId, long eventId, String message){
        this.userId = userId;
        this.eventId = eventId;
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public long getEventId() {
        return eventId;
    }

    public String getMessage() {
        return message;
    }
}

package ar.edu.itba.paw.models;

public class JourneyResponse {
    private final long user_id;
    private final long journey_id;
    private final String message;

    public JourneyResponse(long user_id, long journey_id, String message){
        this.user_id = user_id;
        this.journey_id = journey_id;
        this.message = message;
    }

    public long getUserId() {
        return user_id;
    }

    public long getJourneyId() {
        return journey_id;
    }

    public String getMessage() {
        return message;
    }
}

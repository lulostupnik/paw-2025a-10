package ar.edu.itba.paw.models;

public class JourneyResponse {
    private final long userId;
    private final long journeyId;
    private final String message;

    public JourneyResponse(long userId, long journeyId, String message){
        this.userId = userId;
        this.journeyId = journeyId;
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public long getJourneyId() {
        return journeyId;
    }

    public String getMessage() {
        return message;
    }
}

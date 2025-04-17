package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class EventResponse {
    private final long userId;
    private final String username;
    private final long eventId;
    private final String message;
    private final LocalDate date;



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{userId: ");
        sb.append(userId);
        sb.append(", eventId: ");
        sb.append(eventId);
        sb.append(", message: \"");
        sb.append(message);
        sb.append("\"}");
        return sb.toString();
    }
}

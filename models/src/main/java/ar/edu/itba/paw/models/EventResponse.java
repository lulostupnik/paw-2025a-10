package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@RequiredArgsConstructor
public class EventResponse {
    private final long id;
    private final long userId;
    private final String username;  //@TODO no tiene sentido esto, esta el id
    private final long eventId;
    private final String message;
    private final LocalDateTime dateTime;

    public String getFormattedDate() {
        return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


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

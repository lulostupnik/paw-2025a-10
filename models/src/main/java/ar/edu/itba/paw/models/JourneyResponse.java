package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public class JourneyResponse {
//    private final long id; @TODO hay que agregarle el id?
    private final long userId;
    private final String username;
    private final long journeyId;
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
        sb.append(", journeyId: ");
        sb.append(journeyId);
        sb.append(", message: \"");
        sb.append(message);
        sb.append("\"}");
        return sb.toString();
    }
}


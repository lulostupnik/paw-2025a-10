package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class Event {
    private final long id;
    private final User user;
    private final Date date;
    private final String description;
    private final long flyerImageId;
    private final City eventCity;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{eventID: ");
        sb.append(id);
        sb.append(", user: ");
        sb.append(user);
        sb.append(", city: ");
        sb.append(eventCity);
        sb.append(", date: \"");
        sb.append(date);       
        sb.append("\", description: \"");
        sb.append(description);
        sb.append("\", flyerID: ");
        sb.append(flyerImageId);
        sb.append("}");
        return sb.toString();
    }
}

package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
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
    private final String title;
    private final LocalTime time;
    private final String address;
    private final int attendeesLimit;

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
        sb.append("\", time: \"");
        sb.append(time != null ? time : "all-day");
        sb.append("\", address: \"");
        sb.append(address);
        sb.append("\", attendeesLimit: ");
        sb.append(attendeesLimit);                
        sb.append(", description: \"");
        sb.append(description);
        sb.append("\", flyerID: ");
        sb.append(flyerImageId);
        sb.append("}");
        return sb.toString();
    }
}

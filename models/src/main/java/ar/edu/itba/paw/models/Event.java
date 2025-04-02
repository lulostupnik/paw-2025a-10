package ar.edu.itba.paw.models;

import java.util.Date;

public class Event {
    private final User user;
    private final City eventCity;
    private final Date date;
    private final String description;
    private final long id;
    private final long flyerImageId;

    public Event(long id, User user, Date departureDate, String description, long flyerImage, City eventCity) {
        this.id = id;
        this.user = user;
        this.eventCity = eventCity;
        this.date = departureDate;
        this.flyerImageId= flyerImage;
        this.description = description;
    }

    public User getUser() {
        return user;
    }
    public City getEventCity() {
        return eventCity;
    }
    public String getDescription() {
        return description;
    }
    public long getId() {
        return id;
    }
    public long getFlyerImageId() {return flyerImageId;}
    public Date getDate() {return date;}

}

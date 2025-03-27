package ar.edu.itba.paw.models;

import java.util.Date;

public class Event {
    private final User user;
    private final String destinationCity;
    private final Date date;
    private final University destinationUniversity;
    private final String description;
    private final long id;

    public Event(long id, User user, String destinationCity, Date departureDate, University destinationUniversity, String description) {
        this.id = id;
        this.user = user;
        this.destinationCity = destinationCity;
        this.date = departureDate;
        this.destinationUniversity = destinationUniversity;
        this.description = description;
        // TODO: Add images
    }

    public User getUser() {
        return user;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getDescription() {
        return description;
    }
    public long getId() {
        return id;
    }
    public Date getDate() {
        return date;}

    public University getDestinationUniversity() {
        return destinationUniversity;
    }
}

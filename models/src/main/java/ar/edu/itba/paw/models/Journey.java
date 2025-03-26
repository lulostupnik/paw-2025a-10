package ar.edu.itba.paw.models;

import java.util.Date;

public class Journey{
    private final User user;
    private final String destinationCity;
    private final Date departureDate;
    private final Date arrivalDate;
    private final University destinationUniversity;

    public Journey(User user, String destinationCity, Date departureDate, Date arrivalDate, University destinationUniversity){
        this.user = user;
        this.destinationCity = destinationCity;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.destinationUniversity = destinationUniversity;
    }

    public User getUser() {
        return user;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public University getDestinationUniversity() {
        return destinationUniversity;
    }

}


package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class Journey{
    private final long id;
    private final User user;
    private final String destinationCity;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final University destinationUniversity; // FIXME: Cambiar por String -> lo obtenemos del toString();
    private final String description;

    public Journey(long id, User user, String destinationCity, LocalDate startDate, LocalDate endDate, University destinationUniversity, String description) {
        this.id = id;
        this.user = user;
        this.destinationCity = destinationCity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationUniversity = destinationUniversity;
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public University getDestinationUniversity() {
        return destinationUniversity;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

}


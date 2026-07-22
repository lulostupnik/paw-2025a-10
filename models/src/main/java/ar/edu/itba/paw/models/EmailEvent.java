package ar.edu.itba.paw.models;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class EmailEvent {
    private final Long id;

    private final  EmailUser user;

    private final  LocalDate date;

    private final  String description;

    private final  Long flyerImageId;

    private final  String cityName;

    private final  String title;

    private final  LocalTime time;

    private final  String address;

    private final  Integer attendeesLimit;

    private final  int attendeesCount;

    private final  boolean deleted;

    private final String deletionMessage;


    public EmailEvent(Event event) {
        this.id = event.getId();
        this.user = new EmailUser(event.getUser());
        this.date = event.getDate();
        this.description = event.getDescription();
        this.flyerImageId = event.getFlyerImageId();
        this.cityName = event.getCity().getName();
        this.title = event.getTitle();
        this.time = event.getTime();
        this.address = event.getAddress();
        this.attendeesLimit = event.getAttendeesLimit();
        this.attendeesCount = event.getAttendeesCount();
        this.deleted = event.isDeleted();
        this.deletionMessage = event.getDeletionMessage();
    }


    public boolean getFull(){
        return attendeesLimit != null && attendeesLimit <= attendeesCount;
    }

    public boolean getIsFuture() {
        if(time == null) {
            return date.atStartOfDay().isAfter(LocalDateTime.now());
        }

        return LocalDateTime.of(date, time).isAfter(LocalDateTime.now());
    }


}

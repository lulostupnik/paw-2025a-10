package ar.edu.itba.paw.models;


import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class EmailEvent {
    private Long id;

    private  EmailUser user;

    private  LocalDate date;

    private  String description;

    private  long flyerImageId;

    private  City city;

    private  String title;

    private  LocalTime time;

    private  String address;

    private  Integer attendeesLimit;

    private  int attendeesCount; //FIXME: yo borraria esto

    private  boolean deleted;

    private String deletionMessage;


    public EmailEvent(Event event) {
        this.id = event.getId();
        this.user = new EmailUser(event.getUser());
        this.date = event.getDate();
        this.description = event.getDescription();
        this.flyerImageId = event.getFlyerImageId();
        this.city = event.getCity();
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

package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.EmailUser;
import ar.edu.itba.paw.models.Journey;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmailJourney {
    private Long id;
    private EmailUser user;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private boolean deleted;
    private String deletionMessage;
    private String destinationUniversityName;

    public EmailJourney(Journey journey) {
        this.id = journey.getId();
        this.user = new EmailUser(journey.getUser());
        this.startDate = journey.getStartDate();
        this.endDate = journey.getEndDate();
        this.description = journey.getDescription();
        this.deleted = journey.isDeleted();
        this.deletionMessage = journey.getDeletionMessage();
        this.destinationUniversityName =  journey.getDestinationUniversity().getName();
    }
}

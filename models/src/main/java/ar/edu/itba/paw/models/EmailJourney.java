package ar.edu.itba.paw.models;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class EmailJourney {
    private final Long id;
    private final EmailUser user;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;
    private final boolean deleted;
    private final String deletionMessage;
    private final String destinationUniversityName;

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

package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Journey;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyService {

    Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                                    String destinationUniversity, String destinationCity, LocalDate startDate, LocalDate endDate, String description);

    void replyToJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                                    long journeyId, String message);

    List<Journey> getAllJourneys();

    Optional<Journey> getJourneyById(long id);
}

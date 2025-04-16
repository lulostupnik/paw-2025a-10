package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Journey;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyService {

    Journey createJourney(String email, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    void replyToJourney(String email, long journeyId, String message);

    List<Journey> getAllJourneys();

    Optional<Journey> getJourneyById(long id);

    List<Journey> getFilteredJourneys(String destination, LocalDate startDate, LocalDate endDate, String interest);

    Boolean userHasJourney(String email); // ja

    public List<Journey> getRecommendedJourneys(String email);

    }

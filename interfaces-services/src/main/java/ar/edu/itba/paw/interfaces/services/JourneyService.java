package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

import java.util.Date;
import java.util.Optional;

public interface JourneyService {

    Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                                    String destinationUniversity, String destinationCity, Date startDate, Date endDate, String description);


}

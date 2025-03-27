package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JourneyServiceImpl implements JourneyService {

    // private final JourneyDao journeyDao;
    private final UserService userService;
    private final UniversityService universityService;

    @Autowired
    public JourneyServiceImpl(UserService userService, UniversityService universityService) {
        this.userService = userService;
        this.universityService = universityService;
    }



    @Override
    public Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, String destinationUniversity, String destinationCity, Date startDate, Date endDate, String description) {
        // FIXME: implement
        University destination = universityService.findByName(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));
        User user = userService.createUser(email, username, firstname, lastname, originUniversity, career);
        return new Journey(user, destinationCity, startDate, endDate, destination, description);
    }
}

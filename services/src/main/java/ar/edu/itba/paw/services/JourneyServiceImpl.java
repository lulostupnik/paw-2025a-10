package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JourneyServiceImpl implements JourneyService {

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final UniversityService universityService;
    private final JourneyResponseDao journeyResponseDao;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, UniversityService universityService, JourneyResponseDao journeyResponseDao) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
    }


    @Override
    public Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, String destinationUniversity, String destinationCity, Date startDate, Date endDate, String description) {
        // FIXME: implement
        University destination = universityService.findByName(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));
        Optional<User> maybeUser = userService.findByEmail(email);
        User user;
        user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)); // todo: ¿acá debería usar el service o el dao? -> el service puede llegar a tener validaciones que ya acabo de hacer en esta clase
        // id me lo da la bd btw
        // return journeyDao.create(user, destinationCity, startDate, endDate, destination, description);
        // FIXME: chequear si el journey ya existe? (¿solo si no cree el usuario?) -> chequear sino que las fechas no se solapen
        return journeyDao.create(user, destination, destinationCity, startDate, endDate, description);
    }

    @Override
    public void replyToJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long journeyId, String message) {
        journeyDao.findById(journeyId).orElseThrow(() -> new RuntimeException("Journey not found")).getId();
        /*
        Optional<Journey> maybeJourney = journeyDao.findById(journeyId);
        if (maybeJourney.isEmpty()) {
            throw new RuntimeException("Journey not found");
        }
        */
        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)).getId();
        // tal vez deberíamos chequear por username también -> si intenta repetirlo nos va a caer una excepción de la bd
        /*
        Optional<User> maybeUser = userService.findByEmail(email);
        User user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId));
        */

        // mandar el mail

        journeyResponseDao.create(userId, journeyId, message);

    }

    @Override
    public List<Journey> getAllJourneys() {
        return journeyDao.listAll();
    }

}

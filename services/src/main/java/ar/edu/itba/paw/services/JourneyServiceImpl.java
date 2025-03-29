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

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JourneyServiceImpl implements JourneyService {

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final UniversityService universityService;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, UniversityService universityService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
    }


    @Override
    public Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, String destinationUniversity, String destinationCity, Date startDate, Date endDate, String description) {
        // FIXME: implement
        University destination = universityService.findByName(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));
        Optional<User> maybeUser = userService.findByEmail(email);
        User user;
        user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId));
        // id me lo da la bd btw
        // return journeyDao.create(user, destinationCity, startDate, endDate, destination, description);
        return new Journey(1, user, destinationCity, startDate, endDate, destination, description);
    }

    @Override
    public void replyToJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long journeyId, String message) {
        // comprobar si existe el usuario
        // Optional<User> user = userService.findByEmail(email);
        // crearlo si no existe
        // if (!user.isPresent()) {
        //     User newUser = userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId);
        // }
        // buscar el journey por id
        // Optional<Journey> journey = journeyDao.findById(journeyId);
        // si no existe, manejar el error -> ¿? -> throw new RuntimeException("Journey not found"); o NotFoundException o JourneyNotFoundException o algo así?
        // mandar el mail

    }

    @Override
    public List<Journey> getAllJourneys() {
        return journeyDao.listAll();
    }

}

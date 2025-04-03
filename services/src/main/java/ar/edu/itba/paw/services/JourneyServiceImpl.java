package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class JourneyServiceImpl implements JourneyService {

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final JourneyResponseDao journeyResponseDao;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, UniversityService universityService, JourneyResponseDao journeyResponseDao, EmailService emailService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
    }


    @Override
    public Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, String destinationUniversity, String destinationCity, LocalDate startDate, LocalDate endDate, String description) {
        //University destination = universityService.findByName(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));
        //For testing purposes, accept custom input
        //FIXME: Implement university account creation & stuff to remove custom input
        University destination = universityService.findByAny(destinationUniversity).orElseGet(() -> universityService.registerUniversity(destinationUniversity, destinationUniversity));
        Optional<User> maybeUser = userService.findByEmail(email);
        User user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)); // todo: ¿acá debería usar el service o el dao? -> el service puede llegar a tener validaciones que ya acabo de hacer en esta clase
        // id me lo da la bd btw
        
        if (!journeyDao.findOverlappingJourney(user.getId(), startDate, endDate).isPresent()) {
            new RuntimeException("There's already a journey registered in this time period");
        }
        return journeyDao.create(user, destination, destinationCity, startDate, endDate, description);
    }

    @Override
    public void replyToJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long journeyId, String message) {
        Journey journey = journeyDao.findById(journeyId).orElseThrow(() -> new RuntimeException("Journey not found"));

        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)).getId();
        // tal vez deberíamos chequear por username también -> si intenta repetirlo nos va a caer una excepción de la bd
        /*
        Optional<User> maybeUser = userService.findByEmail(email);
        User user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId));
        */



        journeyResponseDao.create(userId, journeyId, message);

        //@TODO cambiar el Locale
        emailService.answerJourneyMail( email, journey.getUser().getEmail() , firstname, lastname,username, career, originUniversity, message , new Locale("es"));
    }

    @Override
    public List<Journey> getAllJourneys() {
        return journeyDao.listAll();
    }

    @Override
    public Optional<Journey> getJourneyById(long id) {
        return journeyDao.findById(id);
    }

    public List<Journey> getFilteredJourneys(String destination, LocalDate startDate, LocalDate endDate, String interest) {
        return journeyDao.findByFilters(destination, startDate,endDate, interest);
    }

}

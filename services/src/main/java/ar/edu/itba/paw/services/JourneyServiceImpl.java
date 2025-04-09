package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
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
    private final CityService cityService;
    private final InterestService interestService;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, UniversityService universityService, JourneyResponseDao journeyResponseDao, EmailService emailService, CityService cityService, InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
        this.cityService = cityService;
        this.interestService = interestService;
    }
    private void checkDates(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date cannot be null");
        }
        if(startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        if(startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be before today");
        }
        if(endDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("End date cannot be before today");
        }
    }
    @Override
    public Journey createJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture, String destinationUniversity,
                                  LocalDate startDate, LocalDate endDate, String description, String[] interests) {
        checkDates(startDate, endDate);

        University destination = universityService.findByAny(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));
        //Solo por ahora busco tmbn x username
        User user = userService.findByEmail(email).orElseGet(() -> userService.findByUsername(username).orElseGet(()-> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePicture, interests)));
        /*
        if (journeyDao.findOverlappingJourney(user.getId(), startDate, endDate).isPresent()) {
            throw new RuntimeException("There's already a journey registered in this time period");
        }
        */
        if(journeyDao.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("User already has a journey");
        }

        return journeyDao.create(user, destination, startDate, endDate, description); // FIXME
    }


    @Override
    public void replyToJourney(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture, long journeyId, String message) {
        Journey journey = journeyDao.findById(journeyId).orElseThrow(() -> new RuntimeException("Journey not found"));
        //parche temporal buscar por username
        long userId = userService.findByEmail(email).orElseGet(() -> userService.findByUsername(username).orElseGet(()-> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePicture, new String[]{}))).getId();
        journeyResponseDao.create(userId, journeyId, message);

        User user = journey.getUser();
        //@TODO cambiar el locale
        emailService.answerJourneyMail( email, user.getEmail() , firstname, lastname,username, career, originUniversity, message , new Locale("es"), profilePicture);
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

    @Override
    public Boolean userHasJourney(String email) {
        Optional<User> maybeUser = userService.findByEmail(email);
        if(maybeUser.isEmpty()){
            return false;
        }
        return journeyDao.findByUserId(maybeUser.get().getId()).isPresent();
    }

}

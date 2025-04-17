package ar.edu.itba.paw.services;

//import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
//import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class JourneyServiceImpl implements JourneyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyServiceImpl.class);

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final JourneyResponseDao journeyResponseDao;
    //private final CityService cityService;
    //private final InterestService interestService;
    private final ImageService imageService;
    private final InterestService interestService;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, ImageService imageService,
                              UniversityService universityService, JourneyResponseDao journeyResponseDao, EmailService emailService, CityService cityService, InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
        //this.cityService = cityService;
        this.interestService = interestService;
        this.imageService = imageService;
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
    public Journey createJourney(String email, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        LOGGER.debug("Creating journey for {}", email);
        checkDates(startDate, endDate);

        LOGGER.debug("Looking for university {}", destinationUniversity);
        University destination = universityService.findByAny(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));

        LOGGER.debug("Looking for user {}", email);
        //Solo por ahora busco tmbn x username
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        if (journeyDao.findOverlappingJourney(user.getId(), startDate, endDate).isPresent()) {
            LOGGER.info("User has an overlapping journey");
            throw new RuntimeException("There's already a journey registered in this time period");
        }
        //if(journeyDao.findByUserId(user.getId()).isPresent()) {
        //    throw new RuntimeException("User already has a journey");
        //}

        LOGGER.info("Journey data is valid, commiting new event to persistance");
        return journeyDao.create(user, destination, startDate, endDate, description); // FIXME
    }


    @Override
    public void replyToJourney(String email, long journeyId, String message) {
        LOGGER.debug("Replying to journey {}", journeyId);

        LOGGER.debug("Looking for journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId).orElseThrow(() -> new RuntimeException("Journey not found"));

        LOGGER.debug("Looking for user {}", email);
        //parche temporal buscar por username
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.info("Journey reply is valid, commiting new reply to persistance");
        journeyResponseDao.create(user.getId(), journeyId, message);


        LOGGER.info("Updating interest score");
        List<Interest> interests = interestService.findByUserId(journey.getUser().getId());
        interestService.updateScoreByInterests(interests, user.getId());

        LOGGER.info("Sending email notification to journey owner");
        User receiver = journey.getUser();
        //@TODO cambiar el locale
        emailService.answerJourneyMail( email, receiver.getEmail() , user.getFirstname(), user.getLastname(),
                user.getUsername(), user.getCareer().getName(),
                user.getUniversity().getName(), message , new Locale("es"),
                imageService.getImage(user.getProfilePictureId()).orElseThrow(()->new RuntimeException("Image not found")).getData());
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

    @Override
    public List<Journey> getRecommendedJourneys(String email) {
        return journeyDao.getRecommendedJourneys(email);
    }

}

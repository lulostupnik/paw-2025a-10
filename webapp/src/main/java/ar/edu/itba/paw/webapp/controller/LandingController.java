package ar.edu.itba.paw.webapp.controller;

import java.util.Collections;
import java.util.List;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;

@Controller
public class LandingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LandingController.class);

    private final EventService eventService;
    private final JourneyService journeyService;

    @Autowired
    public LandingController(final EventService eventService, final JourneyService journeyService) {
        this.journeyService = journeyService;
        this.eventService = eventService;
    }

    @RequestMapping("/")
    public ModelAndView landing(@ModelAttribute("user") User user) {
        LOGGER.debug("Loading landing page");
        ModelAndView mav = new ModelAndView("index");

        List<Event> recommendedEvents = eventService.getTopEvents(3);

        mav.addObject("recommendedEvents", recommendedEvents);
        List<Event> eventsAttended = Collections.emptyList();

        if (user != null ) {
            eventsAttended = eventService.getUserAttendingEvents(user.getEmail());
        }

        mav.addObject("eventsAttended", eventsAttended);
        return mav;
    }

    private void populateHomePage(ModelAndView mav, String username) {
        List<UserEvent> events = eventService.getRecommendedEvents(username, 8);
        LOGGER.debug("Events: {}", events);
        mav.addObject("events", events);

        List<Journey> journeys = journeyService.getRecommendedJourneys(username, 4);
        LOGGER.debug("Journeys: {}", journeys);
        mav.addObject("journeys", journeys);

        Boolean hasJourney = journeyService.userHasJourney(username);
        LOGGER.debug("User has journey {}", hasJourney);
        mav.addObject("hasJourney", hasJourney);
    }

    @RequestMapping("/explore")
    public ModelAndView explore(@ModelAttribute("user") User user) {
        LOGGER.debug("Getting dashboard page...");

        ModelAndView mav = new ModelAndView("home");
        populateHomePage(mav, user.getEmail());

        return mav;
    }

}

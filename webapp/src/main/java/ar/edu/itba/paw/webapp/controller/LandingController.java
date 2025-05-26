package ar.edu.itba.paw.webapp.controller;

import java.util.List;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EventService;

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

    @GetMapping("/")
    public ModelAndView landing(@ModelAttribute("user") User user) {
        if (user != null) {
            return new ModelAndView("redirect:/explore");
        }

        ModelAndView mav = new ModelAndView("index");
        List<Event> recommendedEvents = eventService.findTopEvents(3);
        mav.addObject("recommendedEvents", recommendedEvents);
        return mav;
    }

    private void populateHomePage(ModelAndView mav, User user) {
        List<Event> events = eventService.findRecommendedEvents(user.getId(), 8);
        mav.addObject("events", events);

        List<Journey> journeys = journeyService.findRecommendedJourneys(user.getEmail(), 4);
        mav.addObject("journeys", journeys);

        Boolean hasJourney = journeyService.existsByUser(user);
        mav.addObject("hasJourney", hasJourney);
    }

    @GetMapping("/explore")
    public ModelAndView explore(
//            @ModelAttribute(value = "validationSuccess") final boolean validationSuccess,
            @ModelAttribute("user") User user) {

        ModelAndView mav = new ModelAndView("home");
//        mav.addObject("validationSuccess", validationSuccess);
        populateHomePage(mav, user);

        return mav;
    }

}

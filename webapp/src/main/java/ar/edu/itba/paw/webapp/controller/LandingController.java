package ar.edu.itba.paw.webapp.controller;

import java.util.Collections;
import java.util.List;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Journey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
    public ModelAndView landing() {
        LOGGER.debug("Loading landing page");
        ModelAndView mav = new ModelAndView("index");

        List<Event> recommendedEvents = eventService.getTopEvents();
        LOGGER.debug("Found events {}", recommendedEvents);
        mav.addObject("recommendedEvents", recommendedEvents);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Event> eventsAttended = Collections.emptyList();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            eventsAttended = eventService.getUserAttendingEvents(auth.getName());
        }

        mav.addObject("eventsAttended", eventsAttended);
        return mav;
    }

    @RequestMapping("/explore")
    public ModelAndView index() {
        LOGGER.debug("Getting dashboard page...");

        ModelAndView mav = new ModelAndView("home");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for: {}", authentication.getPrincipal());

        List<Event> events = eventService.getRecommendedEvents(authentication.getName());
        LOGGER.debug("Events: {}", events);
        mav.addObject("events", events);
        mav.addObject("eventsAttended", eventService.getUserAttendingEvents(
                SecurityContextHolder.getContext().getAuthentication().getName()));

        List<Journey> journeys = journeyService.getRecommendedJourneys(authentication.getName());
        LOGGER.debug("Journeys: {}", journeys);
        mav.addObject("journeys", journeys);

        return mav;
    }

}

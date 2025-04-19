package ar.edu.itba.paw.webapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;

@Controller
public class LandingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LandingController.class);

    private final EventService eventService;

    @Autowired
    public LandingController(final EventService eventService){
        this.eventService = eventService;
    }

    @RequestMapping("/")
    public ModelAndView landing() {
        LOGGER.debug("Loading landing page");
        ModelAndView mav = new ModelAndView("index");

        List<Event> recommendedEvents = eventService.getTopEvents();
        LOGGER.debug("Found events {}", recommendedEvents);
        mav.addObject("recommendedEvents", recommendedEvents);

        return mav;
    }

}

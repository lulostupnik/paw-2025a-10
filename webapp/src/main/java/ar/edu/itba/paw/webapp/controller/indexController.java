package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Controller
public class indexController {

    private final EventService eventService;
    private final JourneyService journeyService;

    @Autowired
    public indexController(EventService eventService,JourneyService journeyService) {
        this.eventService = eventService;
        this.journeyService = journeyService;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView("index");
        List<Event> events = eventService.getAllEvents();
        mav.addObject("newEvents", events);
        List<Journey> journeys = journeyService.getAllJourneys();
        mav.addObject("newJourneys", journeys);
        return mav;
    }


}

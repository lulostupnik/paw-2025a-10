package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class indexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(indexController.class);

    private final EventService eventService;
    private final JourneyService journeyService;

    @Autowired
    public indexController(EventService eventService,JourneyService journeyService) {
        this.eventService = eventService;
        this.journeyService = journeyService;
    }



}

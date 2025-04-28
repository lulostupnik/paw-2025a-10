package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private final EventService eventService;

    private final UserService userService;

    private final JourneyService journeyService;
    @Autowired
    public AdminController(EventService eventService, UserService userService, JourneyService journeyService) {
        this.eventService = eventService;
        this.userService = userService;
        this.journeyService = journeyService;
    }

    @RequestMapping("/dashboard")
    public ModelAndView dashboard() {
        ModelAndView mav = new ModelAndView("admin/dashboard");
        mav.addObject("events", eventService.getAllEvents());
        mav.addObject("users", userService.getAllUsers());
        mav.addObject("journeys", journeyService.getAllJourneys());
        return mav;
    }

}
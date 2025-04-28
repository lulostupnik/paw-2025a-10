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
    public ModelAndView dashboard(
            @RequestParam(value = "eventPage", defaultValue = "1") int eventPage,
            @RequestParam(value = "userPage", defaultValue = "1") int userPage,
            @RequestParam(value = "journeyPage", defaultValue = "1") int journeyPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        ModelAndView mav = new ModelAndView("admin/dashboard");
        mav.addObject("pagedEvents", eventService.getAllEvents(eventPage, pageSize));
        mav.addObject("pagedUsers", userService.getAllUsers(userPage, pageSize));
        mav.addObject("pagedJourneys", journeyService.getAllJourneys(journeyPage, pageSize));
        return mav;
    }

}
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
            @RequestParam(value = "view", defaultValue = "events") String view,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");

        mav.addObject("currentView", view);
        mav.addObject("pagedUsers", userService.getAllUsers(page, pageSize));
        mav.addObject("pagedJourneys", journeyService.getAllJourneys(page, pageSize));
        mav.addObject("pagedEvents", eventService.getAllEvents(page, pageSize));

        switch (view) {
            case "users":
                mav.addObject("pagedUsers", userService.getAllUsers(page, pageSize));
                break;
            case "journeys":
                mav.addObject("pagedJourneys", journeyService.getAllJourneys(page, pageSize));
                break;
            case "events":
            default:
                mav.addObject("pagedEvents", eventService.getAllEvents(page, pageSize));
                break;
        }

        return mav;
    }

}
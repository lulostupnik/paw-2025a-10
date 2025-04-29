package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/dashboard")
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

    @RequestMapping("/events")
    public ModelAndView dashboardEvents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");

        if (search != null && !search.isEmpty()) {
            mav.addObject("pagedEvents", eventService.searchEvents(search, page, pageSize));
        } else {
            mav.addObject("pagedEvents", eventService.getAllEvents(page, pageSize));
        }

        return mav;
    }

    @RequestMapping("/users")
    public ModelAndView dashboardUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");

        if (search != null && !search.isEmpty()) {
            mav.addObject("pagedUsers", userService.searchUsers(search, page, pageSize));
        } else {
            mav.addObject("pagedUsers", userService.getAllUsers(page, pageSize));
        }

        return mav;
    }

    @RequestMapping("/journeys")
    public ModelAndView dashboardJourneys(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");

        if (search != null && !search.isEmpty()) {
            mav.addObject("pagedJourneys", journeyService.searchJourneys(search, page, pageSize));
        } else {
            mav.addObject("pagedJourneys", journeyService.getAllJourneys(page, pageSize));
        }

        return mav;
    }
}

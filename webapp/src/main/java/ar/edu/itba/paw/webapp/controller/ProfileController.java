package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final JourneyService journeyService;
    private final UserService userService;
    private final EventService eventService;
    private final InterestService interestService;

    @Autowired
    public ProfileController(JourneyService journeyService, UserService userService, EventService eventService, InterestService interestService) {
        this.journeyService = journeyService;
        this.userService = userService;
        this.eventService = eventService;
        this.interestService = interestService;
    }
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ModelAndView getInfo() {

        return new ModelAndView("profile/profile");
    }


    @RequestMapping(value = "/interests", method = RequestMethod.GET)
    public ModelAndView getInterests(
            @ModelAttribute("user") User user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "attendingPage", defaultValue = "1") int attendingPage,
            @RequestParam(value = "size", defaultValue = "4") int size) {

        ModelAndView mav = new ModelAndView("profile/profile");
        mav.addObject("interests",interestService.findAllInterestsByUserId(user.getId(), page, size));
        return mav;
    }


    @RequestMapping(value = "/journeys", method = RequestMethod.GET)
    public ModelAndView getJourneys(
            @ModelAttribute("user") User user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "attendingPage", defaultValue = "1") int attendingPage,
            @RequestParam(value = "size", defaultValue = "4") int size) {

        ModelAndView mav = new ModelAndView("profile/profile");
        mav.addObject("userJourneys", journeyService.getJourneysByUser(user.getEmail())); // FIXME: cambiar y usar Optional<Journey> getJourneyByEmail
        return mav;
    }
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ModelAndView getEvents(
            @ModelAttribute("user") User user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "attendingPage", defaultValue = "1") int attendingPage,
            @RequestParam(value = "size", defaultValue = "4") int size) {

        ModelAndView mav = new ModelAndView("profile/profile");
        mav.addObject("userEvents", eventService.getAllEvents(user.getEmail(), page, size));
        mav.addObject("userAttendingEvents", eventService.getUserAttendingEvents(user.getId(), attendingPage, size));
        mav.addObject("currentPageUserEvents", page);
        mav.addObject("currentPageUserAttending", attendingPage);
        return mav;
    }
}

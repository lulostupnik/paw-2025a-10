package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final JourneyService journeyService;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public ProfileController(JourneyService journeyService, UserService userService, EventService eventService) {
        this.journeyService = journeyService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView getProfile(@ModelAttribute("username") String username) {
        ModelAndView mav = new ModelAndView("profile");

        Optional<User> user = userService.findByEmail(username);

        mav.addObject("user", user);
        mav.addObject("userJourneys", journeyService.getJourneysByUser(username)); // FIXME: cambiar y usar Optional<Journey> getJourneyByEmail
        mav.addObject("userEvents", eventService.getAllEvents(username));
        mav.addObject("userAttendingEvents", eventService.getUserAttendingEvents(username));

        return mav;
    }
//
//    @RequestMapping(value = "/profile/edit", method = RequestMethod.GET)
//    public ModelAndView getEditProfile() {
//        ModelAndView mav = new ModelAndView("profile/edit-profile");
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = authentication.getName();
//        Optional<User> user = userService.findByEmail(userEmail);
//
//        if (user.isPresent()) {
//            mav.addObject("user", user.get());
//        } else {
//            return new ModelAndView("redirect:/");
//        }
//
//        return mav;
//    }
}

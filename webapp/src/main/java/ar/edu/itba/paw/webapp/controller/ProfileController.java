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
import org.springframework.web.bind.annotation.*;
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
    public ModelAndView getProfile(@ModelAttribute("user") User user) {
        ModelAndView mav = new ModelAndView("profile");


        mav.addObject("user", user);
        mav.addObject("userJourneys", journeyService.getJourneysByUser(user.getEmail())); // FIXME: cambiar y usar Optional<Journey> getJourneyByEmail
        mav.addObject("userEvents", eventService.getAllEvents(user.getEmail()));
        mav.addObject("userAttendingEvents", eventService.getUserAttendingEvents(user.getEmail()));

        return mav;
    }

    @RequestMapping(value = "/profile/{id}/block", method = RequestMethod.POST)
    public ModelAndView blockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
//        ModelAndView mav = new ModelAndView("redirect:/profile/" + id);
        userService.blockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            throw new RuntimeException("Referer header is missing");
        }
    }

    @RequestMapping(value = "/profile/{id}/unblock", method = RequestMethod.POST)
    public ModelAndView unblockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
//        ModelAndView mav = new ModelAndView("redirect:/profile/" + id);
        userService.unblockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            throw new RuntimeException("Referer header is missing");
        }
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

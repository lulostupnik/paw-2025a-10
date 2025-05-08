package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.resolver.anotation.PageParamCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final JourneyService journeyService;

    private final EventService eventService;
    private final InterestService interestService;
    private static final String PROFILE = "profile/profile";
    private final UserService userService;

    @Autowired
    public ProfileController(JourneyService journeyService, EventService eventService, InterestService interestService, UserService userService) {
        this.journeyService = journeyService;

        this.eventService = eventService;
        this.interestService = interestService;
        this.userService = userService;
    }
    @GetMapping(value = "/info")
    public ModelAndView getInfo() {

        return new ModelAndView(PROFILE);
    }


    @GetMapping(value = "/interests")
    public ModelAndView getInterests(
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 4) PageParams pageParams) {

        ModelAndView mav = new ModelAndView(PROFILE);
        mav.addObject("interests",interestService.findAllInterestsByUserId(user.getId(), pageParams));
        return mav;
    }


    @GetMapping(value = "/journeys")
    public ModelAndView getJourneys(
            @ModelAttribute("user") User user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "attendingPage", defaultValue = "1") int attendingPage,
            @RequestParam(value = "size", defaultValue = "4") int size) {

        ModelAndView mav = new ModelAndView(PROFILE);
        mav.addObject("userJourneys", journeyService.getJourneysByUser(user.getEmail())); // FIXME: cambiar y usar Optional<Journey> getJourneyByEmail
        return mav;
    }
    @GetMapping(value = "/events")
    public ModelAndView getEvents(
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 6, pageParamName = "attendingPage") PageParams attendingPage,
            @PageParamCustomizer(defaultSize = 6) PageParams pageParam) {

        ModelAndView mav = new ModelAndView(PROFILE);
        mav.addObject("userEvents", eventService.getAllEvents(user.getEmail(), pageParam));
        mav.addObject("userAttendingEvents", eventService.getUserAttendingEvents(user.getId(), attendingPage));
        mav.addObject("currentPageUserEvents", pageParam.getPage());
        mav.addObject("currentPageUserAttending", attendingPage.getPage());
        return mav;
    }
    @GetMapping(value="/changePassword")
    public ModelAndView getChangePassword(@ModelAttribute("updatePasswordForm") UpdatePasswordForm updatePasswordForm) {
        return new ModelAndView("profile/change-password");
    }
    @PostMapping(value="/changePassword")
    public ModelAndView changePassword(@ModelAttribute("updatePasswordForm") UpdatePasswordForm updatePasswordForm,
                                       BindingResult errors,
                                       @ModelAttribute("user") User user) {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/profile/changePassword");
        }
        userService.changePassword(user.getEmail(), updatePasswordForm.getPassword());
        return new ModelAndView("redirect:/profile/info");
    }

}

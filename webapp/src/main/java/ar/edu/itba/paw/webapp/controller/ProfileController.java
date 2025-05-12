package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

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

    private void addUserJourneyToMav(User user, ModelAndView mav){
        Optional<Journey> maybeJourney = journeyService.getJourneyByEmail(user.getEmail());
        maybeJourney.map(journey -> mav.addObject("userJourney", journey)).orElseGet(() -> mav.addObject("userJourney", null));
    }

    @GetMapping(value = "/info")
    public ModelAndView getInfo(
            @ModelAttribute("user") User user
    ) {
        ModelAndView mav = new ModelAndView(PROFILE);
        addUserJourneyToMav(user, mav);
        return mav;
    }


    @GetMapping(value = "/interests")
    public ModelAndView getInterests(
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 4) PageParams pageParams) {

        ModelAndView mav = new ModelAndView(PROFILE);
        addUserJourneyToMav(user, mav);
        mav.addObject("interests",interestService.findInterestsByUserId(user.getId(), pageParams));
        return mav;
    }


    @GetMapping(value = "/events")
    public ModelAndView getEvents(
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 6, pageParamName = "attendingPage") PageParams attendingPage,
            @PageParamCustomizer(defaultSize = 6) PageParams pageParam) {

        ModelAndView mav = new ModelAndView(PROFILE);
        mav.addObject("events", eventService.getAllEvents(user.getEmail(), pageParam));
        mav.addObject("userAttendingEvents", eventService.getUserAttendingEvents(user.getId(), attendingPage));
        mav.addObject("currentPageUserEvents", pageParam.getPage());
        mav.addObject("currentPageUserAttending", attendingPage.getPage());
        addUserJourneyToMav(user, mav);
        return mav;
    }

    @GetMapping(value="/changePassword")
    public ModelAndView getChangePassword(@ModelAttribute("updatePasswordForm") UpdatePasswordForm updatePasswordForm) {
        return new ModelAndView("profile/change-password");
    }
    @PostMapping(value="/changePassword")
    public ModelAndView changePassword(@Valid @ModelAttribute("updatePasswordForm") UpdatePasswordForm updatePasswordForm,
                                       BindingResult errors,
                                       @ModelAttribute("user") User user) {
        if(errors.hasErrors()) {
            return getChangePassword(updatePasswordForm);
        }
        userService.updatePassword(user.getId(), updatePasswordForm.getPassword());
        return new ModelAndView("redirect:/profile/info");
    }


}

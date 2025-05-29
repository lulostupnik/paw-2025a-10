package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
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

    @GetMapping(value = "{id}/info")
    public ModelAndView getInfo(
            @PathVariable long id,
            @ModelAttribute("user") User user
    ) {
        User profileUser = userService.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        ModelAndView mav = new ModelAndView(PROFILE);
        addUserJourneyToMav(profileUser, mav);
        mav.addObject("isInfoTab", true);
        mav.addObject("isMine", user.getId().equals(id));
        mav.addObject("profileUser", profileUser);
        LOGGER.debug("Average rating for created events: {}", userService.findAverageRatingForCreatedEvents(user.getId()));
        LOGGER.debug("Average rating for attended events: {}", userService.findAverageRatingForAttendedEvents(user.getId()));
        mav.addObject("averageCreatedEventsRating", userService.findAverageRatingForCreatedEvents(user.getId()));
        mav.addObject("averageAttendingEventsRating", userService.findAverageRatingForAttendedEvents(user.getId()));
        mav.addObject("totalCreatedEventsWithRatings", 3);
        mav.addObject("totalAttendedEventsRated", 7);
        return mav;
    }


    @GetMapping(value = "{id}/interests")
    public ModelAndView getInterests(
            @PathVariable long id,
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 4) PageParams pageParams) {
        User profileUser = userService.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        ModelAndView mav = new ModelAndView(PROFILE);
        addUserJourneyToMav(profileUser, mav);
        mav.addObject("isMine", user.getId().equals(id));
        mav.addObject("isInterestsTab", true);
        mav.addObject("interests",interestService.findInterestsByUser(profileUser, pageParams));
        mav.addObject("profileUser", profileUser);
        return mav;
    }


    @GetMapping(value = "{id}/events")
    public ModelAndView getEvents(
            @PathVariable long id,
            @ModelAttribute("user") User user,
            @PageParamCustomizer(defaultSize = 6, pageParamName = "attendingPage") PageParams attendingPage,
            @PageParamCustomizer(defaultSize = 6) PageParams pageParam,
            @PageParamCustomizer(defaultSize = 6, pageParamName = "finishedPage") PageParams finishedPage) {
        User profileUser = userService.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));

        ModelAndView mav = new ModelAndView(PROFILE);
        mav.addObject("events", eventService.findEvents(profileUser.getEmail(), pageParam));
        mav.addObject("isMine", user.getId().equals(id));
        mav.addObject("isEventTab", true);
        mav.addObject("userAttendingEvents", eventService.findUpcomingEventsByAttendee(profileUser.getId(), attendingPage));
        mav.addObject("finishedEvents", eventService.findFinishedEventsByAttendee(profileUser.getId(), finishedPage));
        mav.addObject("currentPageUserEvents", pageParam.getPage());
        mav.addObject("currentPageUserAttending", attendingPage.getPage());
        mav.addObject("currentPageUserFinished", finishedPage.getPage());
        addUserJourneyToMav(user, mav);
        mav.addObject("profileUser", profileUser);
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

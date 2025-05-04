package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
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
    private final UniversityService universityService;
    private final InterestService interestService;
    private final CityService cityService;
    private final CareerService careerService;

    @Autowired
    public AdminController(EventService eventService, UserService userService, JourneyService journeyService, UniversityService universityService, InterestService interestService, CityService cityService, CareerService careerService) {
        this.eventService = eventService;
        this.userService = userService;
        this.journeyService = journeyService;
        this.universityService = universityService;
        this.interestService = interestService;
        this.cityService = cityService;
        this.careerService = careerService;
    }

    @RequestMapping("/events")
    public ModelAndView dashboardEvents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedEvents", eventService.getAllEventsSearch(search, page, pageSize));

        return mav;
    }

    @RequestMapping("/users")
    public ModelAndView dashboardUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedUsers", userService.getAllUsers(search, page, pageSize));

        return mav;
    }

    @RequestMapping("/journeys")
    public ModelAndView dashboardJourneys(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedJourneys", journeyService.getAllJourneys(search,page,pageSize));


        return mav;
    }

    @RequestMapping("/careers")
    public ModelAndView dashboardCareers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedCareers", careerService.getAllCareers(search,page, pageSize));

        return mav;
    }

    @RequestMapping("/universities")
    public ModelAndView dashboardUniversities(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedUniversities", universityService.getAllUniversities(search, page, pageSize));

        return mav;
    }

    @RequestMapping("/interests")
    public ModelAndView dashboardInterests(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedInterests", interestService.getAllInterests(search, page, pageSize));

        return mav;
    }

    @RequestMapping("/cities")
    public ModelAndView dashboardCities(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView("/admin/dashboard");
        mav.addObject("pagedCities", cityService.getAllCities(search, page, pageSize));
        return mav;
    }

}


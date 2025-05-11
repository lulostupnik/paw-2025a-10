package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
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
    private static final String ADMIN_DASHBOARD = "/admin/dashboard";

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
            @PageParamCustomizer(defaultPage = -1, sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedEvents", eventService.getAllEventsSearch(search, pageParams));
        return mav;
    }

    @RequestMapping("/users")
    public ModelAndView dashboardUsers(
            @PageParamCustomizer(sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedUsers", userService.getAllUsers(search, pageParams));

        return mav;
    }

    @RequestMapping("/journeys")
    public ModelAndView dashboardJourneys(
            @PageParamCustomizer(sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedJourneys", journeyService.getAllJourneys(search,pageParams));


        return mav;
    }

    @RequestMapping("/careers")
    public ModelAndView dashboardCareers(
         @PageParamCustomizer(sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedCareers", careerService.getAllCareers(search,pageParams));

        return mav;
    }

    @RequestMapping("/universities")
    public ModelAndView dashboardUniversities(
         @PageParamCustomizer(sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedUniversities", universityService.getAllUniversities(search, pageParams));

        return mav;
    }

    @RequestMapping("/interests")
    public ModelAndView dashboardInterests(
         @PageParamCustomizer(sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedInterests", interestService.getAllInterests(search, pageParams));

        return mav;
    }

    @RequestMapping("/cities")
    public ModelAndView dashboardCities(
         @PageParamCustomizer( sizeParamName = "pageSize") PageParams  pageParams,
            @RequestParam(value = "search", required = false) String search) {

        ModelAndView mav = new ModelAndView(ADMIN_DASHBOARD);
        mav.addObject("pagedCities", cityService.getAllCities(search, pageParams));
        return mav;
    }

}


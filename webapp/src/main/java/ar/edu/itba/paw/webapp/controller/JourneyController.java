package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;
import ar.edu.itba.paw.webapp.form.ReplyJourneyForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.webapp.form.CreateJourneyForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/journeys")
public class JourneyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyController.class);

    private final JourneyService js;
    private final CityService cityService;
    private final UniversityService universityService;
    private final CareerService carreerService;
    private final InterestService interestService;
    private final JourneyService journeyService;

    @Autowired
    public JourneyController(final JourneyService js, CityService cityService, UniversityService universityService, CareerService carreerService, InterestService interestService, JourneyService journeyService){
        this.js = js;
        this.cityService = cityService;
        this.universityService = universityService;
        this.carreerService = carreerService;
        this.interestService = interestService;
        this.journeyService = journeyService;
    }

    @RequestMapping
    public ModelAndView getJourneys(        @RequestParam(required = false) String destination,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                            @RequestParam(required = false) String interest) {
        LOGGER.debug("Getting journeys with filters: {destination: \"{}\", startDate: \"{}\", endDate: \"{}\", interest: \"{}\"}", destination, startDate, endDate, interest);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for user {}", authentication);

        final ModelAndView mav = new ModelAndView("journeys/list");
        List<Journey> journeys = js.getFilteredJourneys(destination, startDate, endDate, interest);
        LOGGER.debug("Found journeys {}", journeys);

        FilterJourneyForm filterJourneyForm = new FilterJourneyForm();
        filterJourneyForm.setDestination(destination);
        filterJourneyForm.setStartDate(startDate);
        filterJourneyForm.setEndDate(endDate);
        filterJourneyForm.setInterests(interest);
        LOGGER.debug("Filter form created: {}", filterJourneyForm);

        List<City> cities = cityService.getAllCities();
        LOGGER.debug("Cities: {}", cities);

        List<Interest> interests = interestService.findAll();
        LOGGER.debug("Interests: {}", interests);

        Boolean hasJourney = js.userHasJourney(authentication.getName());
        LOGGER.debug("User has journey {}", hasJourney);

        mav.addObject("cities", cities);
        mav.addObject("interests", interests);
        mav.addObject("filterJourneyForm", filterJourneyForm);
        mav.addObject("journeys", journeys);
        mav.addObject("hasJourney", hasJourney);
        return mav;
    }

    @RequestMapping(value = "/create", method = POST)
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf, final BindingResult errors) {
        LOGGER.debug("Creating journey from form: {}", jf);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createJourneyForm(jf);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for: {}", authentication.getPrincipal());

        final Journey journey = js.createJourney(authentication.getName(), // Devuelve el username
                jf.getDestinationUniversity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());

        LOGGER.info("Successfully created journey {}", journey);
        ReplyJourneyForm rjf = new ReplyJourneyForm();  //@TODO tiene sentido??
        return getJourney(journey.getId(), rjf);
    }

    @RequestMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getName() == null) {
            LOGGER.debug("User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }
        if(journeyService.userHasJourney(authentication.getName())) {
            LOGGER.debug("User already has a journey, redirecting to journey list");
            return new ModelAndView("redirect:/journeys");
        }
        final ModelAndView mav = new ModelAndView("journeys/create");

        List<University> universities = universityService.getAllUniversities();
        LOGGER.debug("Universities: {}", universities);
        mav.addObject("universities", universities);

        return mav;
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id, @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf) {
        LOGGER.debug("Getting info for journey {}", id);

        Optional<Journey> journey = js.getJourneyById(id);

        if(journey.isEmpty()){
            LOGGER.debug("Journey {} not found", id);
            return new ModelAndView("journeys/not_found");
        }

        List<JourneyResponse> journeyResponses = js.getJourneyResponses(journey.get().getId());

        final ModelAndView mav = new ModelAndView("journeys/detail");
        LOGGER.debug("Journey found: {}", journey.get());
        mav.addObject("journey", journey.get());
        mav.addObject("journeyResponses", journeyResponses);
        mav.addObject("replyJourneyForm", rjf);
        return mav;
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf, final BindingResult errors) {
        LOGGER.debug("Replying to journey {} from form {}", id, rjf);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
//            return replyToJourneyForm(id, rjf);
            return getJourney(id, rjf);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for: {}", authentication.getPrincipal());

        js.replyToJourney(authentication.getName(), id, rjf.getMessage());

        return new ModelAndView("redirect:/journeys/" + id);
    }

//    @RequestMapping(value = "/{id}/reply")
//    public ModelAndView replyToJourneyForm(@PathVariable int id, @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf) {
//        LOGGER.debug("Getting journey reply form for journey {}", id);
//
//        ModelAndView mav = new ModelAndView("journeys/reply");
//        Optional<Journey> journey = js.getJourneyById(id);
//
//        if(journey.isEmpty()){
//            LOGGER.debug("Journey {} not found, redirecting to journey list", id);
//            return getJourneys(null,null, null, null);
//        }
//        LOGGER.debug("Journey found: {}", journey.get());
//
//        List<University> universities = universityService.getAllUniversities();
//        LOGGER.debug("Universities: {}", universities);
//
//        List<Career> careers = carreerService.findAll();
//        LOGGER.debug("Careers: {}", careers);
//
//        mav.addObject("careers", careers);
//        mav.addObject("universities", universities);
//        mav.addObject("journey", journey.get());
//        mav.addObject("replyJourneyForm", rjf);
//        return mav;
//    }

    @RequestMapping(value ="/filter", method = POST)
    public ModelAndView filterJourney(@ModelAttribute("filterJourneyForm") final FilterJourneyForm form, final BindingResult errors) {
        LOGGER.debug("Filtering journeys from form {}", form);

        if(errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createJourneyForm(form);
        }

        final ModelAndView mav = new ModelAndView("journeys/list");
        List<Journey> journeys = js.getFilteredJourneys(form.getDestination(), form.getStartDate(), form.getEndDate(), form.getInterest());
        LOGGER.debug("Journeys found: {}", journeys);

        mav.addObject("journeys", journeys);
        return new ModelAndView("journeys/list");
    }

    @RequestMapping(value ="/filter")
    public ModelAndView createJourneyForm(@ModelAttribute("filterJourneyForm") final FilterJourneyForm form) {
        LOGGER.debug("Getting journey filter form");
        return new ModelAndView("journeys/list");
    }


}

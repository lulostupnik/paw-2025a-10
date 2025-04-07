package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;
import ar.edu.itba.paw.webapp.form.ReplyJourneyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/journeys")
public class JourneyController {

    private final JourneyService js;
    private final CityService cityService;
    private final UniversityService universityService;
    private final CareerService carreerService;

    @Autowired
    public JourneyController(final JourneyService js, CityService cityService, UniversityService universityService, CareerService carreerService){
        this.js = js;
        this.cityService = cityService;
        this.universityService = universityService;
        this.carreerService = carreerService;
    }

    @RequestMapping
    public ModelAndView getJourneys(        @RequestParam(required = false) String destination,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                            @RequestParam(required = false) String interest) {
        final ModelAndView mav = new ModelAndView("journeys/list");
        List<Journey> journeys = js.getFilteredJourneys(destination, startDate, endDate, interest);
        FilterJourneyForm filterJourneyForm = new FilterJourneyForm();
        filterJourneyForm.setDestination(destination);
        filterJourneyForm.setStartDate(startDate);
        filterJourneyForm.setEndDate(endDate);
        filterJourneyForm.setInterests(interest);
        mav.addObject("filterJourneyForm", filterJourneyForm);
        mav.addObject("journeys", journeys);
        return mav;
    }
    @RequestMapping(value = "/create", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf, final BindingResult errors) {
        if (errors.hasErrors()) {
            return createJourneyForm(jf);
        }

        byte[] profilePicture = null;
        try {
            profilePicture = jf.getProfilePicture().getBytes();
        } catch (Exception e) {
            // FIXME
        }

        //FIXME: Add fields for user creation just in case it does not exist. This will be removed after 1st sprint when we implement authorization
        final Journey journey = js.createJourney(jf.getEmail(), jf.getUsername(), jf.getFirstName(),
                jf.getLastName(), jf.getDestinationUniversity(), jf.getCareer(), profilePicture, jf.getDestinationUniversity(), jf.getDestinationCity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());
        
        return getJourney(journey.getId());
    }
    @RequestMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf) {
        final ModelAndView mav = new ModelAndView("journeys/create");
        List<City> cities = cityService.getAllCities();
        List<University> universities = universityService.getAllUniversities();
        List<Career> careers = carreerService.findAll();
        mav.addObject("careers", careers);
        mav.addObject("universities", universities);
        mav.addObject("cities", cities);
        return mav;
    }
    @RequestMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id) {
        Optional<Journey> journey = js.getJourneyById(id);
        if(journey.isEmpty()){
            return new ModelAndView("journeys/not_found");
        }
        final ModelAndView mav = new ModelAndView("journeys/detail");
        mav.addObject("journey", journey.get());
        return mav;
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf, final BindingResult errors) {
        if (errors.hasErrors()) {
            return replyToJourneyForm(id, rjf);
        }

        //FIXME: Add fields for user creation just in case it does not exist. This will be removed after 1st sprint when we implement authorization
        js.replyToJourney(rjf.getEmail(), rjf.getUsername(), rjf.getFirstName(),
                rjf.getLastName(), rjf.getOriginUniversity(), rjf.getCareer(), 1, id, rjf.getMessage() );

        return getJourneys(null,null, null, null);
    }

    @RequestMapping(value = "/{id}/reply")
    public ModelAndView replyToJourneyForm(@PathVariable int id, @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf) {
        ModelAndView mav = new ModelAndView("journeys/reply");
        Optional<Journey> journey = js.getJourneyById(id);
        if(journey.isEmpty()){
           return getJourneys(null,null, null, null);
        }
        mav.addObject("journey", journey.get());
        mav.addObject("replyJourneyForm", rjf);
        return mav;
    }

    @RequestMapping(value ="/filter", method = POST)
    public ModelAndView filterJourney(@ModelAttribute("filterJourneyForm") final FilterJourneyForm form, final BindingResult errors) {
        if(errors.hasErrors()) {
            return createJourneyForm(form);
        }
        final ModelAndView mav = new ModelAndView("journeys/list");
        List<Journey> journeys = js.getFilteredJourneys(form.getDestination(), form.getStartDate(), form.getEndDate(), form.getInterest());
        mav.addObject("journeys", journeys);
        return new ModelAndView("journeys/list");
    }

    @RequestMapping(value ="/filter")
    public ModelAndView createJourneyForm(@ModelAttribute("filterJourneyForm") final FilterJourneyForm form) {
        return new ModelAndView("journeys/list");
    }


}

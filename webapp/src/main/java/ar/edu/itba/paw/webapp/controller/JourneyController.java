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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final InterestService interestService;
    private final JourneyService journeyService;

    @Autowired
    public JourneyController(final JourneyService js, CityService cityService, UniversityService universityService, InterestService interestService, JourneyService journeyService){
        this.js = js;
        this.cityService = cityService;
        this.universityService = universityService;
        this.interestService = interestService;
        this.journeyService = journeyService;
    }

    @RequestMapping
    public ModelAndView getJourneys(@Valid @ModelAttribute FilterJourneyForm fjf, final BindingResult errors,
                                    @RequestParam(value = "username", required = false) String username) {
        LOGGER.debug("Getting journeys with filters: {destination: \"{}\", startDate: \"{}\", endDate: \"{}\", interest: \"{}\"}",fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
        List<Journey> journeys;
        boolean hasJourney = false;

        final ModelAndView mav = new ModelAndView("journeys/list");
        if(username != null) {
            hasJourney = js.userHasJourney(username);
            LOGGER.debug("User has journey {}", hasJourney);
            journeys = js.getFilteredJourneys(username, fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
        } else{
            journeys = js.getFilteredJourneys(fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
            LOGGER.debug("Found journeys {}", journeys);
        }

        mav.addObject("journeys", journeys);
        mav.addObject("hasJourney", hasJourney);

       populateDropdownAttributes(mav);

        return mav;
    }

    private void populateDropdownAttributes(ModelAndView mav) {
        List<City> cities = cityService.getAllCities();
        LOGGER.debug("Cities: {}", cities);
        mav.addObject("cities", cities);

        List<Interest> interests = interestService.findAll();
        LOGGER.debug("Interests: {}", interests);
        mav.addObject("interests", interests);
    }
    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteJourney(@PathVariable long id) {
        LOGGER.debug("Deleting journey {}", id);
        js.deleteJourney(id);
        return new ModelAndView("redirect:/journeys");
    }

    @RequestMapping(value = "/create", method = POST)
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf,
                                      final BindingResult errors, @ModelAttribute("username") String username) {
        LOGGER.debug("Creating journey from form: {}", jf);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createJourneyForm(jf, username);
        }

        final Journey journey = js.createJourney(username, // Devuelve el username
                jf.getDestinationUniversity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());

        LOGGER.info("Successfully created journey {}", journey);
        return new ModelAndView("redirect:/journeys/" + journey.getId());
    }

    @RequestMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf, @ModelAttribute("username") String username) {

        if(journeyService.userHasJourney(username)) {
            LOGGER.debug("User already has a journey, redirecting to journey list");
            return new ModelAndView("redirect:/journeys");
        }

        return new ModelAndView("journeys/create")
                .addObject("universities",  universityService.getAllUniversities());
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id,@Valid @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf,
                                   BindingResult errors) {
        LOGGER.debug("Getting info for journey {}", id);

        Optional<Journey> journey = js.getJourneyById(id);

        if(journey.isEmpty()){
            LOGGER.debug("Journey {} not found", id);
            return new ModelAndView("journeys/not_found");
        }
        List<JourneyResponse> journeyResponses = js.getJourneyResponses(journey.get().getId());

        final ModelAndView mav = new ModelAndView("journeys/detail");
        mav.addObject("journey", journey.get());
        mav.addObject("journeyResponses", journeyResponses);
        return mav;
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf,
                                       final BindingResult errors, final RedirectAttributes redirectAttributes,
                                       @ModelAttribute("username") String username) {

        LOGGER.debug("Replying to journey {} from form {}", id, rjf);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("replyJourneyForm", rjf);
            return new ModelAndView("redirect:/journeys/" + id);
        }
        js.replyToJourney(username, id, rjf.getMessage());

        return new ModelAndView("redirect:/journeys/" + id);
    }
}

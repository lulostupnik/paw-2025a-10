package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
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
    public ModelAndView getJourneys(@Valid @ModelAttribute("filterJourneyForm") FilterJourneyForm fjf, final BindingResult errors,
                                    @ModelAttribute("user") User user) {
        LOGGER.debug("Getting journeys with filters: {destination: \"{}\", startDate: \"{}\", endDate: \"{}\", interest: \"{}\"}",fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
        List<Journey> journeys;
        boolean hasJourney = false;

        final ModelAndView mav = new ModelAndView("journeys/list");
        if(user != null) {
            hasJourney = js.userHasJourney(user.getEmail());
            LOGGER.debug("User has journey {}", hasJourney);
            journeys = js.getFilteredJourneys(user.getEmail(), fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
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
    public ModelAndView deleteJourney(@PathVariable long id, @Valid @ModelAttribute("deleteForm") final ReplyForm form,
                                      final BindingResult errors, final RedirectAttributes redirectAttributes) {
        LOGGER.debug("Deleting journey {}", id);
        if(errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("deleteErrors", errors);
            redirectAttributes.addFlashAttribute("deleteForm", form);
            return new ModelAndView("redirect:/journeys/" + id);
        }
        js.delete(id, form.getMessage());
        return new ModelAndView("redirect:/journeys");
    }

    @RequestMapping(value = "/create", method = POST)
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf,
                                      final BindingResult errors, @ModelAttribute("user") User user) {
        LOGGER.debug("Creating journey from form: {}", jf);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createJourneyForm(jf, user);
        }

        final Journey journey = js.createJourney(user, // Devuelve el username
                jf.getDestinationUniversity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());

        LOGGER.info("Successfully created journey {}", journey);
        return new ModelAndView("redirect:/journeys/" + journey.getId());
    }

    @RequestMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf, @ModelAttribute("user") User user) {

        if (journeyService.userHasJourney(user.getEmail())) {
            LOGGER.debug("User already has a journey, redirecting to journey list");
            return new ModelAndView("redirect:/journeys");
        }

        return new ModelAndView("journeys/create")
                .addObject("universities", universityService.getAllUniversities());
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id,
                                      @ModelAttribute("user") User user,
                                   @Valid @ModelAttribute("replyJourneyForm") final ReplyForm rjf,
                                   BindingResult errors,
                                   @Valid @ModelAttribute("deleteForm") final ReplyForm deleteForm,
                                   final BindingResult deleteErrors,
                                   @Valid @ModelAttribute("deleteReplyForm") final ReplyForm deleteReplyForm,
                                   final BindingResult deleteReplyErrors,
                                   @RequestParam(value = "replyId", required = false) Long replyId){

        LOGGER.debug("Getting info for journey {}", id);

        Optional<Journey> journey = js.getJourneyById(id);

        if(journey.isEmpty()){ //cambiar con exception controllerAdvice
            LOGGER.debug("Journey {} not found", id);
            return new ModelAndView("journeys/not_found");
        }
        List<JourneyResponse> journeyResponses = js.getJourneyResponses(journey.get().getId());

        final ModelAndView mav = new ModelAndView("journeys/detail");
        mav.addObject("journey", journey.get());
        mav.addObject("journeyResponses", journeyResponses);
        mav.addObject("isOwner", js.isJourneyOwnedByUser(user.getEmail(),journey.get().getId()));

        // Check if there are errors in the delete forms
        if (deleteErrors.hasErrors()) {
            // Add attributes to indicate there was an error in the journey delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "journey");
            mav.addObject("deleteFormId", "delete-journey-form");
        } else if (deleteReplyErrors.hasErrors()) {

            // Add attributes to indicate there was an error in a journey response delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "journeyResponse");
            mav.addObject("deleteFormId", "delete-journey-response-form-" + replyId);
        }
        return mav;
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyForm rjf,
                                       final BindingResult errors, final RedirectAttributes redirectAttributes,
                                       @ModelAttribute("user") User user) {

        LOGGER.debug("Replying to journey {} from form {}", id, rjf);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("replyJourneyForm", rjf);
            return new ModelAndView("redirect:/journeys/" + id);
        }
        js.replyToJourney(user.getEmail(), id, rjf.getMessage());

        return new ModelAndView("redirect:/journeys/" + id);
    }


    @RequestMapping(value = "/{id}/update", method = GET)
    public ModelAndView showUpdateJourneyForm(@PathVariable("id") long journeyId,
                                              @ModelAttribute("username") String username,
                                              @ModelAttribute("createJourneyForm") CreateJourneyForm form,
                                              BindingResult errors) {
        LOGGER.debug("User {} requested to update journey {}", username, journeyId);

        Journey journey = journeyService.getJourneyById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey {} not found", journeyId);
                    return new IllegalArgumentException("Journey not found");
                });

        if(!errors.hasErrors()) {
            form.setStartDate(journey.getStartDate());
            form.setEndDate(journey.getEndDate());
            form.setDestinationUniversity(journey.getDestinationUniversity().getName());
            form.setDescription(journey.getDescription());
        }

        ModelAndView mav = new ModelAndView("journeys/edit");
        mav.addObject("universities", universityService.getAllUniversities());
        mav.addObject("journeyId", journeyId);
        return mav;
    }

    @RequestMapping(value = "/{id}/update", method = POST)
    public ModelAndView updateJourney(@PathVariable("id") long journeyId,
                                      @ModelAttribute("username") String username,
                                      @Valid @ModelAttribute("createJourneyForm") CreateJourneyForm form,
                                      BindingResult errors) {

        LOGGER.debug("User {} submitted update for journey {}", username, journeyId);

        if (errors.hasErrors()) {
            return showUpdateJourneyForm(journeyId, username, form, errors);
        }

        js.editJourney(journeyId,
                form.getDestinationUniversity(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription());

        LOGGER.info("Journey {} updated successfully", journeyId);
        return new ModelAndView("redirect:/journeys/" + journeyId);
    }





}

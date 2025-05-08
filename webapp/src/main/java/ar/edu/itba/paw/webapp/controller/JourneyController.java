package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;

import ar.edu.itba.paw.webapp.resolver.anotation.PageParamCustomizer;
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

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/journeys")
public class JourneyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyController.class);

    private final JourneyService js;
    private final CityService cityService;
    private final UniversityService universityService;
    private final InterestService interestService;
    private final JourneyResponseService journeyResponseService;
    private static final String REDIRECT_JOURNEY = "redirect:/journeys/";

    @Autowired
    public JourneyController(final JourneyService js, CityService cityService, UniversityService universityService, InterestService interestService, JourneyResponseService journeyResponseService) {
        this.js = js;
        this.cityService = cityService;
        this.universityService = universityService;
        this.interestService = interestService;
        this.journeyResponseService = journeyResponseService;
    }


    @RequestMapping
    public ModelAndView getJourneys(@Valid @ModelAttribute("filterJourneyForm") FilterJourneyForm fjf, final BindingResult errors,
                                    @ModelAttribute("user") User user,
                                    @PageParamCustomizer(defaultSize = 8) PageParams  pageParams,
                                    @RequestParam(value = "search", required = false) String search,
                                    @RequestParam(value = "sort", required = false) String sortBy,
                                    @RequestParam(value = "direction", required = false) String direction) {

        LOGGER.debug("Getting journeys with filters: {destination: \"{}\", startDate: \"{}\", endDate: \"{}\", interest: \"{}\"}",fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests());
        final ModelAndView mav = new ModelAndView("journeys/list");
        mav.addObject("journeys", js.getAllJourneys(search, user, sortBy,direction,
                fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests(), pageParams));
        mav.addObject("hasJourney", user != null && js.userHasJourney(user));
        mav.addObject("pageSize", pageParams.getSize());
        mav.addObject("currentPage", pageParams.getPage());

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
            return new ModelAndView(REDIRECT_JOURNEY + id);
        }
        js.delete(id, form.getMessage());
        return new ModelAndView("redirect:/journeys");
    }

    @PostMapping(value = "/create")
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
        return new ModelAndView(REDIRECT_JOURNEY + journey.getId());
    }

    @RequestMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf, @ModelAttribute("user") User user) {

        if (js.userHasJourney(user)) {
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
                                   @RequestParam(value = "replyId", required = false) Long replyId,
                                   @PageParamCustomizer(defaultSize = 5) PageParams  repliesPage,
                                   @PageParamCustomizer(defaultSize = 5, pageParamName = "interestsPage", sizeParamName = "interestsSize") PageParams interestsPage) {

        LOGGER.debug("Getting info for journey {}", id);

        Journey journey = js.getJourneyById(id).orElseThrow(()-> new JourneyNotFoundException("Journey not found"));

        Page<JourneyResponse> journeyResponses = journeyResponseService.listAllFromJourney(journey.getId(), repliesPage);

        final ModelAndView mav = new ModelAndView("journeys/detail");
        mav.addObject("journey", journey);
        mav.addObject("journeyResponsesPage", journeyResponses);
        mav.addObject("commentsCount", journeyResponseService.getCount(journey.getId()));

        if(user != null){
            mav.addObject("isOwner", js.isJourneyOwnedByUser(user.getEmail(),journey.getId()));
        }else{
            mav.addObject("isOwner", false);
        }

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
        mav.addObject("interestPage", interestService.findAllInterestsByUserId(journey.getUser().getId(), interestsPage));
        return mav;
    }

    @PostMapping(value = "/{id}/reply")
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyForm rjf,
                                       final BindingResult errors, final RedirectAttributes redirectAttributes,
                                       @ModelAttribute("user") User user) {

        LOGGER.debug("Replying to journey {} from form {}", id, rjf);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("replyJourneyForm", rjf);
            return new ModelAndView(REDIRECT_JOURNEY + id);
        }
        js.replyToJourney(user.getEmail(), id, rjf.getMessage());

        return new ModelAndView(REDIRECT_JOURNEY + id);
    }


    @GetMapping(value = "/{id}/update")
    public ModelAndView showUpdateJourneyForm(@PathVariable("id") long journeyId,
                                              @ModelAttribute("user") User user,
                                              @ModelAttribute("createJourneyForm") CreateJourneyForm form,
                                              BindingResult errors) {
        LOGGER.debug("User {} requested to update journey {}", user, journeyId);

        Journey journey = js.getJourneyById(journeyId)
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

    @PostMapping(value = "/{id}/update")
    public ModelAndView updateJourney(@PathVariable("id") long journeyId,
                                      @ModelAttribute("user") User user,
                                      @Valid @ModelAttribute("createJourneyForm") CreateJourneyForm form,
                                      BindingResult errors) {

        LOGGER.debug("User {} submitted update for journey {}", user.getEmail(), journeyId);

        if (errors.hasErrors()) {
            return showUpdateJourneyForm(journeyId, user, form, errors);
        }

        js.editJourney(journeyId,
                form.getDestinationUniversity(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription());

        LOGGER.info("Journey {} updated successfully", journeyId);
        return new ModelAndView(REDIRECT_JOURNEY + journeyId);
    }





}

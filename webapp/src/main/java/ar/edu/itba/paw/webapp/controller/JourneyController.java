package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;

import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
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

@Controller
@RequestMapping("/journeys")
public class JourneyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyController.class);

    private final JourneyService js;
    private final CityService cityService;
    private final UniversityService universityService;
    private final InterestService interestService;
    //    private final JourneyResponseService journeyResponseService;
    private static final String REDIRECT_JOURNEY = "redirect:/journeys/";

    @Autowired
    public JourneyController(final JourneyService js, CityService cityService, UniversityService universityService, InterestService interestService) {
        this.js = js;
        this.cityService = cityService;
        this.universityService = universityService;
        this.interestService = interestService;
    }


    @GetMapping
    public ModelAndView getJourneys(@Valid @ModelAttribute("filterJourneyForm") FilterJourneyForm fjf, final BindingResult errors,
                                    @ModelAttribute("user") User user,
                                    @PageParamCustomizer(defaultSize = 8) PageParams  pageParams,
                                    @RequestParam(value = "search", required = false) String search,
                                    @RequestParam(value = "sort", required = false) String sortBy,
                                    @RequestParam(value = "direction", required = false) String direction) {

        LOGGER.debug("Getting journeys with filters: {}", fjf);
        final ModelAndView mav = new ModelAndView("journeys/list");

        boolean hasJourney = user != null && js.userHasJourney(user);
        if(! hasJourney && fjf.getIsMyDestination()){
            throw new InvalidException("You must have a journey to filter by destination");
        }
        mav.addObject("journeys", js.getAllJourneys(search, user, SortFieldJourney.from(sortBy), SortDirection.from(direction),
                fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests(),fjf.getIsPast(), fjf.getIsUpcoming(), fjf.getIsMyDestination(),
                fjf.getIsOngoing(), pageParams));
        mav.addObject("hasJourney", hasJourney);
        mav.addObject("pageSize", pageParams.getSize());
        mav.addObject("currentPage", pageParams.getPage());

        return mav;
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

    @GetMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf, @ModelAttribute("user") User user) {

        if (js.userHasJourney(user)) {
            LOGGER.debug("User already has a journey, redirecting to journey list");
            return new ModelAndView("redirect:/journeys");
        }

        return new ModelAndView("journeys/create");
        //  .addObject("universities", universityService.getAllUniversities("",1, DEFAULT_PAGE_SIZE).getContent());
    }

    @GetMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id,
                                   @ModelAttribute("user") User user,
                                   @ModelAttribute("replyJourneyForm") ReplyForm rjf,
                                   @PageParamCustomizer(defaultSize = 4) PageParams  repliesPage,
                                   @PageParamCustomizer(defaultSize = 8, pageParamName = "interestsPage", sizeParamName = "interestsSize") PageParams interestsPage) {

        LOGGER.debug("Getting info for journey {}", id);

        Journey journey = js.getJourneyById(id).orElseThrow(()-> new JourneyNotFoundException("Journey not found"));

        Page<JourneyResponse> journeyResponses = js.listAllResponsesFromJourney(journey.getId(), repliesPage);

        final ModelAndView mav = new ModelAndView("journeys/detail");
        mav.addObject("journey", journey);
        mav.addObject("journeyResponsesPage", journeyResponses);
        mav.addObject("commentsCount", js.getJourneyResponseCount(journey.getId()));

        if(user != null){
            mav.addObject("isOwner", js.isJourneyOwnedByUser(user.getEmail(),journey.getId()));
        }else{
            mav.addObject("isOwner", false);
        }

        mav.addObject("interestPage", interestService.findAllInterestsByUserId(journey.getUser().getId(), interestsPage));
        return mav;
    }
    @GetMapping(value = "/{id}/delete")
    public ModelAndView deleteJourneyForm(@PathVariable long id, @ModelAttribute("user") User user,
                                          @ModelAttribute("deleteForm") final ReplyForm form) {
        LOGGER.debug("Showing delete form for journey {}", id);

        Journey journey = js.getJourneyById(id).orElseThrow(() -> new JourneyNotFoundException("Journey not found"));

        ModelAndView mav = new ModelAndView("journeys/delete");
        mav.addObject("journey", journey);
        return mav;
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteJourney(@PathVariable long id, @Valid @ModelAttribute("deleteForm") final ReplyForm form,
                                      final BindingResult errors, @ModelAttribute("user") User user) {
        LOGGER.debug("Deleting journey {}", id);
        if(errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return deleteJourneyForm(id, user, form);
        }
        js.delete(id, form.getMessage());
        return new ModelAndView("redirect:/journeys");
    }

    @PostMapping(value = "/{id}")
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm")  ReplyForm rjf,
                                        BindingResult errors, @ModelAttribute("user") User user) {

        LOGGER.debug("Replying to journey {} from form {}", id, rjf);
        if (errors.hasErrors()) {
            return getJourney(id, user, rjf, new PageParams(1, 4), new PageParams(1, 8));
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
    @GetMapping(value = "/{journeyId}/reply/{id}/delete")
    public ModelAndView deleteJourneyReplyForm(@PathVariable(value = "journeyId") long journeyId,
                                               @PathVariable("id") long id,
                                               @ModelAttribute("user") User user,
                                               @ModelAttribute("deleteReplyForm") ReplyForm form) {
        LOGGER.debug("Showing delete form for reply {} from journey {}", id, journeyId);

        Journey journey = js.getJourneyById(journeyId).orElseThrow(() -> new JourneyNotFoundException("Journey not found"));
        JourneyResponse journeyResponse = js.findJourneyResponseById(id).orElseThrow(() -> new IllegalArgumentException("Reply not found"));

        ModelAndView mav = new ModelAndView("journeys/delete-reply");
        mav.addObject("journey", journey);
        mav.addObject("journeyResponse", journeyResponse);
        return mav;
    }

    @PostMapping("{journeyId}/reply/{id}/delete")
    public ModelAndView deleteJourneyReply(@PathVariable(value = "journeyId") long journeyId,
                                           @PathVariable("id") long id,
                                           @Valid @ModelAttribute("deleteReplyForm") ReplyForm form,
                                           BindingResult errors,
                                           @ModelAttribute("user") User user) {
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return deleteJourneyReplyForm(journeyId, id, user, form);
        }

        js.deleteJourneyResponse(id, form.getMessage());
        return new ModelAndView("redirect:/journeys/" + journeyId);
    }

}

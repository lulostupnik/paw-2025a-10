package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.JourneyResponseNotFoundException;
import ar.edu.itba.paw.webapp.form.*;

import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/journeys")
public class JourneyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyController.class);

    private final JourneyService js;
    private final InterestService interestService;
    private final EventService eventService;
    private static final String REDIRECT_JOURNEY = "redirect:/journeys/";

    @Autowired
    public JourneyController(final JourneyService js, InterestService interestService, EventService eventService) {
        this.js = js;
        this.interestService = interestService;
        this.eventService = eventService;
    }


    @GetMapping
    public ModelAndView getJourneys(@Valid @ModelAttribute("filterJourneyForm") FilterJourneyForm fjf, final BindingResult errors,
                                    @ModelAttribute("user") User user,
                                    @PageParamCustomizer(defaultSize = 8) PageParams  pageParams,
                                    @RequestParam(value = "search", required = false) String search,
                                    @RequestParam(value = "sort", required = false) String sortBy,
                                    @RequestParam(value = "direction", required = false) String direction) {

        final ModelAndView mav = new ModelAndView("journeys/list");
        boolean hasJourney = user != null && js.existsByUser(user);
        if(! errors.hasErrors()) {
            mav.addObject("journeys", js.findJourneys(search, user, SortFieldJourney.from(sortBy), SortDirection.from(direction),
                    fjf.getDestination(), fjf.getStartDate(), fjf.getEndDate(), fjf.getInterests(), fjf.getIsPast(), fjf.getIsUpcoming(), fjf.getIsMyDestination(),
                    fjf.getIsOngoing(), pageParams));
        }
        mav.addObject("hasJourney", hasJourney);
        mav.addObject("pageSize", pageParams.getSize());
        mav.addObject("currentPage", pageParams.getPage());
        return mav;
    }

    @PostMapping(value = "/create")
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf,
                                      final BindingResult errors, @ModelAttribute("user") User user) {
        if (errors.hasErrors()) {
            return createJourneyForm(jf, user);
        }

        final Journey journey = js.createJourney(user,
                jf.getDestinationUniversity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());

        return new ModelAndView(REDIRECT_JOURNEY + journey.getId());
    }

    @GetMapping(value = "/create")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf, @ModelAttribute("user") User user) {

        if (js.existsByUser(user)) {
            return new ModelAndView("redirect:/journeys");
        }

        return new ModelAndView("journeys/create");
    }

    @GetMapping(value = "/{id}")
    public ModelAndView getJourney(@PathVariable long id,
                                   @ModelAttribute("user") User user,
                                   @ModelAttribute("replyJourneyForm") ReplyForm rjf,
                                   @PageParamCustomizer(defaultSize = 4) PageParams  repliesPage,
                                   @PageParamCustomizer(defaultSize = 8, pageParamName = "interestsPage", sizeParamName = "interestsSize") PageParams interestsPage,
                                   @PageParamCustomizer(defaultSize = 6, pageParamName = "eventsPage", sizeParamName = "eventsSize") PageParams eventsPage) {
        Journey journey = js.getJourneyById(id).orElseThrow(() -> {
            LOGGER.error("Journey with ID {} not found", id);
            return new JourneyNotFoundException("Journey with ID " + id + " not found");
        });
        Page<JourneyResponse> journeyResponses = js.findJourneyResponses(journey.getId(), repliesPage);
        Page<Event> journeyEvents = eventService.findJourneyEvents(journey, eventsPage);

        final ModelAndView mav = new ModelAndView("journeys/detail/detail");
        mav.addObject("journey", journey);
        mav.addObject("journeyResponsesPage", journeyResponses);
        mav.addObject("commentsCount", js.countJourneyResponses(journey.getId()));
        mav.addObject("isOwner", user != null && js.isJourneyOwnedByUser(journey, user));
        mav.addObject("interestPage", interestService.findInterestsByUser(journey.getUser(), interestsPage));
        mav.addObject("eventsPage", journeyEvents);
        mav.addObject("eventsPageSize", eventsPage.getSize());
        return mav;
    }

    @GetMapping(value = "/{id}/delete")
    public ModelAndView deleteJourneyForm(@PathVariable long id,
                                          @ModelAttribute("deleteForm") final DeleteJourneyForm form,
                                          @ModelAttribute("user") User user) {

        Journey journey = js.getJourneyById(id).orElseThrow(() -> {
            LOGGER.error("Journey with ID {} not found", id);
            return new JourneyNotFoundException("Journey with ID " + id + " not found");
        });
        ModelAndView mav = new ModelAndView("journeys/delete");
        mav.addObject("journey", journey);
        mav.addObject("isJourneyOwner", js.isJourneyOwnedByUser(user.getEmail(), id));
        return mav;
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteJourney(@PathVariable long id, @Valid @ModelAttribute("deleteForm") final DeleteJourneyForm form,
                                      final BindingResult errors, @ModelAttribute("user") User user) {
        if(errors.hasErrors()) {
            return deleteJourneyForm(id, form, user);
        }
        js.deleteJourney(id, form.getMessage());
        return new ModelAndView("redirect:/journeys");
    }

    @PostMapping(value = "/{id}")
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm")  ReplyForm rjf,
                                        BindingResult errors, @ModelAttribute("user") User user) {
        if (errors.hasErrors()) {
            return getJourney(id, user, rjf, new PageParams(1, 4), new PageParams(1, 8), new PageParams(1, 6));
        }
        js.createJourneyResponse(user.getEmail(), id, rjf.getMessage());
        return new ModelAndView(REDIRECT_JOURNEY + id);
    }


    @GetMapping(value = "/{id}/update")
    public ModelAndView showUpdateJourneyForm(@PathVariable("id") long journeyId,
                                              @ModelAttribute("createJourneyForm") CreateJourneyForm form,
                                              BindingResult errors) {

        Journey journey = js.getJourneyById(journeyId)
                .orElseThrow(()-> new JourneyNotFoundException("Journey with id %d not found to update".formatted(journeyId)));

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
        if (errors.hasErrors()) {
            return showUpdateJourneyForm(journeyId, form, errors);
        }
        js.updateJourney(journeyId,
                form.getDestinationUniversity(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription());
        return new ModelAndView(REDIRECT_JOURNEY + journeyId);
    }
    @GetMapping(value = "/{journeyId}/reply/{id}/delete")
    public ModelAndView deleteJourneyReplyForm(@PathVariable(value = "journeyId") long journeyId,
                                               @PathVariable("id") long id,
                                               @ModelAttribute("deleteReplyForm") ReplyForm form) {
        JourneyResponse journeyResponse = js.findJourneyResponseById(id).orElseThrow(() -> {
            LOGGER.warn("Journey with ID {} not found", id); // todo: es warn o error?
            return new JourneyResponseNotFoundException("Reply not found");
        });
        ModelAndView mav = new ModelAndView("journeys/delete-reply");
        mav.addObject("journey", journeyResponse.getJourney());
        mav.addObject("journeyResponse", journeyResponse);
        return mav;
    }

    @PostMapping("{journeyId}/reply/{id}/delete")
    public ModelAndView deleteJourneyReply(@PathVariable(value = "journeyId") long journeyId,
                                           @PathVariable("id") long id,
                                           @Valid @ModelAttribute("deleteReplyForm") ReplyForm form,
                                           BindingResult errors) {
        if (errors.hasErrors()) {
            return deleteJourneyReplyForm(journeyId, id, form);
        }
        js.deleteJourneyResponse(id, form.getMessage());
        return new ModelAndView("redirect:/journeys/" + journeyId);
    }

}
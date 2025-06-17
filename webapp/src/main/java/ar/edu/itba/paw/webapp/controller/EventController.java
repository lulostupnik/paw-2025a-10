package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
import ar.edu.itba.paw.webapp.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.Optional;
import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;


@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);


    private final EventService eventService;

    private static final String REDIRECT = "redirect:/events/";

    @Autowired
    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping
    public ModelAndView getEvents(@ModelAttribute("user") User user,
                                  @PageParamCustomizer(defaultSize = 8) PageParams  pageParams,
                                  @RequestParam(value = "search", required = false) String search,
                                  @Valid @ModelAttribute("filterEventForm") FilterEventForm filterForm,
                                  BindingResult errors,
                                  @RequestParam(value = "sort", required = false) String sortBy,
                                  @RequestParam(value = "direction", required = false) String direction) {

        ModelAndView mav = new ModelAndView("events/list");
        LOGGER.debug("Getting events list with search: {}, filter: {}, pageParams: {}, sortBy: {}, direction: {}",
                search, filterForm, pageParams, sortBy, direction);

        if(! errors.hasErrors()) {
            Page<Event> userEventsPage = eventService.searchEventsWithFilters(search, user, SortFieldEvent.from(sortBy), SortDirection.from(direction),
                    filterForm.getDestination(), filterForm.getStartDate(), filterForm.getEndDate(), filterForm.getInterests(),
                    filterForm.getIsPast(), filterForm.getIsUpcoming(), filterForm.getAttending(), pageParams);
            mav.addObject("eventsPage", userEventsPage);
        }

        mav.addObject("currentPage", pageParams.getPage());
        mav.addObject("pageSize", pageParams.getSize());
        return mav;
    }


    @GetMapping(value = "/create")
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        return new ModelAndView("events/create");
    }

    @PostMapping(path = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createEventForm(eventForm);
        }
        byte[] flyerBytes = getBytes(eventForm.getFlyer());
        Event event = eventService.createEvent(
            user.getEmail(),
            eventForm.getCity(), 
            eventForm.getDate(), 
            flyerBytes, 
            eventForm.getDescription(), 
            eventForm.getTitle(), 
            eventForm.getTime(), 
            eventForm.getAddress(), 
            eventForm.getAttendeesLimit()
        );
        return new ModelAndView(REDIRECT + event.getId());
    }

    private ModelAndView populateEventDetails( EventWithStatistics eventWithStatistics, long id,
                                              PageParams pageParams, PageParams attendeesPageParams, User user) {
        ModelAndView mav = new ModelAndView("events/detail/detail");
        Event event = eventWithStatistics.getEvent();
        mav.addObject("event", event);
        mav.addObject("createdEventsCount", eventWithStatistics.getCreatedEventsCount());
        mav.addObject("attendedEventsCount", eventWithStatistics.getAttendedEventsCount());
        mav.addObject("topAttendeeCountry", eventWithStatistics.getTopAttendeeCountry());
        mav.addObject("topAttendeeCountryCount", eventWithStatistics.getTopAttendeeCountryCount());
        mav.addObject("attendeesPage", eventService.findEventAttendees(event.getId(), attendeesPageParams));
        mav.addObject("attendeesCount", event.getAttendeesCount());
        Page<EventResponse> eventResponsesPage = eventService.findEventResponses(event.getId(), pageParams);
        mav.addObject("eventResponsesPage", eventResponsesPage);
        mav.addObject("commentsCount", eventService.countEventResponses(event.getId()));
        mav.addObject("attend", eventWithStatistics.isAttending());
        mav.addObject("isEventOwner", eventWithStatistics.isCreator());
        mav.addObject("isFull", event.getFull());


        mav.addObject("averageRating", event.getRating());

        if (user != null) {
            Optional<Rating> maybeUserRating = eventService.findRatingByUserAndEvent(user.getId(), event.getId());
            maybeUserRating.ifPresent(rating -> mav.addObject("userRating", rating.getRating()));
        }
        mav.addObject("ratingCount", eventService.countRatingsByEvent(event.getId()));


        return mav;
    }


    @GetMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id, @ModelAttribute("replyEventForm") final ReplyForm form,
        @ModelAttribute("eventRatingForm") final RatingForm ratingForm,
        @ModelAttribute("user") User user,
        @PageParamCustomizer(defaultSize = 4) PageParams  repliesPage,
        @PageParamCustomizer(defaultSize = 6, pageParamName = "attendeesPage", sizeParamName = "attendeesSize") PageParams attendeesPage)
    {
        EventWithStatistics eventWithStatistics = eventService.findEventWithStatistics(user,id).orElseThrow(() -> {
            LOGGER.error("eventWithStatistics not found for id: {}", id);
            return new EventNotFoundException(id);});
        return populateEventDetails(eventWithStatistics,
                id, repliesPage, attendeesPage, user );
    }

    @PostMapping("/{id}/rating")
    public ModelAndView rateEvent(@PathVariable long id, @Valid @ModelAttribute("eventRatingForm") final RatingForm form,
                                  final BindingResult errors, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eventRatingForm", errors);
            redirectAttributes.addFlashAttribute("eventRatingForm", form);
            LOGGER.debug("Found {} errors in rating form data", errors.getErrorCount());
            return new ModelAndView(REDIRECT + id);
        }
        LOGGER.debug("Rating event {} with rating {}", id, form.getRating());
        eventService.rateEvent(user, id, form.getRating());
        return new ModelAndView(REDIRECT + id);
    }
    @PostMapping("/{id}/rating/update")
    public ModelAndView updateEventRating(@PathVariable long id, @Valid @ModelAttribute("eventRatingForm") final RatingForm form,
                                          final BindingResult errors, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eventRatingForm", errors);
            redirectAttributes.addFlashAttribute("eventRatingForm", form);
            LOGGER.debug("Found {} errors in rating form data", errors.getErrorCount());
            return new ModelAndView(REDIRECT + id);
        }
        LOGGER.debug("Updating rating for event {} with rating {}", id, form.getRating());
        eventService.updateEventRating(user, id, form.getRating());
        return new ModelAndView(REDIRECT + id);
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteEvent(@PathVariable long id,
            @ModelAttribute("user") User user,
            @Valid @ModelAttribute("deleteForm") final DeleteEventForm form,
                                    final BindingResult errors) {
        if (errors.hasErrors()) {
            return deleteEventForm(id, user, form);
        }
        eventService.deleteEvent(id, form.getMessage());
        return new ModelAndView(REDIRECT);
    }
    @GetMapping(value = "/{id}/delete")
    public ModelAndView deleteEventForm(@PathVariable long id, @ModelAttribute("user") User user,
                                        @ModelAttribute("deleteForm") final DeleteEventForm form) {
        LOGGER.debug("Showing delete form for event {}", id);

        Event event = eventService.findEventById(id).orElseThrow(() -> {
            LOGGER.error("event not found for id: {}", id);
            return new EventNotFoundException(id);});
        long commentsCount = eventService.countEventResponses(event.getId());

        ModelAndView mav = new ModelAndView("events/delete");
        mav.addObject("event", event);
        mav.addObject("commentsCount", commentsCount);
        mav.addObject("isEventOwner", eventService.isEventOwnedByUser(user.getEmail(), event.getId()));
        return mav;
    }



    @PostMapping(value = "/{id}")
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form,
                              final BindingResult errors, @ModelAttribute("user") User user) {
        if (errors.hasErrors()) {
            return getEvent(id, form, new RatingForm(), user, new PageParams(1, 4), new PageParams(1, 6));
        }
        eventService.replyToEvent(user.getEmail(), id, form.getMessage());
        return new ModelAndView(REDIRECT + id);
    }

    @PostMapping(value="/{id}/attend",produces = "application/json")
    public ModelAndView attendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                    @ModelAttribute("user") User user) {
        eventService.createEventAttendance(user.getEmail(), id);
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        } else {
            return new ModelAndView(REDIRECT + id);
        }
    }

    @PostMapping(value="/{id}/dont-attend",produces = "application/json")
    public ModelAndView dontAttendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                        @ModelAttribute("user") User user) {
        eventService.deleteEventAttendance(user.getEmail(), id);
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        } else {
            return new ModelAndView(REDIRECT + id);
        }
    }
    @GetMapping(value = "/{id}/update")
    public ModelAndView showUpdateEventForm(@PathVariable("id") int eventId,
                                            @ModelAttribute("user") User user,
                                            @ModelAttribute("editEventForm") EditEventForm form,
                                            BindingResult errors) {

        Event event = eventService.findEventById(eventId).orElseThrow(() -> {
            LOGGER.error("event not found for id: {}", eventId);
            return new EventNotFoundException(eventId);});

        if(!errors.hasErrors()) {
            form.setCity(event.getCity().getName());
            form.setDate(event.getDate());
            form.setDescription(event.getDescription());
            form.setTitle(event.getTitle());
            form.setTime(event.getTime());
            form.setAddress(event.getAddress());
            form.setAttendeesLimit(event.getAttendeesLimit());
        }

        ModelAndView mav = new ModelAndView("events/edit");
        mav.addObject("eventId", eventId);
        return mav;
    }

    @PostMapping(value = "/{id}/update")
    public ModelAndView updateEvent(@PathVariable("id") int eventId,
                                    @ModelAttribute("user") User user,
                                    @Valid @ModelAttribute("editEventForm") EditEventForm form,
                                    BindingResult errors) {

        if(errors.hasErrors()) {
            return showUpdateEventForm(eventId, user, form, errors);
        }
        byte[] flyerContent = ImageUtils.getBytes(form.getFlyer());
        eventService.updateEvent(
                eventId,
                form.getCity(),
                form.getDate(),
                flyerContent,
                form.getDescription(),
                form.getTitle(),
                form.getTime(),
                form.getAddress(),
                form.getAttendeesLimit()
        );
        return new ModelAndView(REDIRECT + eventId);
    }

    @GetMapping(value = "/reply/{id}/delete")
    public ModelAndView deleteEventReplyForm(
            @PathVariable("id") long id,
            @ModelAttribute("deleteReplyForm") ReplyForm form) {
        EventResponse er = eventService.findEventResponseById(id).orElseThrow(() -> {
            LOGGER.error("Event response with id {} not found", id);
            return new EventResponseNotFoundException("eventResponse not found");});
        ModelAndView mav = new ModelAndView("events/delete-reply");
        mav.addObject("event", er.getEvent());
        mav.addObject("eventResponse", er);
        return mav;
    }

    @PostMapping("/reply/{id}/delete")
    public ModelAndView deleteEventReply(
            @PathVariable("id") long id, @Valid @ModelAttribute("deleteReplyForm") ReplyForm form,
                                         BindingResult errors) {
        EventResponse er = eventService.findEventResponseById(id).orElseThrow(() ->{
            LOGGER.error("Event response not found {}", id);
            return new EventResponseNotFoundException("Event response doesn't exist");});

        if (errors.hasErrors()) {
            return deleteEventReplyForm(er.getEvent().getId(), form);
        }

        eventService.deleteEventResponse(er, form.getMessage());
        return new ModelAndView( "redirect:/events/" + er.getEvent().getId());
    }




}

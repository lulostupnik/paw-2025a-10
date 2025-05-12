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

import java.util.NoSuchElementException;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;



@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);


    private final EventService eventService;
    private final UserService userService;

    private static final String REDIRECT = "redirect:/events/";

    @Autowired
    public EventController(final EventService eventService, final UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
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
            Page<Event> userEventsPage = eventService.getEventsPage(search, user, SortFieldEvent.from(sortBy), SortDirection.from(direction),
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
        LOGGER.debug("Getting event creation form");
        return new ModelAndView("events/create");
    }

    @PostMapping(path = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors, @ModelAttribute("user") User user) {

        LOGGER.debug("CREATING EVENT FROM FORM {}", eventForm);
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
        LOGGER.info("Successfully created event {}", event);
        return new ModelAndView(REDIRECT + event.getId());
    }

    private ModelAndView populateEventDetails( EventWithStatistics eventWithStatistics, long id,
                                              PageParams pageParams, PageParams attendeesPageParams) {
        ModelAndView mav = new ModelAndView("events/detail/detail");
        Event event = eventWithStatistics.getEvent();
        mav.addObject("event", event);
        mav.addObject("createdEventsCount", eventWithStatistics.getCreatedEventsCount());
        mav.addObject("attendedEventsCount", eventWithStatistics.getAttendedEventsCount());
        mav.addObject("topAttendeeCountry", eventWithStatistics.getTopAttendeeCountry());
        mav.addObject("topAttendeeCountryCount", eventWithStatistics.getTopAttendeeCountryCount());

        LOGGER.info("Found event {}", event);
        mav.addObject("attendeesPage", userService.getEventAttendees(event.getId(), attendeesPageParams));

        mav.addObject("attendeesCount", eventService.getEventAttendeesCount(event.getId()));
        Page<EventResponse> eventResponsesPage = eventService.listAllResponseFromEvent(event.getId(), pageParams);
        mav.addObject("eventResponsesPage", eventResponsesPage);
        mav.addObject("commentsCount", eventService.getResponseCount(event.getId()));


        if(eventWithStatistics.isCreator()){
            mav.addObject("attendees", userService.getEventAttendees(id));
        }
        mav.addObject("attend", eventWithStatistics.isAttending());
        mav.addObject("isEventOwner", eventWithStatistics.isCreator());
        mav.addObject("isFull", event.getFull());
        return mav;
    }


    @GetMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id, @ModelAttribute("replyEventForm") final ReplyForm form,
        @ModelAttribute("user") User user,
        @PageParamCustomizer(defaultSize = 4) PageParams  repliesPage,
        @PageParamCustomizer(defaultSize = 6, pageParamName = "attendeesPage", sizeParamName = "attendeesSize") PageParams attendeesPage)
    {
        LOGGER.debug("Getting info for event {}", id);
        EventWithStatistics eventWithStatistics = eventService.findEventWithStatistics(user, id).orElseThrow(EventNotFoundException::new);


        return populateEventDetails(eventWithStatistics,
                id, repliesPage, attendeesPage );
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteEvent(@PathVariable long id,
            @ModelAttribute("user") User user,
            @Valid @ModelAttribute("deleteForm") final ReplyForm form,
                                    final BindingResult errors) {
        if (errors.hasErrors()) {
            return deleteEventForm(id, user, form);
        }
        eventService.delete(id, form.getMessage());
        return new ModelAndView(REDIRECT);
    }
    @GetMapping(value = "/{id}/delete")
    public ModelAndView deleteEventForm(@PathVariable long id, @ModelAttribute("user") User user,
                                        @ModelAttribute("deleteForm") final ReplyForm form) {
        LOGGER.debug("Showing delete form for event {}", id);

        Event event = eventService.getEventById(id).orElseThrow(EventNotFoundException::new);
        long commentsCount = eventService.getResponseCount(event.getId());

        ModelAndView mav = new ModelAndView("events/delete");
        mav.addObject("event", event);
        mav.addObject("commentsCount", commentsCount);
        return mav;
    }

    @GetMapping(value = "/{eventId}/reply/{id}/delete")
    public ModelAndView deleteEventReplyForm(@PathVariable(value = "eventId") long eventId,
                                             @PathVariable("id") long id,
                                             @ModelAttribute("deleteReplyForm") ReplyForm form) {
        LOGGER.debug("Showing delete form for reply {} from event {}", id, eventId);

        Event event = eventService.getEventById(eventId).orElseThrow(EventNotFoundException::new);

        EventResponse eventResponse = eventService.findEventResponseById(id).orElseThrow(EventResponseNotFoundException::new);


        ModelAndView mav = new ModelAndView("events/delete-reply");
        mav.addObject("event", event);
        mav.addObject("eventResponse", eventResponse);
        return mav;
    }


    @PostMapping(value = "/{id}")
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form,
                              final BindingResult errors, @ModelAttribute("user") User user) {
        LOGGER.debug("Replying to event {} from form {}", id, form);

        if (errors.hasErrors()) {
            return getEvent(id, form, user, new PageParams(1, 4), new PageParams(1, 6));
        }

        eventService.replyToEvent(user.getEmail(), id, form.getMessage());
        return new ModelAndView(REDIRECT + id);
    }

    @PostMapping(value="/{id}/attend",produces = "application/json")
    public ModelAndView attendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                    @ModelAttribute("user") User user) {
        LOGGER.debug("Attending event {}", id);
        eventService.attendEvent(user.getEmail(), id);

        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        } else {
            return new ModelAndView(REDIRECT + id);
        }
    }

    @PostMapping(value="/{id}/dont-attend",produces = "application/json")
    public ModelAndView dontAttendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                        @ModelAttribute("user") User user) {
        LOGGER.debug("Cancel event attendance {}", id);

        eventService.cancelAttendance(user.getEmail(), id);
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

        LOGGER.debug("User {} requested to update event {}", user.getEmail(), eventId);

        Event event = eventService.getEventById(eventId).orElseThrow(NoSuchElementException::new);
        if(!errors.hasErrors()) {
            // 3. Prefill a CreateEventForm with existing event data
            form.setCity(event.getEventCity().getName());
            form.setDate(event.getDate());
            form.setDescription(event.getDescription());
            form.setTitle(event.getTitle());
            form.setTime(event.getTime().orElse(null));
            form.setAddress(event.getAddress());
            form.setAttendeesLimit(event.getAttendeesLimit().orElse(null));
        }

        ModelAndView mav = new ModelAndView("events/edit");
//        addDropdownAttributes(mav);
        mav.addObject("eventId", eventId);
        return mav;
    }

    //OBS para checkear. no se porque me deja subir una imagen vacia si uso el create event form.
    @PostMapping(value = "/{id}/update")
    public ModelAndView updateEvent(@PathVariable("id") int eventId,
                                    @ModelAttribute("user") User user,
                                    @Valid @ModelAttribute("editEventForm") EditEventForm form,
                                    BindingResult errors) {

        LOGGER.debug("User {} submitted update for event {}", user.getEmail(), eventId);
        if(errors.hasErrors()) {
            return showUpdateEventForm(eventId, user, form, errors);
        }

        byte[] flyerContent = ImageUtils.getBytes(form.getFlyer());

        eventService.editEvent(
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

        LOGGER.info("Event {} updated successfully", eventId);

        // 5. Redirect to the event detail page (or somewhere you want)
        return new ModelAndView(REDIRECT + eventId);
    }

    @PostMapping("{eventId}/reply/{id}/delete")
    public ModelAndView deleteEventReply(@PathVariable(value = "eventId") long eventId,
            @PathVariable("id") long id, @Valid @ModelAttribute("deleteReplyForm") ReplyForm form,
                                         BindingResult errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return deleteEventReplyForm(eventId, id, form);
        }
        eventService.deleteResponse(id, form.getMessage());
        return new ModelAndView( "redirect:/events/" + eventId);
    }




}

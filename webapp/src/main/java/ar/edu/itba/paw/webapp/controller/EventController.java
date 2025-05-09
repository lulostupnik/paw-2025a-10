package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.*;

import ar.edu.itba.paw.webapp.resolver.annotation.PageParamCustomizer;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;



@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);


    private final EventService eventService;
    private final CityService cityService;
    private final UniversityService universityService;
    private final CareerService careerService;
    private final InterestService interestService;
    private static final String REDIRECT = "redirect:/events/";

    @Autowired
    public EventController(CityService cityService, UniversityService universityService, CareerService careerService, EventService eventService, InterestService interestService) {
        this.eventService = eventService;
        this.cityService = cityService;
        this.universityService = universityService;
        this.careerService = careerService;
        this.interestService = interestService;
    }
    private void populateDropdownAttributes(ModelAndView mav) {
//        List<City> cities = cityService.getAllCities();
//        LOGGER.debug("Cities: {}", cities);
//        mav.addObject("cities", cities);
//
//        List<Interest> interests = interestService.findAll();
//        LOGGER.debug("Interests: {}", interests);
//        mav.addObject("interests", interests);
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

        Page<Event> userEventsPage = eventService.getEventsPageWithAttendanceStatus(search, user, sortBy, direction,
                filterForm.getDestination(), filterForm.getStartDate(), filterForm.getEndDate(), filterForm.getInterests(),
                filterForm.getIsPast(), filterForm.getIsUpcoming(), filterForm.getAttending(), pageParams);


        mav.addObject("eventsPage", userEventsPage);
        mav.addObject("eventsWithAttendance", userEventsPage.getContent());
        mav.addObject("currentPage", pageParams.getPage());
        mav.addObject("pageSize", pageParams.getSize());
        populateDropdownAttributes(mav);
        return mav;
    }


    private void addDropdownAttributes(ModelAndView mav) {
//        mav.addObject("careers", careerService.findAll());
//        mav.addObject("universities", universityService.getAllUniversities());
    }

    @GetMapping(value = "/create")
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        LOGGER.debug("Getting event creation form");
        ModelAndView mav = new ModelAndView("events/create");
        addDropdownAttributes(mav);
        return mav;
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

    private ModelAndView populateEventDetails( EventWithStatistics eventWithStatistics, long id, User user,
                                              BindingResult deleteErrors, BindingResult deleteReplyErrors,
                                              Long replyId, PageParams pageParams, PageParams attendeesPageParams) {
        ModelAndView mav = new ModelAndView("events/detail/detail");
        Event event = eventWithStatistics.getEvent();
        mav.addObject("event", event);
        mav.addObject("createdEventsCount", eventWithStatistics.getCreatedEventsCount());
        mav.addObject("attendedEventsCount", eventWithStatistics.getAttendedEventsCount());
        mav.addObject("topAttendeeCountry", eventWithStatistics.getTopAttendeeCountry());
        mav.addObject("topAttendeeCountryCount", eventWithStatistics.getTopAttendeeCountryCount());


        // Check if there are errors in the delete forms
        if (deleteErrors.hasErrors()) {
            // Add attributes to indicate there was an error in the journey delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "event");
            mav.addObject("deleteFormId", "delete-event-form");
        } else if (deleteReplyErrors.hasErrors()) {

            // Add attributes to indicate there was an error in a event response delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "eventResponse");
            mav.addObject("deleteFormId", "delete-event-response-form-" + replyId);
        }
        LOGGER.info("Found event {}", event);
        mav.addObject("attendeesPage", eventService.getEventAttendees(event.getId(), attendeesPageParams));
        mav.addObject("attendeesCount", eventService.getEventAttendeesCount(event.getId()));
        Page<EventResponse> eventResponsesPage = eventService.listAllResponseFromEvent(event.getId(), pageParams);
        mav.addObject("eventResponsesPage", eventResponsesPage);
        mav.addObject("commentsCount", eventService.getResponseCount(event.getId()));


        Boolean isFull = eventService.isEventFull(id);

        boolean isAttending = false;
        boolean isEventOwner = false;

        if(user != null) {
            isAttending = eventService.isUserAttending(user.getEmail(), id);
            isEventOwner = eventService.isEventOwnedByUser(user.getEmail(), id);
        }

        LOGGER.debug("User attending event {}", isAttending);
        LOGGER.debug("User is event owner {}", isEventOwner);

        if(isEventOwner){
            mav.addObject("attendees", eventService.getEventAttendees(id));
        }
        mav.addObject("attend", isAttending);
        mav.addObject("isEventOwner", isEventOwner);
        mav.addObject("isFull", isFull);
        return mav;
    }


    @GetMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form, final BindingResult errors,
        @ModelAttribute("user") User user,
        @Valid @ModelAttribute("deleteForm") final DeleteForm deleteForm, final BindingResult deleteErrors,
        @Valid @ModelAttribute("deleteReplyForm") final ReplyForm deleteReplyForm, final BindingResult deleteReplyErrors,
        @RequestParam(value = "replyId", required = false) Long replyId,
        @PageParamCustomizer(defaultSize = 4) PageParams  repliesPage,
        @PageParamCustomizer(defaultSize = 6, pageParamName = "attendeesPage", sizeParamName = "attendeesSize") PageParams attendeesPage)
    {
        LOGGER.debug("Getting info for event {}", id);
        Optional<EventWithStatistics> maybeEvent = eventService.findEventWithStatistics(id);
        if (maybeEvent.isEmpty()) { // error ControllerAdvice
            LOGGER.warn("Event {} not found", id);
            return new ModelAndView("events/not_found");
        }

        return populateEventDetails(maybeEvent.get(),
                id, user, deleteErrors, deleteReplyErrors, replyId ,repliesPage, attendeesPage );
    }
    @PostMapping("/delete")
    public ModelAndView deleteEvent(@ModelAttribute("user") User user,
            @Valid @ModelAttribute("deleteForm") final DeleteForm form,
                                    final BindingResult errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("deleteErrors", errors);
            redirectAttributes.addFlashAttribute("deleteForm", form);
            return new ModelAndView("events/" + form.getId() );
        }
        eventService.delete(form.getId(), form.getMessage());
        return new ModelAndView(REDIRECT);
    }


    @PostMapping(value = "/{id}/reply")
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form,
                              final BindingResult errors, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        LOGGER.debug("Replying to event {} from form {}", id, form);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("replyEventForm", form);
            return new ModelAndView(REDIRECT + id);
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
        LOGGER.debug("Attending event {}", id);

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
        addDropdownAttributes(mav);
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
            redirectAttributes.addFlashAttribute("deleteReplyErrors", errors);
            redirectAttributes.addFlashAttribute("deleteReplyForm", form);
            redirectAttributes.addAttribute("replyId",id );
            return new ModelAndView( "redirect:/events/" + eventId);
        }
        eventService.deleteResponse(id, form.getMessage());
        return new ModelAndView( "redirect:/events/" + eventId);
    }




}

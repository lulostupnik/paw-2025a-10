package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateEventForm;

import ar.edu.itba.paw.webapp.form.ReplyEventForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);


    private final EventService eventService;
    private final CityService cityService;
    private final UniversityService universityService;
    private final CareerService careerService;

    @Autowired
    public EventController(EventService eventService, CityService cityService, UniversityService universityService, CareerService careerService) {
        this.eventService = eventService;
        this.cityService = cityService;
        this.universityService = universityService;
        this.careerService = careerService;
    }

    @RequestMapping
    public ModelAndView getEvents() {
        LOGGER.debug("Loading events...");
        ModelAndView mav = new ModelAndView("events/list");
        List<Event> events = eventService.getAllEvents();
        LOGGER.debug("Events found: {}", events);
        mav.addObject("events", events);
        /*
        mav.addObject("eventsAttended", eventService.getUserAttendingEvents(SecurityContextHolder.getContext().getAuthentication().getName()));
        */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Event> eventsAttended = Collections.emptyList();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            eventsAttended = eventService.getUserAttendingEvents(auth.getName());
        }

        mav.addObject("eventsAttended", eventsAttended);
        return mav;
    }

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        LOGGER.debug("Getting event creation form");
        ModelAndView mav = new ModelAndView("events/create");
        List<City> cities = cityService.getAllCities();
        LOGGER.debug("Cities: {}", cities);
        List<University> universities = universityService.getAllUniversities();
        LOGGER.debug("Universities: {}", universities);
        List<Career> careers = careerService.findAll();
        LOGGER.debug("Careers: {}", careers);
        mav.addObject("careers", careers);
        mav.addObject("universities", universities);
        mav.addObject("cities", cities);
        return mav;
    }

    @RequestMapping(path = "/create", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors) {

        LOGGER.debug("CREATING EVENT FROM FORM {}", eventForm);
                                
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createEventForm(eventForm);
        }
        Event event;
        byte[] flyerBytes;
        try {
            flyerBytes = eventForm.getFlyer().getBytes();
            LOGGER.debug("User picture loaded successfully");
        } catch (IOException e) {
            //TODO: Display error to user in a friendly way
            LOGGER.error("Error getting submitted image: {}", e.getMessage(), new RuntimeException("Error reading flyer file", e));
            throw new RuntimeException("Error reading flyer file", e);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for: {}", authentication.getPrincipal());
        
        event = eventService.createEvent(authentication.getName(),eventForm.getCity(), eventForm.getDate(), flyerBytes, eventForm.getDescription(), eventForm.getTitle());
        LOGGER.info("Successfully created event {}", event);
        return getEvent(event.getId(), new ReplyEventForm());
    }

    @RequestMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id,  @ModelAttribute("replyEventForm") final ReplyEventForm form) {
        LOGGER.debug("Getting info for event {}", id);
        Optional<Event> maybeEvent = eventService.getEventById(id);
        if (maybeEvent.isEmpty()) {
            LOGGER.warn("Event {} not found", id);
            return new ModelAndView("events/not_found");
        }

        Event event = maybeEvent.get();
        LOGGER.info("Found event {}", event);

        List<EventResponse> eventResponses = eventService.getEventResponses(event.getId());
        LOGGER.debug("Got event responses {}", eventResponses);

        List<User> attendees = eventService.getEventAttendees(id);
        LOGGER.debug("Got event attendees {}", attendees);

        Boolean isAttending = eventService.isUserAttending(SecurityContextHolder.getContext().getAuthentication().getName(), id);
        LOGGER.debug("User attending event {}", isAttending);

        ModelAndView mav = new ModelAndView("events/detail");
        mav.addObject("event", event);
        mav.addObject("attendees", attendees);
        mav.addObject("attend", isAttending);
        mav.addObject("eventResponses", eventResponses);
        mav.addObject("replyEventForm", form);
        return mav;
    }

    private ModelAndView getReplyFormWithEvent(int id, ReplyEventForm form) {
        ModelAndView mav = new ModelAndView("events/reply");
        Optional<Event> event = eventService.getEventById(id);
        if(event.isEmpty()){
            LOGGER.debug("Event {} not found", id);
            return getEvent(id, form);
        }
        LOGGER.debug("Event found: {}", event.get());

        List<Career> careers = careerService.findAll();
        LOGGER.debug("Found careers {}", careers);

        List<University> universities = universityService.getAllUniversities();
        LOGGER.debug("Found universities {}", universities);

        mav.addObject("careers", careers);
        mav.addObject("universities", universities);
        mav.addObject("event", event.get());
        mav.addObject("replyEventForm", form);
        return mav;
    }

    @RequestMapping(value = "/{id}/reply")
    public ModelAndView createReplyEventForm(@PathVariable int id, @ModelAttribute("replyEventForm") final ReplyEventForm form) {
        LOGGER.debug("Getting event reply form for event {}", id);
        return getReplyFormWithEvent(id,form);
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyEventForm form, BindingResult errors) {
        LOGGER.debug("Replying to event {} from form {}", id, form);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return getReplyFormWithEvent(id, form);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.debug("Auth provided for: {}", authentication.getPrincipal());

        eventService.replyToEvent(authentication.getName(), id, form.getMessage());
//        return new ModelAndView("redirect:/events");
        return getEvent(id, new ReplyEventForm());
    }

    @RequestMapping(value="/{id}/attend",method = POST,produces = "application/json")
    public ModelAndView attendEvent(@PathVariable int id) {
                LOGGER.debug("Attending event {}", id);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                eventService.attendEvent(authentication.getName(), id);
                return new ModelAndView("redirect:/events/{id}");
    }

    @RequestMapping(value="/{id}/dont-attend",method = POST,produces = "application/json")
    public ModelAndView dontAttendEvent(@PathVariable int id) {
        LOGGER.debug("Attending event {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        eventService.cancelAttendance(authentication.getName(), id);
        return new ModelAndView("redirect:/events/{id}");
    }


    
}

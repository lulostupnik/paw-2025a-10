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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;
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
    public ModelAndView getEvents(@ModelAttribute("username") String username) {
        ModelAndView mav = new ModelAndView("events/list");
        if (username != null ) {
            mav.addObject("eventsWithAttendance", eventService.getEventsWithAttendanceStatus(username));
        } else{
            mav.addObject("events",eventService.getAllEvents());
        }
        return mav;
    }

    private void addDropdownAttributes(ModelAndView mav) {
        mav.addObject("careers", careerService.findAll());
        mav.addObject("universities", universityService.getAllUniversities());
        mav.addObject("cities", cityService.getAllCities());
    }

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        LOGGER.debug("Getting event creation form");
        ModelAndView mav = new ModelAndView("events/create");
        addDropdownAttributes(mav);
        return mav;
    }

    @RequestMapping(path = "/create", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors, @ModelAttribute("username") String username) {

        LOGGER.debug("CREATING EVENT FROM FORM {}", eventForm);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return createEventForm(eventForm);
        }
        byte[] flyerBytes = getBytes(eventForm.getFlyer());

        Event event = eventService.createEvent(
            username,
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
        return new ModelAndView("redirect:/events/{id}", "id", event.getId());
    }

    private ModelAndView populateEventDetails( Event event, long id, String username) {
        ModelAndView mav = new ModelAndView("events/detail");
        mav.addObject("event", event);
        LOGGER.info("Found event {}", event);
        mav.addObject("attendees", eventService.getEventAttendees(event.getId()));
        mav.addObject("eventResponses", eventService.getEventResponses(event.getId()));

        Boolean isFull = eventService.isEventFull(id);

        boolean isAttending = false;
        boolean isEventOwner = false;

        if(username != null) {
            isAttending = eventService.isUserAttending(SecurityContextHolder.getContext().getAuthentication().getName(), id);
            isEventOwner = eventService.isEventOwnedByUser(SecurityContextHolder.getContext().getAuthentication().getName(), id);
        }

        LOGGER.debug("User attending event {}", isAttending);
        LOGGER.debug("User is event owner {}", isEventOwner);

        if(isEventOwner){  //@todo preguntar si es necesario este if
            mav.addObject("attendees", eventService.getEventAttendees(id));
        }
        mav.addObject("attend", isAttending);
        mav.addObject("isEventOwner", isEventOwner);
        mav.addObject("isFull", isFull);
        return mav;
    }

    @RequestMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id, @Valid @ModelAttribute("replyEventForm") final ReplyEventForm form, final BindingResult errors, @ModelAttribute("username") String username) {
        LOGGER.debug("Getting info for event {}", id);
        Optional<Event> maybeEvent = eventService.getEventById(id);
        if (maybeEvent.isEmpty()) {
            LOGGER.warn("Event {} not found", id);
            return new ModelAndView("events/not_found");
        }

        return populateEventDetails(maybeEvent.get(), id, username);
    }


    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyEventForm form,
                              final BindingResult errors, @ModelAttribute("username") String username, RedirectAttributes redirectAttributes) {
        LOGGER.debug("Replying to event {} from form {}", id, form);

        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("replyEventForm", form);
            return new ModelAndView("redirect:/events/{id}", "id", id);
        }

        eventService.replyToEvent(username, id, form.getMessage());
        return new ModelAndView("redirect:/events/{id}", "id", id);
    }

    @RequestMapping(value="/{id}/attend",method = POST,produces = "application/json")
    public ModelAndView attendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                    @ModelAttribute("username") String username) {
        LOGGER.debug("Attending event {}", id);
        eventService.attendEvent(username, id);

        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);//
        } else {
            return new ModelAndView("redirect:/events/{id}");
        }
    }

    @RequestMapping(value="/{id}/dont-attend",method = POST,produces = "application/json")
    public ModelAndView dontAttendEvent(@PathVariable int id, @RequestHeader(value = "Referer",required = false) String referer,
                                        @ModelAttribute("username") String username) {
        LOGGER.debug("Attending event {}", id);

        eventService.cancelAttendance(username, id);
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer); //@TODO preguntar si es lícito
        } else {
            return new ModelAndView("redirect:/events/{id}");
        }
    }
    //@TODO cambiar a spring security
    @RequestMapping(value = "/{id}/update", method = GET)
    public ModelAndView showUpdateEventForm(@PathVariable("id") int eventId,
                                            @ModelAttribute("username") String username) {

        LOGGER.debug("User {} requested to update event {}", username, eventId);

        Optional<Event> maybeEvent = eventService.getEventById(eventId);
        if (maybeEvent.isEmpty()) {
            LOGGER.warn("Event {} not found", eventId);
            return new ModelAndView("events/not_found"); //DEBERIA TIRAR UN error 404
        }

        Event event = maybeEvent.get();


        if (!event.getUser().getEmail().equals(username)) {
            LOGGER.warn("User {} is not owner of event {}", username, eventId);
            return new ModelAndView("errors/403"); // Forbidden page
        }

        // 3. Prefill a CreateEventForm with existing event data
        CreateEventForm form = new CreateEventForm();
        form.setCity(event.getEventCity().getName());
        form.setDate(event.getDate());
        form.setDescription(event.getDescription());
        form.setTitle(event.getTitle());
        form.setTime(event.getTime().orElse(null));
        form.setAddress(event.getAddress());
        form.setAttendeesLimit(event.getAttendeesLimit().orElse(null));

        // 4. Build the response
        ModelAndView mav = new ModelAndView("events/edit");
        mav.addObject("createEventForm", form);
        addDropdownAttributes(mav);
        mav.addObject("eventId", eventId);
        return mav;
    }

    //OBS para checkear. no se porque me deja subir una imagen vacia si uso el create event form.
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    public ModelAndView updateEvent(@PathVariable("id") int eventId,
                                    @ModelAttribute("username") String username,
                                    @ModelAttribute("createEventForm") CreateEventForm form) {

        LOGGER.debug("User {} submitted update for event {}", username, eventId);

        // 1. Validate event existence and ownership
        Optional<Event> maybeEvent = eventService.getEventById(eventId);
        if (maybeEvent.isEmpty()) {  //mejor tirar una excepcion y tener un exception handler. AOP
            LOGGER.warn("Event {} not found", eventId);
            return new ModelAndView("events/not_found");
        }

        Event event = maybeEvent.get();
        if (!event.getUser().getUsername().equals(username)) {   //@TODO mover a spring security.  usar metodo access
            LOGGER.warn("User {} is not owner of event {}", username, eventId);
            return new ModelAndView("errors/403"); // Forbidden
        }

        // 2. Extract flyer content of a new flyer is uploaded
        Optional<byte[]> flyerContent = Optional.empty();
        if (form.getFlyer() != null && !form.getFlyer().isEmpty()) {
            try {
                flyerContent = Optional.of(form.getFlyer().getBytes());
            } catch (IOException e) {
                LOGGER.error("Failed to read flyer file", e);
                // Optional: add error message to ModelAndView and return to edit page
                ModelAndView mav = new ModelAndView("events/edit");
                mav.addObject("createEventForm", form);
                mav.addObject("eventId", eventId);
                mav.addObject("errorMessage", "Failed to process uploaded flyer");
                addDropdownAttributes(mav);
                return mav;
            }
        }

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
        return new ModelAndView("redirect:/events/" + eventId);
    }




}

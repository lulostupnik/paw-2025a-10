package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateEventForm;

import ar.edu.itba.paw.webapp.form.ReplyForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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

    private ModelAndView populateEventDetails( Event event, long id, String username,
                                              BindingResult deleteErrors, BindingResult deleteReplyErrors,
                                              Long replyId) {
        ModelAndView mav = new ModelAndView("events/detail");
        mav.addObject("event", event);

        // Check if there are errors in the delete forms
        if (deleteErrors.hasErrors()) {
            // Add attributes to indicate there was an error in the journey delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "event");
            mav.addObject("deleteFormId", "delete-event-form");
        } else if (deleteReplyErrors.hasErrors()) {

            // Add attributes to indicate there was an error in a journey response delete form
            mav.addObject("deleteFormHasErrors", true);
            mav.addObject("deleteFormType", "eventResponse");
            mav.addObject("deleteFormId", "delete-event-response-form-" + replyId);
        }
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

        if(isEventOwner){
            mav.addObject("attendees", eventService.getEventAttendees(id));
        }
        mav.addObject("attend", isAttending);
        mav.addObject("isEventOwner", isEventOwner);
        mav.addObject("isFull", isFull);
        return mav;
    }

    @RequestMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form, final BindingResult errors,
                                 @ModelAttribute("username") String username,
                                 @Valid @ModelAttribute("deleteForm") final ReplyForm deleteForm, final BindingResult deleteErrors,
                                 @Valid @ModelAttribute("deleteReplyForm") final ReplyForm deleteReplyForm, final BindingResult deleteReplyErrors,
                                 @RequestParam(value = "replyId", required = false) Long replyId) {


        LOGGER.debug("Getting info for event {}", id);
        Optional<Event> maybeEvent = eventService.getEventById(id);
        if (maybeEvent.isEmpty()) {
            LOGGER.warn("Event {} not found", id);
            return new ModelAndView("events/not_found");
        }

        return populateEventDetails(maybeEvent.get(), id, username, deleteErrors, deleteReplyErrors, replyId);
    }
    @PostMapping("/{id}/delete")
    public ModelAndView deleteEvent(@PathVariable int id, @Valid @ModelAttribute("deleteForm") final ReplyForm form,
                                    final BindingResult errors, RedirectAttributes redirectAttributes) {
        LOGGER.debug("Deleting event {}", id);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("deleteErrors", errors);
            redirectAttributes.addFlashAttribute("deleteForm", form);
            return new ModelAndView("redirect:/events/{id}", "id", id);
        }
        eventService.deleteEvent(id, form.getMessage());
        return new ModelAndView("redirect:/events");
    }


    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyForm form,
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
            return new ModelAndView("redirect:" + referer); // @TODO history.back()
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
    
}

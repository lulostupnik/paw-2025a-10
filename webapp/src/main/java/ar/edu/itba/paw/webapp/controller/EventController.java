package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateEventForm;

import ar.edu.itba.paw.webapp.form.ReplyEventForm;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final CityService cityService;
    private final UniversityService universityService;

    public EventController(EventService eventService, UserService userService, CityService cityService, UniversityService universityService) {
        this.eventService = eventService;
        this.userService = userService;

        this.cityService = cityService;
        this.universityService = universityService;
    }

    @RequestMapping
    public ModelAndView getEvents() {
        ModelAndView mav = new ModelAndView("events/list");
        mav.addObject("events", eventService.getAllEvents());
        return mav;
    }

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        ModelAndView mav = new ModelAndView("events/create");
        List<City> cities = cityService.getAllCities();
        List<University> universities = universityService.getAllUniversities();
        mav.addObject("universities", universities);
        mav.addObject("cities", cities);
        return mav;
    }

    @RequestMapping(path = "/create", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("events/create");
        }
        byte[] image = null;
        try {
            if (eventForm.getFlyer() != null){
                image = eventForm.getFlyer().getBytes();
            }
        } catch(IOException e) {
            //what to do?
        }
        Event event;
        byte[] flyerBytes;
        byte[] profilePictureBytes;
        try {
            flyerBytes = eventForm.getFlyer().getBytes();
            profilePictureBytes = eventForm.getProfilePicture().getBytes();
        } catch (IOException e) {
            // Handle the exception, e.g., log it or return an error response
            throw new RuntimeException("Error reading flyer file", e);
        }

        event = eventService.createEvent(eventForm.getEmail(), eventForm.getCity(), eventForm.getDate(), flyerBytes, eventForm.getDescription(), eventForm.getFirstName(), eventForm.getLastName(), eventForm.getUsername(), eventForm.getUniversity(), eventForm.getCareer(), profilePictureBytes);
        return new ModelAndView("redirect:/events/" + event.getId());
    }

    @RequestMapping("/{id}")
    public ModelAndView getEvent(@PathVariable long id) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return new ModelAndView("redirect:/events"); // Redirect if event is not found
        }
        ModelAndView mav = new ModelAndView("events/detail");
        mav.addObject("event", event);
        return mav;
    }
    private ModelAndView getReplyFormWithEvent(int id, ReplyEventForm form) {
        ModelAndView mav = new ModelAndView("events/reply");
        Optional<Event> event = eventService.getEventById(id);
        event.ifPresent(e -> mav.addObject("event", e));
        mav.addObject("replyEventForm", form);
        return mav;
    }

    @RequestMapping(value = "/{id}/reply")
    public ModelAndView createReplyEventForm(@PathVariable int id, @ModelAttribute("replyEventForm") final ReplyEventForm form) {
        ModelAndView mav = new ModelAndView("events/reply");
        Optional<Event> event = eventService.getEventById(id);
        if(event.isEmpty()){
            return getEvent(id);
        }
        mav.addObject("event", event.get());
        mav.addObject("replyEventForm", form);
        return mav;
    }

    @RequestMapping(value = "/{id}/reply", method = POST)
    public ModelAndView reply(@PathVariable int id, @Valid @ModelAttribute("replyEventForm") final ReplyEventForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            return getReplyFormWithEvent(id, form);
        }
        // FIXME: The image id is hardcoded to 1, this should be changed to the logged in user id
        eventService.replyToEvent(form.getEmail(), form.getUsername(), form.getFirstName(), form.getLastName(), form.getOriginUniversity(), form.getCareer(), 1, id, form.getMessage());
        return getEvents();
    }

}

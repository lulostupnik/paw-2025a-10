package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.form.CreateEventForm;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping
    public ModelAndView getEvents() {
        ModelAndView mav = new ModelAndView("events/list");
        mav.addObject("events", eventService.getAllEvents());
        return mav;
    }

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        return new ModelAndView("events/create");
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
        Event event = eventService.createEvent(eventForm.getEmail(), eventForm.getCity(), eventForm.getDate(), image, eventForm.getDescription());
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
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @RequestMapping("/create")
    public ModelAndView showCreateEventForm(@ModelAttribute("createEventForm") final CreateEventForm form) {
        return new ModelAndView("events/create");
    }

    @RequestMapping(method = POST)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("events/create");
        }

        Event event = eventService.createEvent(eventForm.getEmail(), eventForm.getCity(), eventForm.getDate(), eventForm.getDescription());
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

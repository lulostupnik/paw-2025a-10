package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller

public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping("/events")
    public ModelAndView getEvents() {
        // TODO: Implement the logic to fetch events
        return new ModelAndView("event");
    }


    //FIXME: Not yet tested, nor finished
    @RequestMapping(value = "/events", method = POST)
    public ModelAndView createEvent(@Valid @ModelAttribute("createEventForm") final CreateEventForm eventForm,
                                    final BindingResult errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("event");
        }

        try {
            //FIXME: look for user by email if created, otherwise create it
            //User user = eventService.getUserByEmail(eventForm.getEmail());
            //FIXME: with the new or founded user it should create the event, we also need to add the image
            //Event event = eventService.createEvent("s","s","s","s","s","s",3, eventForm.);


            //ModelAndView mav = new ModelAndView("redirect:/events/" + eve);
            return new ModelAndView("event");
        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("event");
            mav.addObject("createEventForm", eventForm);
            mav.addObject("error", "Failed to create event: " + e.getMessage());
            return mav;
        }
    }

    @RequestMapping(value = "/events")
    public ModelAndView createEventForm(@ModelAttribute("createEventForm") final CreateEventForm ef) {
        return new ModelAndView("event");
    }


    @RequestMapping(value = "/events/{id}")
    public ModelAndView getEvent(@PathVariable int id) {
        return new ModelAndView("event");
    }
    @RequestMapping(value = "/events/{id}", method = PUT)
    public ModelAndView updateEvent(@PathVariable int id) {
        return new ModelAndView("event");
    }

    @RequestMapping(value = "/events/{id}/response", method = POST)
    public ModelAndView replyToEvent(@PathVariable int id) {
        return new ModelAndView("event");

    }
}

package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller

public class EventController {
    @RequestMapping("/events")
    public ModelAndView getEvents() {
        // TODO: Implement the logic to fetch events
        return new ModelAndView("event");
    }
    @RequestMapping(value = "/events", method = POST)
    public ModelAndView createEvent() {
        // TODO: Implement the logic to create an event
        return new ModelAndView("event");
    }
    @RequestMapping(value = "/events/{id}")
    public ModelAndView getEvent(@PathVariable int id) {
        //TODO: Implement the logic to fetch a specific event
        return new ModelAndView("event");
    }
    @RequestMapping(value = "/events/{id}", method = PUT)
    public ModelAndView updateEvent(@PathVariable int id) {
        //TODO: Implement the logic to update an event
        return new ModelAndView("event");
    }

    @RequestMapping(value = "/events/{id}/response", method = POST)
    public ModelAndView replyToEvent(@PathVariable int id) {
        // TODO: Implement the logic to reply to an event
        return new ModelAndView("event");

    }
}

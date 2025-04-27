package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event-replies")
public class EventReplyController {
    private final EventService eventService;

    public EventReplyController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{id}/delete")
    public String deleteEventReply(@PathVariable("id") long id) {
        // Logic to delete the event reply by ID
        eventService.deleteEventResponse(id);
        return "redirect:/event-replies"; // Redirect to the list of event replies after deletion
    }
}

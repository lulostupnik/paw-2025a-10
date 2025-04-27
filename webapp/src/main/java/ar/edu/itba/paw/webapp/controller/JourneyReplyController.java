package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/journey-replies")
public class JourneyReplyController {

    private final JourneyService journeyService;

    public JourneyReplyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @PostMapping("/{id}/delete")
    public String deleteJourneyReply(@PathVariable("id") long id) {
        journeyService.deleteJourneyResponse(id);
        return "redirect:/journeys"; // Redirect to the list of journey replies after deletion
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.webapp.form.ReplyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/journey-replies")
public class JourneyReplyController {

    private final JourneyService journeyService;

    @Autowired
    public JourneyReplyController( JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteJourneyReply(@PathVariable("id") long id,
                                           @Valid @ModelAttribute("deleteReplyForm") ReplyForm form, BindingResult errors,
                                           RedirectAttributes redirectAttributes) {
        long journeyId = journeyService.getJourneyIdByResponseId(id);
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("deleteReplyErrors", errors);
            redirectAttributes.addFlashAttribute("deleteReplyForm", form);
            redirectAttributes.addAttribute("replyId", id);
        }else {
            journeyService.deleteJourneyResponse(id, form.getMessage());
        }
        return new ModelAndView( "redirect:/journeys/" + journeyId ); // Redirect to the list of journey replies after deletion
    }
}

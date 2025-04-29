package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventResponseService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.webapp.form.ReplyForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/event-replies")
public class EventReplyController {
    private final EventService eventService;
    private final EventResponseService eventResponseService;

    public EventReplyController(EventService eventService, EventResponseService eventResponseService) {
        this.eventService = eventService;
        this.eventResponseService = eventResponseService;
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteEventReply(@PathVariable("id") long id, @Valid @ModelAttribute("deleteReplyForm") ReplyForm form,
                                         BindingResult errors, RedirectAttributes redirectAttributes) {
        // Logic to delete the event reply by ID
        long eventId = eventResponseService.getEventIdByResponseId(id);
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("deleteReplyErrors", errors);
            redirectAttributes.addFlashAttribute("deleteReplyForm", form);
            redirectAttributes.addAttribute("replyId",id );
            return new ModelAndView( "redirect:/events/" + eventId); // Redirect to the list of event replies in case of error
        }
        eventResponseService.delete(id, form.getMessage());
        return new ModelAndView( "redirect:/events/" + eventId);
    }
}

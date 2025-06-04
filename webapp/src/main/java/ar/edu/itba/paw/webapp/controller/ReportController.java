package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.models.exceptions.ReportNotFoundException;
import ar.edu.itba.paw.webapp.form.CreateReportForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    private static final String REPORT_CREATE = "components/report-modal";
    private static final String REPORT_DETAIL = "reports/detail";
    private static final String REPORT_CREATE_FORM = "createReportForm";
    private final JourneyService journeyService;
    private final EventService eventService;

    @Autowired
    public ReportController(ReportService reportService, JourneyService journeyService, EventService eventService) {
        this.reportService = reportService;
        this.journeyService = journeyService;
        this.eventService = eventService;
    }

    private ModelAndView createReportForm(final String contentType, final long contentId, final String actionUrl) {
        ModelAndView mav = new ModelAndView(REPORT_CREATE);
        mav.addObject("targetType", contentType);
        mav.addObject("targetId", contentId);
        mav.addObject("actionUrl", actionUrl);
        return mav;
    }

    @GetMapping(value = "/journeys/{journeyId}/create")
    public ModelAndView createJourneyReportForm(@PathVariable long journeyId,
                                                @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {
        return createReportForm("journey", journeyId, "/reports/journeys/" + journeyId + "/create");
    }

    @PostMapping(path = "/journeys/{journeyId}/create")
    public ModelAndView createJourneyReport(@PathVariable long journeyId,
                                            @ModelAttribute("user") User user,
                                            @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                            final BindingResult errors,
                                            final RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return createJourneyReportForm(journeyId, form);
        }
        reportService.createReportForJourney(user, journeyId, form.getDescription(),form.getReason());
        redirectAttributes.addFlashAttribute("reportSuccess", "Report submitted successfully");

        return new ModelAndView("redirect:/journeys/" + journeyId);
    }

    @GetMapping(value = "/journey-responses/{responseId}/create")
    public ModelAndView createJourneyResponseReportForm(@PathVariable long responseId,
                                                        @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {
        return createReportForm("comment", responseId, "/reports/journey-responses/" + responseId + "/create");
    }

    @PostMapping(path = "/journey-responses/{responseId}/create")
    public ModelAndView createJourneyResponseReport(@PathVariable long responseId,
                                                    @ModelAttribute("user") User user,
                                                    @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                                    final BindingResult errors,
                                                    final RedirectAttributes redirectAttributes) {
        JourneyResponse response = journeyService.findJourneyResponseById(responseId).orElseThrow( () -> new ReportNotFoundException("Journey response not found with id: " + responseId));

        if (errors.hasErrors()) {
            return createJourneyResponseReportForm(responseId,form);
        }
        reportService.createReportForJourneyResponse(user, responseId, form.getDescription(),form.getReason());
        redirectAttributes.addFlashAttribute("reportSuccess", "Report submitted successfully");

        return new ModelAndView("redirect:/journeys/" + response.getJourney().getId());
    }




    @GetMapping(value = "/events/{eventId}/create")
    public ModelAndView createEventReportForm(@PathVariable long eventId,
                                              @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {

        return createReportForm("event", eventId, "/reports/events/" + eventId + "/create");
    }


    @PostMapping(path = "/events/{eventId}/create")
    public ModelAndView createEventReport(@PathVariable long eventId,
                                          @ModelAttribute("user") User user,
                                          @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                          final BindingResult errors,
                                          final RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            LOGGER.debug("Event report Form errors: {}", errors.getAllErrors());
            return createEventReportForm(eventId, form);
        }
        reportService.createReportForEvent(user, eventId, form.getDescription(), form.getReason());
        redirectAttributes.addFlashAttribute("reportSuccess", "Report submitted successfully");

        return new ModelAndView("redirect:/events/" + eventId);
    }




    @GetMapping(value = "/event-responses/{responseId}/create")
    public ModelAndView createEventResponseReportForm(@PathVariable long responseId,
                                                      @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {

        return createReportForm("comment", responseId, "/reports/event-responses/" + responseId + "/create");
    }

    @PostMapping(path = "/event-responses/{responseId}/create")
    public ModelAndView createEventResponseReport(@PathVariable long responseId,
                                                  @ModelAttribute("user") User user,
                                                  @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                                  final BindingResult errors,
                                                  final RedirectAttributes redirectAttributes) {
        EventResponse response = eventService.findEventResponseById(responseId).orElseThrow( () -> new ReportNotFoundException("Event response not found with id: " + responseId));

        if (errors.hasErrors()) {
            return createEventResponseReportForm(responseId, form);
        }
        reportService.createReportForEventResponse(user, responseId, form.getReason(), form.getDescription());
        redirectAttributes.addFlashAttribute("reportSuccess", "Report submitted successfully");


        return new ModelAndView("redirect:/journeys/" + response.getEvent().getId());
    }

    @GetMapping(value= "/{id}")
    public ModelAndView getReport(@PathVariable(value = "id") final long id) {
        Report report = reportService.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        ModelAndView mav = new ModelAndView(REPORT_DETAIL);
        mav.addObject("report", report);

        if (report.getJourney() != null) {
            mav.addObject("reportedContent", report.getJourney());
            mav.addObject("contentType", "journey");
        } else if (report.getEvent() != null) {
            mav.addObject("reportedContent", report.getEvent());
            mav.addObject("contentType", "event");
        } else if (report.getJourneyResponse() != null) {
            mav.addObject("reportedContent", report.getJourneyResponse());
            mav.addObject("contentType", "journey-comment");
        } else if (report.getEventResponse() != null) {
            mav.addObject("reportedContent", report.getEventResponse());
            mav.addObject("contentType", "event-comment");
        }

        return mav;
    }


    @PostMapping(value = "/{id}/status")
    public ModelAndView updateReportStatus(@PathVariable long id,
                                           @RequestParam ReportStatus status) {

        Report report = reportService.updateReportStatus(id,status);
        return new ModelAndView("redirect:/reports/" + report.getId());
    }


}
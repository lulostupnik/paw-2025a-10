package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
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

@Controller
@RequestMapping("/reports")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    private static final String REPORT_CREATE = "reports/create";
    private static final String REPORT_DETAIL = "reports/detail";
    private static final String REPORT_CREATE_FORM = "createReportForm";

    @Autowired
    public ReportController(ReportService reportService, UserService userService,
                            JourneyService journeyService, EventService eventService) {
        this.reportService = reportService;
    }


    @GetMapping(value = "/journeys/{journeyId}/create")
    public ModelAndView createJourneyReportForm(@PathVariable long journeyId,
                                                @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {

        return new ModelAndView(REPORT_CREATE);
    }


    @PostMapping(path = "/journeys/{journeyId}/create")
    public ModelAndView createJourneyReport(@PathVariable long journeyId,
                                            @ModelAttribute("user") User user,
                                            @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                            final BindingResult errors) {

        if (errors.hasErrors()) {
            return createJourneyReportForm(journeyId, form);
        }
        reportService.createReportForJourney(user, journeyId, form.getReason(), form.getDescription());
        return new ModelAndView("redirect:/journeys/");
    }


    @GetMapping(value = "/events/{eventId}/create")
    public ModelAndView createEventReportForm(@PathVariable long eventId,
                                              @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {

        return new ModelAndView(REPORT_CREATE);
    }

    @PostMapping(path = "/events/{eventId}/create")
    public ModelAndView createEventReport(@PathVariable long eventId,
                                          @ModelAttribute("user") User user,
                                          @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                          final BindingResult errors) {

        if (errors.hasErrors()) {
            return createEventReportForm(eventId, form);
        }
        reportService.createReportForEvent(user,eventId, form.getReason(), form.getDescription());
        return new ModelAndView("redirect:/events/");
    }


    @GetMapping(value = "/journey-responses/{responseId}/create")
    public ModelAndView createJourneyResponseReportForm(@PathVariable long responseId,
                                                        @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {
        return new ModelAndView(REPORT_CREATE);
    }

    @PostMapping(path = "/journey-responses/{responseId}/create")
    public ModelAndView createJourneyResponseReport(@PathVariable long responseId,
                                                    @ModelAttribute("user") User user,
                                                    @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                                    final BindingResult errors) {



        if (errors.hasErrors()) {
            return createJourneyResponseReportForm(responseId, form);
        }
        reportService.createReportForJourneyResponse(user, responseId, form.getReason(), form.getDescription());
        return new ModelAndView("redirect:/journeys/");
    }

    @GetMapping(value = "/event-responses/{responseId}/create")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView createEventResponseReportForm(@PathVariable long responseId,
                                                      @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {
        return new ModelAndView(REPORT_CREATE);
    }

    @PostMapping(path = "/event-responses/{responseId}/create")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView createEventResponseReport(@PathVariable long responseId,
                                                  @ModelAttribute("user") User user,
                                                  @Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
                                                  final BindingResult errors) {
        if (errors.hasErrors()) {
            return createEventResponseReportForm(responseId, form);
        }
        reportService.createReportForEventResponse(user, responseId, form.getReason(), form.getDescription());
        return new ModelAndView("redirect:/events/");
    }

    @GetMapping(value= "/{id}")
    public ModelAndView getReport(@PathVariable(value = "id") final long id) {
        Report report = reportService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

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
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

    private static final String REPORT_CREATE = "reports/create";
    private static final String REPORT_DETAIL = "reports/detail";
    private static final String REPORT_CREATE_FORM = "createReportForm";

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/create")
    public ModelAndView createReportForm(@ModelAttribute("createReportForm") CreateReportForm form) {
        return new ModelAndView(REPORT_CREATE);
    }

    @PostMapping("/create")
    public ModelAndView createReport(@ModelAttribute("user") User user,
                                     @Valid @ModelAttribute("createReportForm") CreateReportForm form,
                                     BindingResult errors) {
        if (errors.hasErrors()) {
            return createReportForm(form);
        }

        reportService.createReport(user, form.getReportType(), form.getTargetId(), form.getReason(), form.getDescription());
        return new ModelAndView("redirect:" + getRedirectUrl(form));
    }

    private String getRedirectUrl(CreateReportForm form) {
        return switch (form.getReportType()) {
            case "JOURNEY" -> "/journeys/" + form.getTargetId();
            case "EVENT" -> "/events/" + form.getTargetId();
            case "JOURNEY_RESPONSE" -> "/journeys/";
            case "EVENT_RESPONSE" -> "/events/";
            default -> "/";
        };
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
package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import ar.edu.itba.paw.webapp.form.CreateReportForm;
import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
import ar.edu.itba.paw.webapp.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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



    @GetMapping(value = "/create")
    public ModelAndView createReportForm(@ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form) {
        return new ModelAndView(REPORT_CREATE);
    }

//    @PostMapping(path = "/create")
//    public ModelAndView createCities(@Valid @ModelAttribute(REPORT_CREATE_FORM) final CreateReportForm form,
//                                     final BindingResult errors, @ModelAttribute("user") User user) {
//
//        if (errors.hasErrors()) {
//            return createReportForm(form);
//        }
//        Report report = reportService.createReport(
//
//        );
//
//        return new ModelAndView("redirect:/reports/{id}", "id", report.getId());
//    }


    @GetMapping(value= "/{id}")
    public ModelAndView getReport(@PathVariable(value = "id") final long id) {
        Report report = reportService.findById(id).orElseThrow(() -> {
            LOGGER.error("Report not found");
            return new NotFoundException("Report not found");
        });
        ModelAndView mav = new ModelAndView(REPORT_DETAIL);
        mav.addObject("report", report);
        return mav;
    }



    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteReport(@PathVariable long id) {
        reportService.deleteById(id);
        return new ModelAndView("redirect:/dashboard/reports");
    }


}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;

import ar.edu.itba.paw.models.Career;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.*;
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

import java.util.NoSuchElementException;


@Controller
@RequestMapping("/careers")
public class CareerController {
    private final CareerService careerService;
    private static final String CAREER_DASHBOARD = "/dashboard/careers";
    private static final String CAREER_CREATE = "/careers/create";
    private static final String CAREER_DETAIL = "/careers/detail";


    @Autowired
    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @GetMapping( produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getCareersJSON(@RequestParam(value = "search", required = false) String search,
                                 @PageParamCustomizer(defaultSize = 30) PageParams pageParams) {
        return JsonUtils.toJson( careerService.getAllCareers(search, pageParams).getContent());
    }


    @GetMapping(value = "/create")
    public ModelAndView createCareersForm(@ModelAttribute("createCareerForm") final CreateCareerForm form) {
        return new ModelAndView(CAREER_CREATE);
    }

    @PostMapping(path = "/create")
    public ModelAndView createCareer(@Valid @ModelAttribute("createCareerForm") final CreateCareerForm careerForm,
                                     final BindingResult errors) {
        if (errors.hasErrors()) {
            return createCareersForm(careerForm);
        }
        Career career = careerService.create(careerForm.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", career.getId());
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getCareers(@PathVariable(value = "id") final long id) {
        Career career = careerService.findById(id).orElseThrow(() -> new NotFoundException("Career not found"));
        ModelAndView mav = new ModelAndView(CAREER_DETAIL);
        mav.addObject("career", career);
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateCareerForm(@PathVariable("id") Long id, @ModelAttribute("createCareerForm") final CreateCareerForm form,
                                         final BindingResult errors) {

        // Create and populate form with existing university data
        if(! errors.hasErrors()) {
            Career career = careerService.findById(id).orElseThrow(() -> new NotFoundException("Career not found"));
            form.setName(career.getName());
        }
        ModelAndView mav = new ModelAndView(CAREER_CREATE);
        mav.addObject("isUpdate", true);
        mav.addObject("careerId", id);
        return mav;
    }

    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateCareer(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute("createCareerForm") final CreateCareerForm form,
                                         final BindingResult errors,
                                         @ModelAttribute("user") User user) {
        if (errors.hasErrors()) {
            return createCareersForm(form);
        }

        careerService.update(id,form.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", id);
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteCareer(@PathVariable long id) {
        careerService.delete(id);
        return new ModelAndView("redirect:" + CAREER_DASHBOARD);
    }

}

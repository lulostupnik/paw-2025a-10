package ar.edu.itba.paw.webapp.controller.mvc;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.paging.PageParamCustomizer;
import ar.edu.itba.paw.webapp.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

@Deprecated

//@Controller
//@RequestMapping("/careers")
public class CareerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerController.class);
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
        return JsonUtils.toJson( careerService.searchCareers(search, pageParams).getContent());
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
        Career career = careerService.createCareer(careerForm.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", career.getId());
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getCareers(@PathVariable(value = "id") final long id) {
        Career career = careerService.findCareerById(id).orElseThrow(() -> {
            LOGGER.error("Career not found for id: {}", id);
            return new CareerNotFoundException(id);}
        );
        ModelAndView mav = new ModelAndView(CAREER_DETAIL);
        mav.addObject("career", career);
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateCareerForm(@PathVariable("id") Long id, @ModelAttribute("createCareerForm") final CreateCareerForm form,
                                         final BindingResult errors) {


        if(! errors.hasErrors()) {
            Career career = careerService.findCareerById(id).orElseThrow(() -> new CareerNotFoundException(id));
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

        careerService.updateCareer(id,form.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", id);
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteCareer(@PathVariable long id) {
        careerService.deleteCareer(id);
        return new ModelAndView("redirect:" + CAREER_DASHBOARD);
    }

}

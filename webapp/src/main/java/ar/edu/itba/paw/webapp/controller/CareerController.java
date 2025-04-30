package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Career;

import ar.edu.itba.paw.webapp.form.CreateCareerForm;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/careers")
public class CareerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerController.class);
    private final CityService cityService;
    private final UniversityService universityService;

    public CareerController(CityService cityService, UniversityService universityService) {
        this.cityService = cityService;
        this.universityService = universityService;
    }


    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createCareersForm(@ModelAttribute("createCityForm") final CreateCareerForm form) {
        return new ModelAndView("careers/create");
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createCareer(@Valid @ModelAttribute("createCareerForm") final CreateCareerForm careerForm,
                                     final BindingResult errors, @ModelAttribute("username") String username) {

        if (errors.hasErrors()) {
            return createCareersForm(careerForm);
        }

        return new ModelAndView("redirect:/careers/{id}", "id", 1);
    }
    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getCareers(@ModelAttribute("career") final Career career) {
        ModelAndView mav = new ModelAndView("careers/detail");
        mav.addObject("career", career);
        return mav;
    }


}

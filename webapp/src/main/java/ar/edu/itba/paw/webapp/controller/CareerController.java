package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.NoSuchElementException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/careers")
public class CareerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerController.class);
    private final CityService cityService;
    private final UniversityService universityService;
    private final CareerService careerService;

    public CareerController(CityService cityService, UniversityService universityService, CareerService careerService) {
        this.cityService = cityService;
        this.universityService = universityService;
        this.careerService = careerService;
    }


    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createCareersForm(@ModelAttribute("createCareerForm") final CreateCareerForm form) {
        return new ModelAndView("careers/create");
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createCareer(@Valid @ModelAttribute("createCareerForm") final CreateCareerForm careerForm,
                                     final BindingResult errors) {
        if (errors.hasErrors()) {
            return createCareersForm(careerForm);
        }
        Career career = careerService.create(careerForm.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", career.getId());
    }
    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getCareers(@PathVariable(value = "id") final long id) {
        Career career = careerService.findById(id).orElseThrow(() -> new NoSuchElementException("Career not found"));
        ModelAndView mav = new ModelAndView("careers/detail");
        mav.addObject("career", career);
        return mav;
    }


}

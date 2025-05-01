package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.User;
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
@RequestMapping("/cities")
public class CityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
    private final CityService cityService;
    private final UniversityService universityService;

    public CityController(CityService cityService, UniversityService universityService) {
        this.cityService = cityService;
        this.universityService = universityService;
    }


    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createCitiesForm(@ModelAttribute("createCityForm") final CreateCityForm form) {
        return new ModelAndView("cities/create");
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createCities(@Valid @ModelAttribute("createInterestForm") final CreateCityForm cityForm,
                                        final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createCitiesForm(cityForm);
        }

        return new ModelAndView("redirect:/cities/{id}", "id", 1);
    }
    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getCities(@ModelAttribute("city") final City city) {
        ModelAndView mav = new ModelAndView("cities/detail");
        mav.addObject("city", city);
        return mav;
    }


}

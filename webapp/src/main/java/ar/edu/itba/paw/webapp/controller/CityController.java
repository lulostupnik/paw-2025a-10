package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateCareerForm;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.NoSuchElementException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/cities")
public class CityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
    private final CityService cityService;
    private final UniversityService universityService;
    private final CountryService countryService;

    public CityController(CityService cityService, UniversityService universityService, CountryService countryService) {
        this.cityService = cityService;
        this.universityService = universityService;
        this.countryService = countryService;
    }

    @RequestMapping(value = "", method = GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getCitiesJson(@RequestParam(value = "search", required = false) String search) {
        return cityService.getCitiesJson(search);
    }

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createCitiesForm(@ModelAttribute("createCityForm") final CreateCityForm form) {
        return new ModelAndView("cities/create").addObject(countryService.getAllCountries());
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createCities(@Valid @ModelAttribute("createCityForm") final CreateCityForm cityForm,
                                        final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createCitiesForm(cityForm);
        }

        return new ModelAndView("redirect:/cities/{id}", "id", 1);
    }
    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getCity(@PathVariable(value = "id") final long id) {
        City city = cityService.findById(id).orElseThrow(NoSuchElementException::new);
        ModelAndView mav = new ModelAndView("cities/detail");
        mav.addObject("city", city);
        return mav;
    }

    @RequestMapping(value = "/{id}/edit", method = GET)
    public ModelAndView updateCityForm(@PathVariable("id") Long id) {
        City city = cityService.findById(id).get();
        if (city == null) {
            return new ModelAndView("redirect:/cities");
        }
        CreateCityForm form = new CreateCityForm();
        form.setName(city.getName());
        form.setCountry(city.getCountry());

        ModelAndView mav = new ModelAndView("cities/create");
        mav.addObject("createCityForm", form);
        mav.addObject("isUpdate", true);
        mav.addObject("cityId", id);
        mav.addObject("country", countryService.getAllCountries());
        return mav;
    }

    @RequestMapping(value = "/{id}/edit", method = POST)
    public ModelAndView updateCity(@PathVariable("id") Long id,
                                     @Valid @ModelAttribute("createCityForm") final CreateCityForm form,
                                     final BindingResult errors,
                                     @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("cities/create");
            mav.addObject("isUpdate", true);
            mav.addObject("cityId", id);
            return mav;
        }

        cityService.updateCity(id,
                form.getName(),
                form.getCountry()
        );

        return new ModelAndView("redirect:/cities/{id}", "id", id);
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteCity(@PathVariable long id) {
        cityService.delete(id);
        return new ModelAndView("redirect:/dashboard/cities");
    }



}

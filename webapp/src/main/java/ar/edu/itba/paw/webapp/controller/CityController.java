package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequestMapping("/cities")
public class CityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
    private final CityService cityService;
    private final CountryService countryService;
    private static final String CREATE_CITY = "cities/create";
    private static final String CITY_DETAIL = "cities/detail";
    private static final String CITY_CREATE_FORM = "createCityForm";

    public CityController(CityService cityService, CountryService countryService) {
        this.cityService = cityService;
        this.countryService = countryService;
    }


    @GetMapping(value = "/create")
    public ModelAndView createCitiesForm(@ModelAttribute(CITY_CREATE_FORM) final CreateCityForm form) {
        return new ModelAndView(CREATE_CITY).addObject("countries",countryService.getAllCountries());
    }

    @PostMapping(path = "/create")
    public ModelAndView createCities(@Valid @ModelAttribute(CITY_CREATE_FORM) final CreateCityForm cityForm,
                                        final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createCitiesForm(cityForm);
        }
        long cityId = cityService.createCity(
                cityForm.getName(),
                cityForm.getCountry()
        );

        return new ModelAndView("redirect:/cities/{id}", "id", cityId);
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getCity(@PathVariable(value = "id") final long id) {
        City city = cityService.findById(id).orElseThrow(NoSuchElementException::new);
        ModelAndView mav = new ModelAndView(CITY_DETAIL);
        mav.addObject("city", city);
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateCityForm(@PathVariable("id") Long id) {
        Optional<City> optionalCity = cityService.findById(id);

        if (optionalCity.isEmpty()) {
            return new ModelAndView("redirect:/cities");
        }

        City city = optionalCity.get();
        CreateCityForm form = new CreateCityForm();
        form.setName(city.getName());
        form.setCountry(city.getCountry());

        ModelAndView mav = new ModelAndView(CREATE_CITY);
        mav.addObject(CITY_CREATE_FORM, form);
        mav.addObject("isUpdate", true);
        mav.addObject("cityId", id);
        mav.addObject("countries", countryService.getAllCountries());
        return mav;
    }


    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateCity(@PathVariable("id") Long id,
                                     @Valid @ModelAttribute(CITY_CREATE_FORM) final CreateCityForm form,
                                     final BindingResult errors,
                                     @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView(CREATE_CITY);
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

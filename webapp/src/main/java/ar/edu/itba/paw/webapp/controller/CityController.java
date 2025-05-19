package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
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
@RequestMapping("/cities")
public class CityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
    private final CityService cityService;
    private final CountryService countryService;
    private static final String CREATE_CITY = "cities/create";
    private static final String CITY_DETAIL = "cities/detail";
    private static final String CITY_CREATE_FORM = "createCityForm";

    @Autowired
    public CityController(CityService cityService, CountryService countryService) {
        this.cityService = cityService;
        this.countryService = countryService;
    }

    @GetMapping(value = "", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getCitiesJson(@RequestParam(value = "search", required = false) String search,
                                @PageParamCustomizer(defaultSize = 30) PageParams pageParams) {
        return JsonUtils.toJson( cityService.searchCities(search, pageParams).getContent());
    }

    @GetMapping(value = "/create")
    public ModelAndView createCitiesForm(@ModelAttribute(CITY_CREATE_FORM) final CreateCityForm form) {
        return new ModelAndView(CREATE_CITY).addObject("countries",countryService.findCountries());
    }

    @PostMapping(path = "/create")
    public ModelAndView createCities(@Valid @ModelAttribute(CITY_CREATE_FORM) final CreateCityForm cityForm,
                                        final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createCitiesForm(cityForm);
        }
        City city = cityService.createCity(
                cityForm.getName(),
                cityForm.getCountry()
        );

        return new ModelAndView("redirect:/cities/{id}", "id", city.getId());
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getCity(@PathVariable(value = "id") final long id) {
        City city = cityService.findCityById(id).orElseThrow(() -> {
            LOGGER.error("City not found");
            return new NotFoundException("City not found");
        });
        ModelAndView mav = new ModelAndView(CITY_DETAIL);
        mav.addObject("city", city);
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateCityForm(@PathVariable("id") Long id,
                                       @ModelAttribute(CITY_CREATE_FORM) final CreateCityForm form, BindingResult errors) {
        City city = cityService.findCityById(id).orElseThrow(() -> {
            LOGGER.error("City not found");
            return new NotFoundException("City not found");
        });
        if(!errors.hasErrors()){
            form.setName(city.getName());
            form.setCountry(city.getCountry().getName()); // fixme: estaría mejor que le llegue un Country en vez del nombre del Country
        }

        ModelAndView mav = new ModelAndView(CREATE_CITY);
        mav.addObject("isUpdate", true);
        mav.addObject("cityId", id);
        mav.addObject("countries", countryService.findCountries());
        return mav;
    }


    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateCity(@PathVariable("id") Long id,
                                     @Valid @ModelAttribute(CITY_CREATE_FORM) final CreateCityForm form,
                                     final BindingResult errors) {

        if (errors.hasErrors()) {
            return updateCityForm(id, form, errors);
        }

        cityService.updateCity(id,
                form.getName(),
                form.getCountry()
        );

        return new ModelAndView("redirect:/cities/{id}", "id", id);
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteCity(@PathVariable long id) {
        cityService.deleteCity(id);
        return new ModelAndView("redirect:/dashboard/cities");
    }



}

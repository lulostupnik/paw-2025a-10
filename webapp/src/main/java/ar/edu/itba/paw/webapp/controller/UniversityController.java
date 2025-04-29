package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/universities")
public class UniversityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityController.class);
    private final CityService cityService;
    private final UniversityService universityService;

    public UniversityController(CityService cityService, UniversityService universityService) {
        this.cityService = cityService;
        this.universityService = universityService;
    }


    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createUniversityForm(@ModelAttribute("createUniversityForm") final CreateUniversityForm form) {
        ModelAndView mav = new ModelAndView("universities/create");
        mav.addObject("cities", cityService.getAllCities());
        return mav;
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createEvent(@Valid @ModelAttribute("createUniversityForm") final CreateUniversityForm uniForm,
                                    final BindingResult errors, @ModelAttribute("username") String username) {

        if (errors.hasErrors()) {
            return createUniversityForm(uniForm);
        }

        University uni = universityService.createUniversity(
                uniForm.getName(),
                uniForm.getAbbreviation(),
                uniForm.getCityName()
        );
        return new ModelAndView("redirect:/universities/{id}", "id", uni.getId());
    }

}

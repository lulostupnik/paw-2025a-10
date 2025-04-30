package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
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
@RequestMapping("/interests")
public class InterestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestController.class);
    private final CityService cityService;
    private final UniversityService universityService;

    public InterestController(CityService cityService, UniversityService universityService) {
        this.cityService = cityService;
        this.universityService = universityService;
    }


    @RequestMapping(value = "/create", method = GET)
    public ModelAndView createInterestsForm(@ModelAttribute("createInterestsForm") final CreateInterestForm form) {
        return new ModelAndView("interests/create");
    }

    @RequestMapping(path = "/create", method = POST)
    public ModelAndView createInterests(@Valid @ModelAttribute("createInterestForm") final CreateInterestForm intForm,
                                    final BindingResult errors, @ModelAttribute("username") String username) {

        if (errors.hasErrors()) {
            return createInterestsForm(intForm);
        }

        return new ModelAndView("redirect:/interests/{id}", "id", 1);
    }
    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getInterests(@ModelAttribute("interest") final Interest interest) {
        ModelAndView mav = new ModelAndView("interests/detail");
        mav.addObject("interest", interest);
        return mav;
    }

}

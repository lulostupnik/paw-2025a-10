package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.Optional;

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
                                    final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createUniversityForm(uniForm);
        }

        University university = universityService.createUniversity(
                uniForm.getName(),
                uniForm.getAbbreviation(),
                uniForm.getCity()
        );

        LOGGER.info("Created university: {}", university.getName());
        return new ModelAndView("redirect:/universities/{id}", "id", university.getId());
    }

    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getUniversity(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("universities/detail");
        mav.addObject("university", universityService.findById(id));
        return mav;
    }

    @RequestMapping(value = "/{id}/edit", method = GET)
    public ModelAndView updateUniversityForm(@PathVariable("id") Long id) {
        University university = universityService.findById(id).get();
        if (university == null) {
            return new ModelAndView("redirect:/universities");
        }

        // Create and populate form with existing university data
        CreateUniversityForm form = new CreateUniversityForm();
        form.setName(university.getName());
        form.setAbbreviation(university.getAbbreviation());
        form.setCity(university.getCity().getName());

        ModelAndView mav = new ModelAndView("universities/create");
        mav.addObject("createUniversityForm", form);
        mav.addObject("cities", cityService.getAllCities());
        mav.addObject("isUpdate", true);
        mav.addObject("universityId", id);
        return mav;
    }

    @RequestMapping(value = "/{id}/edit", method = POST)
    public ModelAndView updateUniversity(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute("createUniversityForm") final CreateUniversityForm form,
                                         final BindingResult errors,
                                         @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("universities/create");
            mav.addObject("cities", cityService.getAllCities());
            mav.addObject("isUpdate", true);
            mav.addObject("universityId", id);
            return mav;
        }

        universityService.updateUniversity(
                id,
                form.getName(),
                form.getAbbreviation(),
                form.getCity()
        );

        return new ModelAndView("redirect:/universities/{id}", "id", id);
    }

    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteUniversity(@PathVariable long id, @Valid @ModelAttribute("deleteForm") final ReplyForm form,
                                   final BindingResult errors, final RedirectAttributes redirectAttributes) {

        if(errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            redirectAttributes.addFlashAttribute("deleteErrors", errors);
            redirectAttributes.addFlashAttribute("deleteForm", form);
            return new ModelAndView("redirect:/universities/{id}","id", id);
        }
        return new ModelAndView("redirect:/universities/cities");
    }
}
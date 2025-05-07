package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;

import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/universities")
public class UniversityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityController.class);
    private final CityService cityService;
    private final UniversityService universityService;
    private static final String CREATE_UNIVERSITY_FORM = "createUniversityForm";
    private static final String CITIES = "cities";
    private static final String IS_UPDATE = "isUpdate";
    private static final String UNIVERSITY_ID = "universityId";
    private static final String UNIVERSITY = "university";
    private static final String DETAIL = "universities/detail";
    private static final String CREATE = "universities/create";

    public UniversityController(CityService cityService, UniversityService universityService) {
        this.cityService = cityService;
        this.universityService = universityService;
    }

    @GetMapping(value = "/create")
    public ModelAndView createUniversityForm(@ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm form) {
        ModelAndView mav = new ModelAndView(CREATE);
        mav.addObject(CITIES, cityService.getAllCities());
        return mav;
    }

    @PostMapping(path = "/create")
    public ModelAndView createEvent(@Valid @ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm uniForm,
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

    @GetMapping(value= "/{id}")
    public ModelAndView getUniversity(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView(DETAIL);
        mav.addObject(UNIVERSITY, universityService.findById(id).orElseThrow(
                () -> new IllegalArgumentException("University not found")));
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateUniversityForm(@PathVariable("id") Long id) {
        Optional<University> university = universityService.findById(id);
        if (university.isEmpty()) {
            return new ModelAndView("redirect:/dashboard/universities");
        }
        University newUni = university.get();
        // Create and populate form with existing university data
        CreateUniversityForm form = new CreateUniversityForm();
        form.setName(newUni.getName());
        form.setAbbreviation(newUni.getAbbreviation());
        form.setCity(newUni.getCity().getName());

        ModelAndView mav = new ModelAndView(CREATE);
        mav.addObject(CREATE_UNIVERSITY_FORM, form);
        mav.addObject(CITIES, cityService.getAllCities());
        mav.addObject(IS_UPDATE, true);
        mav.addObject(UNIVERSITY_ID, id);
        return mav;
    }

    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateUniversity(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm form,
                                         final BindingResult errors,
                                         @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView(CREATE);
            mav.addObject(CITIES, cityService.getAllCities());
            mav.addObject(IS_UPDATE, true);
            mav.addObject(UNIVERSITY_ID, id);
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
    public ModelAndView deleteUniversity(@PathVariable long id) {
        universityService.delete(id);
        return new ModelAndView("redirect:/dashboard/universities");
    }
}
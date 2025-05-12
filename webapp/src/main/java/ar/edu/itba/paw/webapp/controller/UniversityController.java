package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
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
import java.util.Optional;


import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
@Controller
@RequestMapping("/universities")
public class UniversityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityController.class);
    private final UniversityService universityService;
    private static final String CREATE_UNIVERSITY_FORM = "createUniversityForm";
    private static final String IS_UPDATE = "isUpdate";
    private static final String UNIVERSITY_ID = "universityId";
    private static final String UNIVERSITY = "university";
    private static final String DETAIL = "universities/detail";
    private static final String CREATE = "universities/create";

    @Autowired
    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }
    @GetMapping(produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getUniversitiesJSON(@RequestParam(value = "search", required = false) String search,
                                     @PageParamCustomizer(defaultSize = 30) PageParams pageParams) {

        return JsonUtils.toJson( universityService.getAllUniversities(search, pageParams).getContent());
    }

    @GetMapping(value = "/create")
    public ModelAndView createUniversityForm(@ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm form) {
        return new ModelAndView(CREATE);
    }

    @PostMapping(path = "/create")
    public ModelAndView createUniversity(@Valid @ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm uniForm,
                                    final BindingResult errors, @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createUniversityForm(uniForm);
        }

        University university = universityService.createUniversity(
                uniForm.getName(),
                uniForm.getAbbreviation(),
                uniForm.getCity()
        );

        return new ModelAndView("redirect:/universities/{id}", "id", university.getId());
    }

    @GetMapping(value= "/{id}")
    public ModelAndView getUniversity(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView(DETAIL);
        mav.addObject(UNIVERSITY, universityService.findById(id).orElseThrow(
                () -> {
                    LOGGER.error("University not found");
                    return new NotFoundException("University not found");}
        ));
        return mav;
    }

    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateUniversityForm(@PathVariable("id") Long id, @ModelAttribute(CREATE) CreateUniversityForm form, BindingResult errors) {

        // Create and populate form with existing university data
        if(errors.hasErrors()) {
            University university = universityService.findById(id).orElseThrow(()-> {
                LOGGER.error("University not found");
                return new NotFoundException("University not found");});
            form.setName(university.getName());
            form.setAbbreviation(university.getAbbreviation());
            form.setCity(university.getCity().getName());
        }


        ModelAndView mav = new ModelAndView(CREATE);
        mav.addObject(IS_UPDATE, true);
        mav.addObject(UNIVERSITY_ID, id);
        return mav;
    }

    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateUniversity(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute(CREATE_UNIVERSITY_FORM) final CreateUniversityForm form,
                                         final BindingResult errors) {

        if (errors.hasErrors()) {
          return updateUniversityForm(id,form, errors);
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
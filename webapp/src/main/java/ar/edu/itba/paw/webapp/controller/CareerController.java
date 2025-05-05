package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Career;

import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.*;
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
    @RequestMapping(value = "", method = GET)
    @ResponseBody
    public String getCareersJSON(@RequestParam(value = "search", required = false) String search) {
        return careerService.getCareersJSON(search);
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

    @RequestMapping(value = "/{id}/edit", method = GET)
    public ModelAndView updateCareerForm(@PathVariable("id") Long id) {
        Career career = careerService.findById(id).get();
        if (career == null) {
            return new ModelAndView("redirect:/careers");
        }

        // Create and populate form with existing university data
        CreateCareerForm form = new CreateCareerForm();
        form.setName(career.getName());

        ModelAndView mav = new ModelAndView("careers/create");
        mav.addObject("createCareerForm", form);
        mav.addObject("isUpdate", true);
        mav.addObject("careerId", id);
        return mav;
    }

    @RequestMapping(value = "/{id}/edit", method = POST)
    public ModelAndView updateCareer(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute("createCareerForm") final CreateCareerForm form,
                                         final BindingResult errors,
                                         @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("careers/create");
            mav.addObject("isUpdate", true);
            mav.addObject("careerId", id);
            return mav;
        }

        careerService.update(id,form.getName());

        return new ModelAndView("redirect:/careers/{id}", "id", id);
    }


    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteCareer(@PathVariable long id) {
        careerService.delete(id);
        return new ModelAndView("redirect:/dashboard/careers");
    }

}

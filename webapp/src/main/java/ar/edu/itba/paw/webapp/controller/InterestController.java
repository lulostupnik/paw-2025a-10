package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.NoSuchElementException;


@Controller
@RequestMapping("/interests")
public class InterestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestController.class);

    private final InterestService interestService;

    public InterestController(InterestService interestService) {

        this.interestService = interestService;
    }


    @GetMapping(value = "/create")
    public ModelAndView createInterestsForm(@ModelAttribute("createInterestForm") final CreateInterestForm form) {
        return new ModelAndView("interests/create");
    }

    @PostMapping(path = "/create")
    public ModelAndView createInterests(@Valid @ModelAttribute("createInterestForm") final CreateInterestForm intForm,
                                    final BindingResult errors,@ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
            return createInterestsForm(intForm);
        }
        Interest interest = interestService.createUserInterest(intForm.getName());
        return new ModelAndView("redirect:/interests/{id}", "id", interest.getId());
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getInterests(@PathVariable(value = "id") final long id) {
        Interest interest = interestService.findById(id).orElseThrow(NoSuchElementException::new);
        ModelAndView mav = new ModelAndView("interests/detail");
        mav.addObject("interest", interest);
        return mav;
    }


    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateInterestForm(@PathVariable("id") Long id,
                                            @ModelAttribute("createInterestForm") final CreateInterestForm form,
                                           BindingResult errors ) {
        Interest interest = interestService.findById(id).orElseThrow(NoSuchElementException::new);
        if (interest == null) {
            return new ModelAndView("redirect:/interests");
        }

        if(!errors.hasErrors()){
            form.setName(interest.getName());
        }

        ModelAndView mav = new ModelAndView("interests/create");
        mav.addObject("createInterestForm",form);
        mav.addObject("isUpdate", true);
        mav.addObject("interestId", id);
        return mav;
    }


    @PostMapping(value = "/{id}/edit")
    public ModelAndView updateInterest(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute("createInterestForm") final CreateInterestForm form,
                                         final BindingResult errors,
                                         @ModelAttribute("user") User user) {

        if (errors.hasErrors()) {
          return updateInterestForm(id, form, errors);
        }

        interestService.editUserInterest(
                id,
                form.getName()
        );

        return new ModelAndView("redirect:/interests/{id}", "id", id);
    }

    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteInterest(@PathVariable long id) {
        interestService.delete(id);
        return new ModelAndView("redirect:/dashboard/interests");
    }

}

package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import ar.edu.itba.paw.webapp.form.EditInterestForm;
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
@RequestMapping("/interests")
public class InterestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestController.class);

    private final InterestService interestService;

    @Autowired
    public InterestController(InterestService interestService) {
        this.interestService = interestService;
    }

    @GetMapping(value = "", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getInterestsJSON(@RequestParam(value = "search", required = false) String search,
                                   @PageParamCustomizer(defaultSize = 30) PageParams pageParams) {
        return JsonUtils.toJson( interestService.findInterests(search, pageParams).getContent());
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
        Interest interest = interestService.createInterest(intForm.getName());
        return new ModelAndView("redirect:/interests/{id}", "id", interest.getId());
    }
    @GetMapping(value= "/{id}")
    public ModelAndView getInterests(@PathVariable(value = "id") final long id) {
        Interest interest = interestService.findInterestById(id).orElseThrow(() -> {
            LOGGER.error("Interest not found for id: {}", id);
            return new NotFoundException("Interest not found");});
        ModelAndView mav = new ModelAndView("interests/detail");
        mav.addObject("interest", interest);
        return mav;
    }


    @GetMapping(value = "/{id}/edit")
    public ModelAndView updateInterestForm(@PathVariable("id") Long id,
                                            @ModelAttribute("createInterestForm") final CreateInterestForm form,
                                           BindingResult errors ) {
        Interest interest = interestService.findInterestById(id).orElseThrow(() -> {
            LOGGER.error("Interest not found for id: {}", id);
            return new NotFoundException("Interest not found");});
        if(!errors.hasErrors()){
            form.setName(interest.getName());
        }

        ModelAndView mav = new ModelAndView("interests/create");
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

        interestService.updateInterest(
                id,
                form.getName()
        );

        return new ModelAndView("redirect:/interests/{id}", "id", id);
    }

    @PostMapping(value = "/{id}/delete")
    public ModelAndView deleteInterest(@PathVariable long id) {
        interestService.deleteInterest(id);
        return new ModelAndView("redirect:/dashboard/interests");
    }

    @GetMapping(value = "/edit")
    public ModelAndView updateInterestForm( @ModelAttribute("user") User user,
                                            @ModelAttribute("editInterestsForm") final EditInterestForm form) {
        Page<UserInterest> pagedInterests = interestService.findInterestsByUser(user, new PageParams(1, 20));
        ModelAndView mav = new ModelAndView("interests/interests-edit");
        mav.addObject("editInterestsForm",form);
        mav.addObject("userInterests", pagedInterests.getContent());
        return mav;
    }


    @PostMapping(value = "/edit")
    public ModelAndView updateInterest(@ModelAttribute("user") User user,
                                       @Valid @ModelAttribute("editInterestsForm") final EditInterestForm form,
                                       final BindingResult errors) {
        if (errors.hasErrors()) {
            return updateInterestForm(user, form);
        }
        interestService.updateUserInterests(form.getInterests(), user.getId());
        return new ModelAndView("redirect:/profile/interests");
    }

}

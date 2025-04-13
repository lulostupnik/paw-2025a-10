package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateEventForm;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AuthController {
    private  final UniversityService universityService;
    private final CareerService careerService;
    private final UserService userService;
    private final InterestService interestService;

    @Autowired
    public AuthController(final UniversityService universityService, final CareerService carreerService, UserService userService, InterestService interestService) {
        this.universityService = universityService;
        this.careerService = carreerService;
        this.userService = userService;
        this.interestService = interestService;
    }
    @RequestMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("auth/login");
    }

    @RequestMapping(value = "/register", method = {RequestMethod.GET})
    public ModelAndView registerForm(@ModelAttribute ("createUserForm") final CreateUserForm form) {
        ModelAndView mav = new ModelAndView("auth/register");
        mav.addObject("careers", careerService.findAll());
        mav.addObject("universities",  universityService.getAllUniversities());
        mav.addObject("interests", interestService.findAll() );
        return mav;
    }


    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public ModelAndView registerSubmit(@ModelAttribute("createUserForm") final CreateUserForm form, final BindingResult errors) {
        if (errors.hasErrors()) {
            return registerForm(form);
        }

        byte[] profilePicture = null;
        try {
            profilePicture = form.getProfilePicture().getBytes();
        } catch (Exception e) {
            // FIXME
        }

        //FIXME: Add fields for user creation just in case it does not exist. This will be removed after 1st sprint when we implement authorization
        final User user = userService.createUser(form.getEmail(), form.getUsername(), form.getFirstName(),
                form.getLastName(), form.getOriginUniversity(), form.getCareer(), profilePicture, form.getInterests(), form.getPassword());

        return new ModelAndView("redirect:login");
    }
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.form.CreateUserForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UniversityService universityService;
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
        LOGGER.debug("Loading login form");
        return new ModelAndView("auth/login");
    }

    @RequestMapping(value = "/register", method = {RequestMethod.GET})
    public ModelAndView registerForm(@ModelAttribute ("createUserForm") final CreateUserForm form) {

        LOGGER.debug("Loading register form");
        ModelAndView mav = new ModelAndView("auth/register");
        mav.addObject("careers", careerService.findAll());
        mav.addObject("universities",  universityService.getAllUniversities());
        mav.addObject("interests", interestService.findAll());
        return mav;
    }


    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public ModelAndView registerSubmit(@ModelAttribute("createUserForm") final CreateUserForm form, final BindingResult errors) {

        Locale currentLocale = LocaleContextHolder.getLocale();

        LOGGER.info("CREATING USER FROM USERFORM {}", form);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            return registerForm(form);
        }

        byte[] profilePicture = null;
        try {
            profilePicture = form.getProfilePicture().getBytes();
            LOGGER.debug("User picture loaded successfully");
        } catch (Exception e) {
            LOGGER.error("Error getting submitted image: {}", e.getMessage());
        }

        final User user = userService.createUser(form.getEmail(), form.getUsername(), form.getFirstName(),
                form.getLastName(), form.getOriginUniversity(), form.getCareer(), profilePicture, form.getInterests(), form.getPassword(), currentLocale);
        LOGGER.info("Successfully created user {}", user);

        return new ModelAndView("redirect:login");
    }
}

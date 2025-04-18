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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.List;
import java.util.Locale;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UniversityService universityService;
    private final CareerService careerService;
    private final UserService userService;
    private final InterestService interestService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(final UniversityService universityService, final CareerService carreerService, UserService userService,
                          InterestService interestService, final AuthenticationManager authenticationManager) {
        this.universityService = universityService;
        this.careerService = carreerService;
        this.userService = userService;
        this.interestService = interestService;
        this.authenticationManager = authenticationManager;
    }
    @RequestMapping("/login")
    public ModelAndView loginForm(Authentication authentication) {
        LOGGER.debug("Loading login form");
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return new ModelAndView("redirect:/explore");

        }

        return new ModelAndView("auth/login");
    }
    @RequestMapping("/")
    public ModelAndView landing() {
        LOGGER.debug("Loading landing page");
        return new ModelAndView("index");
    }
    @RequestMapping(value = "/register", method = {RequestMethod.GET})
    public ModelAndView registerForm(@ModelAttribute ("createUserForm") final CreateUserForm form) {
        LOGGER.debug("Loading register form");
        ModelAndView mav = new ModelAndView("auth/register");
        List<Career> careers = careerService.findAll();
        LOGGER.debug("Found careers {}", careers);
        mav.addObject("careers", careers);

        List<University> universities = universityService.getAllUniversities();
        LOGGER.debug("Found universities {}", universities);
        mav.addObject("universities",  universities);

        List<Interest> interests = interestService.findAll();
        LOGGER.debug("Found interests {}", interests);
        mav.addObject("interests", interests);
        return mav;
    }


    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public ModelAndView registerSubmit(@Valid @ModelAttribute("createUserForm") final CreateUserForm form, final BindingResult errors) {

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

        userService.createUser(form.getEmail(), form.getUsername(), form.getFirstName(),
                form.getLastName(), form.getOriginUniversity(), form.getCareer(), profilePicture, form.getInterests(), form.getPassword(), currentLocale);
        Authentication authToken = new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return new ModelAndView("redirect:login");
    }
}

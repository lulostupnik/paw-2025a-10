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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.List;
import java.util.UUID;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;

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
    @PostMapping(value ="/validate")
    public ModelAndView validateEmail(@RequestParam("token") String token) {
        userService.validateEmail(token);
        return new ModelAndView("redirect:/explore");
    }

    @RequestMapping("/login")
    public ModelAndView loginForm( @ModelAttribute("user") User user) {
        LOGGER.debug("Loading login form");
        if (user != null) {
            return new ModelAndView("redirect:/explore");
        }
        return new ModelAndView("auth/login");
    }
    @RequestMapping("/blocked")
    public ModelAndView blockedForm() {
        LOGGER.debug("Loading blocked view");
        ModelAndView mav = new ModelAndView("auth/blocked-user");
        mav.addObject("email", "paw.2025a.10@gmail.com" );
        return mav;
    }

    @GetMapping(value = "/register")
    public ModelAndView registerForm(@ModelAttribute ("createUserForm") final CreateUserForm form) {
        LOGGER.debug("Loading register form");
        return new ModelAndView("auth/register");
    }

    @PostMapping(value = "/register")
    public ModelAndView registerSubmit(@Valid @ModelAttribute("createUserForm") final CreateUserForm form, final BindingResult errors) {

        LOGGER.info("CREATING USER FROM USERFORM {}", form);
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in form data", errors.getErrorCount());
            LOGGER.debug("Errors: {}", errors);
            return registerForm(form);
        }

        byte[] profilePicture = getBytes(form.getProfilePicture());

        userService.createUser(form.getEmail(), form.getUsername(), form.getFirstName(),
                form.getLastName(), form.getOriginUniversity(), form.getCareer(), profilePicture,
                form.getInterests(), form.getPassword(), LocaleContextHolder.getLocale());

        setAuth(form.getEmail(), form.getPassword());

        return new ModelAndView("redirect:explore");
    }

    private void setAuth(String email, String password) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

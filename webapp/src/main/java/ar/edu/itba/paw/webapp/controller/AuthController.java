package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.LoginHelper;
import ar.edu.itba.paw.webapp.form.CreateUserForm;

import ar.edu.itba.paw.webapp.form.EmailForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final LoginHelper loginHelper;

    @Autowired
    public AuthController(final UserService userService, final LoginHelper loginHelper) {
        this.userService = userService;
        this.loginHelper = loginHelper;
    }
    @GetMapping(value ="/validate")
    public ModelAndView validateEmail(@RequestParam("token") String token) {
        Optional<UserAuthInfo> user = userService.validateEmail(token);
        if(user.isEmpty()) { //@TODO: exception
            LOGGER.debug("User not found or token expired");
            return new ModelAndView("redirect:/login");
        }
        loginHelper.loginUser(user.get().getEmail());
        LOGGER.debug("User {} validated", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new ModelAndView("redirect:/explore?validationSuccess=true");
    }

    @GetMapping(value ="/not-verified")
    public ModelAndView notVerified() {
        return new ModelAndView("auth/not-verified");
    }

    @GetMapping(value ="/reset-password")
    public ModelAndView changePassForm(@RequestParam("token") String token, @ModelAttribute("updatePasswordForm")UpdatePasswordForm form) {
        if (!userService.isValidPasswordResetToken(token)) {
            LOGGER.debug("Invalid password reset token attempt: {}", token);
            return new ModelAndView("auth/invalid-token");
        }
        if(!userService.isTokenExpired(token)) {
            LOGGER.debug("Password reset token expired: {}", token);
            return new ModelAndView("auth/expired-token");
        }

        ModelAndView mav = new ModelAndView("auth/reset-password");
        mav.addObject("token", token);
        return mav;
    }

    @PostMapping(value ="/reset-password")
    public ModelAndView changePass(@RequestParam("token") String token, @Valid @ModelAttribute("updatePasswordForm")UpdatePasswordForm form, final BindingResult errors) {
        if (!userService.isValidPasswordResetToken(token)) {
            LOGGER.debug("Invalid password reset token attempt: {}", token);
            return new ModelAndView("auth/invalid-token");
        }
        if(!userService.isTokenExpired(token)) {
            LOGGER.debug("Password reset token expired: {}", token);
            return new ModelAndView("auth/expired-token");
        }

        if(errors.hasErrors()) {
            LOGGER.debug("Found {} errors in Update Password form data", errors.getErrorCount());
            return changePassForm(token, form);
        }

        userService.newPassword(token, form.getPassword());
        return new ModelAndView("redirect:login?resetPassword=true");
    }


    @RequestMapping("/login")
    public ModelAndView loginForm(
            @RequestParam(value = "emailSuccess", required = false, defaultValue = "false") final boolean emailSuccess,
            @RequestParam(value = "resetPassword", required = false, defaultValue = "false") final boolean resetPassword,
            @RequestParam(value = "registrationSuccess", required = false, defaultValue = "false") final boolean registrationSuccess,
                                  @ModelAttribute("user") User user) {
        LOGGER.debug("Loading login form");
        if (user != null) {
            return new ModelAndView("redirect:/explore");
        }
        ModelAndView mav = new ModelAndView("auth/login");
        mav.addObject("registrationSuccess", registrationSuccess);
        mav.addObject("resetPassword", resetPassword);
        mav.addObject("emailSuccess", emailSuccess);
        return mav;
    }

    @GetMapping("/forgot_pass")
    public ModelAndView forgotPassForm(@ModelAttribute ("emailForm") final EmailForm form) {
        LOGGER.debug("Loading forgot password form");
        return new ModelAndView("auth/email-form");
    }

    @PostMapping("/forgot_pass")
    public ModelAndView forgotPass(@Valid @ModelAttribute ("emailForm") final EmailForm form,
                                   final BindingResult errors) {
        if (errors.hasErrors()) {
            LOGGER.debug("Found {} errors in Forgot Password form data", errors.getErrorCount());
            LOGGER.debug("Errors: {}", errors);
            return forgotPassForm(form);
        }
        userService.forgotPass(form.getEmail());
        return new ModelAndView("redirect:/login?emailSuccess=true");
    }


    @RequestMapping("/blocked")
    public ModelAndView blockedForm() {
        ModelAndView mav = new ModelAndView("auth/blocked-user");
        mav.addObject("email", "paw.2025a.10@gmail.com" );
        return mav;
    }

    @GetMapping(value = "/register")
    public ModelAndView registerForm(@ModelAttribute ("createUserForm") final CreateUserForm form) {
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

        return new ModelAndView("redirect:/login?registrationSuccess=true");
    }

}

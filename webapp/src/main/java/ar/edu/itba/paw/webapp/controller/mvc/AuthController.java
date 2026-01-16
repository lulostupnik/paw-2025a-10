package ar.edu.itba.paw.webapp.controller.mvc;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.LoginHelper;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.EmailForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Deprecated

//@Controller
public class AuthController {

    private final UserService userService;
    private final LoginHelper loginHelper;
    private final TokenService tokenService;

    @Autowired
    public AuthController(final UserService userService, final LoginHelper loginHelper, TokenService tokenService) {
        this.userService = userService;
        this.loginHelper = loginHelper;
        this.tokenService = tokenService;
    }
    @GetMapping(value ="/validate")
    public ModelAndView validateEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        User user = userService.verifyUser(token);
        loginHelper.loginUser(user.getEmail());
        ModelAndView mav = new ModelAndView("redirect:/explore");
        redirectAttributes.addFlashAttribute("validationSuccess", true);
        return mav;
    }

    @GetMapping(value ="/not-verified")
    public ModelAndView notVerified() {
        return new ModelAndView("auth/not-verified");
    }

    @GetMapping(value ="/reset-password")
    public ModelAndView changePassForm(@RequestParam("token") String token, @ModelAttribute("updatePasswordForm") UpdatePasswordForm form) {
        tokenService.checkTokenValidity(token);
        ModelAndView mav = new ModelAndView("auth/reset-password");
        mav.addObject("token", token);
        return mav;
    }

    @PostMapping(value ="/reset-password")
    public ModelAndView changePass(@RequestParam("token") String token, @Valid @ModelAttribute("updatePasswordForm")UpdatePasswordForm form, final BindingResult errors) {
        tokenService.checkTokenValidity(token);

        if(errors.hasErrors()) {
            return changePassForm(token, form);
        }

        userService.resetPassword(token, form.getPassword());
        return new ModelAndView("redirect:login?resetPassword=true");
    }


    @RequestMapping("/login")
    public ModelAndView loginForm(
            @RequestParam(value = "emailSuccess", required = false, defaultValue = "false") final boolean emailSuccess,
            @RequestParam(value = "resetPassword", required = false, defaultValue = "false") final boolean resetPassword,
            @RequestParam(value = "registrationSuccess", required = false, defaultValue = "false") final boolean registrationSuccess,
                                  @ModelAttribute("user") User user) {
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
        return new ModelAndView("auth/email-form");
    }

    @PostMapping("/forgot_pass")
    public ModelAndView forgotPass(@Valid @ModelAttribute ("emailForm") final EmailForm form,
                                   final BindingResult errors) {
        if (errors.hasErrors()) {
            return forgotPassForm(form);
        }
        userService.initiatePasswordReset(form.getEmail());
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

        if (errors.hasErrors()) {
            return registerForm(form);
        }

        userService.createUser(form.getEmail(), form.getUsername(), form.getFirstName(),
                form.getLastName(), form.getOriginUniversity(), form.getCareer(),
                form.getInterests(), form.getPassword(), LocaleContextHolder.getLocale());

        return new ModelAndView("redirect:/login?registrationSuccess=true");
    }

}

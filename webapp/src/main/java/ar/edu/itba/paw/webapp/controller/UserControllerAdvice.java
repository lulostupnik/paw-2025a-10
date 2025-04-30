package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    private final UserService userService;

    @Autowired
    UserControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User user() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && !"anonymousUser".equals(auth.getPrincipal()) )
                ?  userService.findByEmail( auth.getName()).orElseThrow(RuntimeException::new)
                : null;
    }
}
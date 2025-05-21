package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerAdvice.class);

    @Autowired
    UserControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User user() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth != null && !"anonymousUser".equals(auth.getPrincipal()) ) {
            LOGGER.debug("user: {}",userService.findUserByEmail(auth.getName()).orElseThrow(()-> new RuntimeException("a")).toString());
        }
        return (auth != null && !"anonymousUser".equals(auth.getPrincipal()) )
                ?  userService.findUserByEmail(auth.getName()).orElseThrow(()-> {
                    LOGGER.error("Authenticated user with email {} not found in database", auth.getName());
                    return new IllegalStateException();
                }) : null;
    }
}
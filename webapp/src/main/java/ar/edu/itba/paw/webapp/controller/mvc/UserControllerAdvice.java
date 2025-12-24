package ar.edu.itba.paw.webapp.controller.mvc;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
@Deprecated

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
        return (auth != null && !"anonymousUser".equals(auth.getPrincipal()) )
                ?  userService.findUserByEmail(auth.getName()).orElseThrow(()-> {
                    LOGGER.error("Authenticated user with email {} not found in database", auth.getName());
                    return new UserNotFoundException("Authenticated user not found in database");
                }) : null;
    }
}
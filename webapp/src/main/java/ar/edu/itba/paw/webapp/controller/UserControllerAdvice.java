package ar.edu.itba.paw.webapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    @ModelAttribute("username")
    public String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && !"anonymousUser".equals(auth.getPrincipal()) )
                ? auth.getName()
                : null;
    }
}


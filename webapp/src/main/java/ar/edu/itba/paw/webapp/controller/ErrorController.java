package ar.edu.itba.paw.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/errors")
public class ErrorController {
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @RequestMapping("/403")
    public ModelAndView error403() {
        return new ModelAndView("errors/error");
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping("/404")
    public ModelAndView error404() {
        return new ModelAndView("errors/error");
    }
}

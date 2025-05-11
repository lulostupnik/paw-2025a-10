package ar.edu.itba.paw.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/errors")
public class ErrorController {
    private static final String ERROR_VIEW = "errors/error";
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @RequestMapping("/403")
    public ModelAndView error403() {
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "403");
        mav.addObject("errorIcon", "shield-off");
        return mav;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping("/404")
    public ModelAndView error404() {
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "404");
        return mav;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping("/500")
    public ModelAndView error500() {
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "500");
        return mav;
    }



    @RequestMapping("/general")
    public ModelAndView errorGeneral() {
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "general");
        return mav;
    }
}

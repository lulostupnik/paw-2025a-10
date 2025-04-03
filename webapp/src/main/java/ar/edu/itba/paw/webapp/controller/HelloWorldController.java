package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Context;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Controller
public class HelloWorldController {

    private final EmailService emailService;
    @Autowired
    public HelloWorldController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }


}

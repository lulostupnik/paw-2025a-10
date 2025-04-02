package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Context;
import java.util.HashMap;
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

//un controller to test
    @RequestMapping("/sendmail")
    public ModelAndView sendMail() {
        // Create a Thymeleaf context and add variables
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("usernae", "Lucas Stupnik"); // Dynamic value
        vars.put("message", "This is a <strong>test HTML email</strong>!");

        // Send HTML email using Thymeleaf template
        emailService.sendHtmlMessage(
                "lstupnik@itba.edu.ar",
                "Test HTML Email",
                "welcome", // Name of the Thymeleaf template (without .html)
                 vars
        );

        // Return a confirmation view
        ModelAndView mav = new ModelAndView("mail_sent");
        mav.addObject("message", "HTML email sent successfully!");
        return mav;
    }

}

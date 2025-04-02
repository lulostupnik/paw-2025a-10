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

    @GetMapping("/sendEmail")
    public String sendEmail() {

        // Set Spanish as the user's locale
        Locale userLocale = new Locale("es");

        // 1) Prepare the subject arguments, for the key: "mail.subject.welcome"
        //    which includes a placeholder {0}.
        Object[] subjectArgs = {"Carlos"};

        // 2) Variables for the Thymeleaf template (the HTML body)
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Carlos");

        // 3) Send the email
        emailService.sendHtmlMessage(
                "lstupnik@itba.edu.ar",    // Destination email address
                "mail.subject.welcome",     // The subject key from messages_es.properties
                subjectArgs,                // Replacement for placeholders (i.e. {0} -> "Carlos")
                "welcome",          // The Thymeleaf template name (no .html if configured)
                variables,                  // Variables map for the template
                userLocale                  // <--- Spanish
        );
        // Return a simple status
        return "Email sent in Spanish!"; //esto falla lo q vuelve, pero no importa es para probar. 
    }

}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final static String fromEmail = "paw.2025a.10@gmail.com";

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            TemplateEngine emailTemplateEngine,
                            MessageSource messageSource) {
        this.emailSender = emailSender;
        this.templateEngine = emailTemplateEngine;
        this.messageSource = messageSource;
    }
    @Async
    @Override
    public void sendHtmlMessage(String to,
                                String subjectKey,
                                Object[] subjectArgs,
                                String templateName,
                                Map<String, Object> variables,
                                Locale locale) {
        try {
            String subject = messageSource.getMessage(subjectKey, subjectArgs, locale);
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Context context = new Context(locale);
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    /*
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    @Override
    public void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }*/


}


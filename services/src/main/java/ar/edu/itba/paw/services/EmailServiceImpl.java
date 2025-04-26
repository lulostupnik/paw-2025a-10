package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.Locale;
import java.util.Map;

@Async
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final static String fromEmail = "paw.2025a.10@gmail.com";
    private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            TemplateEngine emailTemplateEngine,
                            MessageSource messageSource) {
        this.emailSender = emailSender;
        this.templateEngine = emailTemplateEngine;
        this.messageSource = messageSource;
    }




    protected void sendHtmlMessage(String to,
                                   String[] cc,
                                   String[] bcc,
                                   String subjectKey,
                                   Object[] subjectArgs,
                                   String templateName,
                                   Map<String, Object> variables,
                                   Locale locale,
                                   byte[] imageBytes) {

        try {
            String subject = messageSource.getMessage(subjectKey, subjectArgs, locale);
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context(locale);
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail);
            if(to==null || to.isEmpty()){
                to = fromEmail; //asi puedo mandar BCC/CC
            }
            helper.setTo(to);

            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            if (bcc != null && bcc.length > 0) {
                helper.setBcc(bcc);
            }

            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            if (imageBytes != null) {
                DataSource imageSource = new ByteArrayDataSource(imageBytes, "image/jpeg"); // or image/png
                helper.addInline("profileImage", imageSource);
            }

            emailSender.send(message);

        } catch (Exception e) {
            LOGGER.error("Failed to send email", e);
        }
    }

    private void answerEventMailHelper( String to, String[] cc, String[] bcc,
                                String firstName, String lastName,
                                String username, String career,
                                String originUniversity, String message,
                                Locale locale, byte[] profilePicture,
                                String subjectKey,Object[] subjectArgs, String temapleName, String idKey, long id) {

        Map<String, Object> variables = Map.of(
                "firstname", firstName,
                "lastname", lastName,
                "username", username,
                "career", career,
                "university", originUniversity,
                "message", message,
                "hasProfileImage", profilePicture != null && profilePicture.length > 0,
                idKey, id
        );

        sendHtmlMessage(
                to,
                cc,
                bcc,
                subjectKey,
                subjectArgs,
                temapleName,
                variables,
                locale,
                profilePicture
        );
    }
    @Override
    public void answerEventRespondersNotification( String[] bcc,
                                String firstName, String lastName,
                                String username, String career,
                                String originUniversity, String message,
                                Locale locale, byte[] profilePicture,
                                long eventId) {
        if(bcc == null || bcc.length == 0){
            return;
        }
        answerEventMailHelper(null,null, bcc, firstName,lastName,username,career,
                originUniversity,message,locale,profilePicture,"email.event.comment.notification.title", new Object[]{},
                "event-new-comment", "eventId", eventId);
    }


    @Override
    public void answerJourneyRespondersNotification( String[] bcc,
                                                   String firstName, String lastName,
                                                   String username, String career,
                                                   String originUniversity, String message,
                                                   Locale locale, byte[] profilePicture,
                                                   long journeyId) {
        if(bcc == null || bcc.length == 0){
            return;
        }
        answerEventMailHelper(null,null, bcc, firstName,lastName,username,career,
                originUniversity,message,locale,profilePicture,"email.journey.comment.notification.title", new Object[]{},
                "journey-new-comment", "journeyId", journeyId);
    }
    @Override
    public void answerEventMail(String from, String to,
                                String firstName, String lastName,
                                String username, String career,
                                String originUniversity, String message,
                                Locale locale, byte[] profilePicture,
                                long eventId) {
        if(from.equals(to)){
            return;
        }
       answerEventMailHelper(to,null, null, firstName,lastName,username,career,
               originUniversity,message,locale,profilePicture,"email.event.reply.title", new Object[]{},
               "event-response", "eventId", eventId);}

    @Override
    public void answerJourneyMail(String from, String to,
                                  String firstName, String lastName,
                                  String username, String career,
                                  String originUniversity, String message,
                                  Locale locale, byte[] profilePicture, long journeyId) {

        if(from.equals(to)){
            return;
        }
        answerEventMailHelper(to,null,null,firstName,lastName,username,career,
                originUniversity,message,locale,profilePicture,"email.journey.reply.subject", new Object[]{},
                "journey-response", "journeyId", journeyId);}


}


package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.valueObjects.EmailContent;
import ar.edu.itba.paw.models.valueObjects.EmailRecipient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Async
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Value("${email.from}")
    private String fromEmail;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            TemplateEngine templateEngine,
                            MessageSource messageSource) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    private void sendHtmlMessage(EmailRecipient emailRecipient, EmailContent emailContent, String templateName, Map<String, Object> variables) {
        try {
            String subject = messageSource.getMessage(
                    emailContent.getSubjectKey(),
                    emailContent.getSubjectArgs(),
                    emailRecipient.getLocale()
            );

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Context context = new Context(emailRecipient.getLocale());
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setFrom(fromEmail);
            helper.setTo(emailRecipient.getToEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            DataSource imageSource = new ByteArrayDataSource(emailContent.getImage().getData(), "image/jpeg");  //Preguntar: no lo valido porque emailMessage hace que el byteArray sea notNull.
            helper.addInline("profileImage", imageSource);
            emailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("Failed to send email", e);
        }
    }

    private Map<String, Object> buildVariables(String firstName, String lastName, String username,
                                               String career, String originUniversity, String message,
                                               byte[] profilePicture, String idKey, long id) {
        return Map.of(
                "firstname", firstName,
                "lastname", lastName,
                "username", username,
                "career", career,
                "university", originUniversity,
                "message", message,
                "hasProfileImage", profilePicture != null && profilePicture.length > 0,
                idKey, id
        );
    }

    @Override
    public void answerEventRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, Event event) {
        User eventUser = event.getUser();

        Map<String, Object> variables = buildVariables(
                eventUser.getFirstname(), eventUser.getLastname(),
                eventUser.getUsername(), eventUser.getCareer().getName(),
                eventUser.getUniversity().getName(),emailContent.getMessage(),
                emailContent.getImage().getData(), "eventId", event.getId()
        );
        for(EmailRecipient recipient : emailRecipients){
            if(recipient.getToEmail().isEmpty() || recipient.getToEmail().equals(eventUser.getEmail())){
                continue;  //no se si es buen estilo // o hace falta
            }
            sendHtmlMessage(recipient, emailContent,"event-new-comment", variables);
        }
    }

    @Override
    public void answerJourneyRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, Journey journey) {
        User journeyUser = journey.getUser();

        Map<String, Object> variables = buildVariables(
                journeyUser.getFirstname(), journeyUser.getLastname(), journeyUser.getUsername(),
                journeyUser.getCareer().getName(), journeyUser.getUniversity().getName(), emailContent.getMessage(),
                emailContent.getImage().getData(), "journeyId", journey.getId()
        );

        for(EmailRecipient recipient: emailRecipients){
            if(recipient.getToEmail().isEmpty() || recipient.getToEmail().equals(journeyUser.getEmail())){
                continue;  //no se si es buen estilo // o hace falta
            }
            sendHtmlMessage(recipient, emailContent,"journey-new-comment", variables);
        }
    }


    @Override
    public void answerEventMail(EmailRecipient emailRecipient, EmailContent emailContent, Event event) {
        User eventUser = event.getUser();
        if(emailRecipient.getToEmail().isEmpty() || emailRecipient.getToEmail().equals(eventUser.getEmail())){
            return;
        }
        Map<String, Object> variables = buildVariables(
                eventUser.getFirstname(), eventUser.getLastname(),
                eventUser.getUsername(), eventUser.getCareer().getName(),
                eventUser.getUniversity().getName(),emailContent.getMessage(),
                emailContent.getImage().getData(), "eventId", event.getId()
        );

        sendHtmlMessage(emailRecipient, emailContent,"event-response", variables);
    }

    @Override
    public void answerJourneyMail(EmailRecipient emailRecipient,EmailContent emailContent, Journey journey) {
        User journeyUser = journey.getUser();
        if(emailRecipient.getToEmail().isEmpty() || emailRecipient.getToEmail().equals(journeyUser.getEmail())){
            return;
        }
        Map<String, Object> variables = buildVariables(
                journeyUser.getFirstname(), journeyUser.getLastname(), journeyUser.getUsername(),
                journeyUser.getCareer().getName(), journeyUser.getUniversity().getName(), emailContent.getMessage(),
                emailContent.getImage().getData(), "journeyId", journey.getId()
        );

        sendHtmlMessage(emailRecipient, emailContent, "journey-response", variables);
    }
}









//No borrar, preguntar el lunes
//
//@Async
//@Service
//public class EmailServiceImpl implements EmailService {
//    private final JavaMailSender emailSender;
//    private final TemplateEngine templateEngine;
//    private final MessageSource messageSource;
//
//    @Value("${email.from}")
//    private String fromEmail;
//
////    private final static String fromEmail = "paw.2025a.10@gmail.com";
//    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
//
//    @Autowired
//    public EmailServiceImpl(JavaMailSender emailSender,
//                            TemplateEngine emailTemplateEngine,
//                            MessageSource messageSource) {
//        this.emailSender = emailSender;
//        this.templateEngine = emailTemplateEngine;
//        this.messageSource = messageSource;
//    }
//
//
//
//    protected void sendHtmlMessage(String to,
//                                   String[] cc,
//                                   String[] bcc,
//                                   String subjectKey,
//                                   Object[] subjectArgs,
//                                   String templateName,
//                                   Map<String, Object> variables,
//                                   Locale locale,
//                                   byte[] imageBytes) {
//
//        try {
//            String subject = messageSource.getMessage(subjectKey, subjectArgs, locale);
//            MimeMessage message = emailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            Context context = new Context(locale);
//            context.setVariables(variables);
//            String htmlContent = templateEngine.process(templateName, context);
//
//            helper.setFrom(fromEmail);
//            if(to==null || to.isEmpty()){
//                to = fromEmail; //asi puedo mandar BCC/CC
//            }
//            helper.setTo(to);
//
//            if (cc != null && cc.length > 0) {
//                helper.setCc(cc);
//            }
//            if (bcc != null && bcc.length > 0) {
//                helper.setBcc(bcc);
//            }
//
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true);
//
//            if (imageBytes != null) {
//                DataSource imageSource = new ByteArrayDataSource(imageBytes, "image/jpeg");
//                helper.addInline("profileImage", imageSource);
//            }
//
//            emailSender.send(message);
//
//        } catch (Exception e) {
//            LOGGER.error("Failed to send email", e);
//        }
//    }
//
//    private void answerEventMailHelper( String to, String[] cc, String[] bcc,
//                                String firstName, String lastName,
//                                String username, String career,
//                                String originUniversity, String message,
//                                Locale locale, byte[] profilePicture,
//                                String subjectKey,Object[] subjectArgs, String temapleName, String idKey, long id) {
//
//        Map<String, Object> variables = Map.of(
//                "firstname", firstName,
//                "lastname", lastName,
//                "username", username,
//                "career", career,
//                "university", originUniversity,
//                "message", message,
//                "hasProfileImage", profilePicture != null && profilePicture.length > 0,
//                idKey, id
//        );
//
//        sendHtmlMessage(
//                to,
//                cc,
//                bcc,
//                subjectKey,
//                subjectArgs,
//                temapleName,
//                variables,
//                locale,
//                profilePicture
//        );
//    }
//    @Override
//    public void answerEventRespondersNotification( String[] bcc,
//                                String firstName, String lastName,
//                                String username, String career,
//                                String originUniversity, String message,
//                                Locale locale, byte[] profilePicture,
//                                long eventId) {
//        if(bcc == null || bcc.length == 0){
//            return;
//        }
//        answerEventMailHelper(null,null, bcc, firstName,lastName,username,career,
//                originUniversity,message,locale,profilePicture,"email.event.comment.notification.title", new Object[]{},
//                "event-new-comment", "eventId", eventId);
//    }
//
//
//    @Override
//    public void answerJourneyRespondersNotification( String[] bcc,
//                                                   String firstName, String lastName,
//                                                   String username, String career,
//                                                   String originUniversity, String message,
//                                                   Locale locale, byte[] profilePicture,
//                                                   long journeyId) {
//        if(bcc == null || bcc.length == 0){
//            return;
//        }
//        answerEventMailHelper(null,null, bcc, firstName,lastName,username,career,
//                originUniversity,message,locale,profilePicture,"email.journey.comment.notification.title", new Object[]{},
//                "journey-new-comment", "journeyId", journeyId);
//    }
//    @Override
//    public void answerEventMail(String from, String to,
//                                String firstName, String lastName,
//                                String username, String career,
//                                String originUniversity, String message,
//                                Locale locale, byte[] profilePicture,
//                                long eventId) {
//        if(from.equals(to)){
//            return;
//        }
//       answerEventMailHelper(to,null, null, firstName,lastName,username,career,
//               originUniversity,message,locale,profilePicture,"email.event.reply.title", new Object[]{},
//               "event-response", "eventId", eventId);}
//
//    @Override
//    public void answerJourneyMail(String from, String to,
//                                  String firstName, String lastName,
//                                  String username, String career,
//                                  String originUniversity, String message,
//                                  Locale locale, byte[] profilePicture, long journeyId) {
//
//        if(from.equals(to)){
//            return;
//        }
//        answerEventMailHelper(to,null,null,firstName,lastName,username,career,
//                originUniversity,message,locale,profilePicture,"email.journey.reply.subject", new Object[]{},
//                "journey-response", "journeyId", journeyId);}
//
//
//}
//

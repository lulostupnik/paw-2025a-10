package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

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


@Service
@Async
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    private final UserService userService;
    @Value("${email.from}")
    private String fromEmail;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            TemplateEngine templateEngine,
                            MessageSource messageSource,
                            UserService userService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.userService = userService;
    }

    private void sendHtmlMessage(byte[] image, User emailRecipient, String templateName, Map<String, Object> variables, String subjectKey, Object[] subjectArgs) {
        try {
            String subject = messageSource.getMessage(
                    subjectKey,
                    subjectArgs,
                    emailRecipient.getLocale()
            );

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Context context = new Context(emailRecipient.getLocale());
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setFrom(fromEmail);
            helper.setTo(emailRecipient.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            DataSource imageSource = new ByteArrayDataSource(image, "image/jpeg");  //Preguntar: no lo valido porque emailMessage hace que el byteArray sea notNull.
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
    public void answerEventNotification(List<User> oldRepliers, String message, User commenter, Event event) {
        User eventUser = event.getUser();

        byte[] profilePictureData = userService.getProfilePictureData(commenter);

        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                profilePictureData, "eventId", event.getId());

        for(User recipient : oldRepliers){
            if(recipient.getEmail().equals(eventUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;  //no se si es buen estilo // o hace falta
            }
            sendHtmlMessage(profilePictureData, recipient,"event-new-comment", variables, "email.event.comment.notification.title", new Object[]{});
        }
        if(!commenter.getUsername().equals(eventUser.getUsername())){
            sendHtmlMessage(profilePictureData,eventUser, "event-response", variables, "email.event.reply.title", new Object[]{});
        }

    }

    @Override
    public void answerJourneyNotification(List<User> oldRepliers, String message, User commenter, Journey journey) {
        User journeyUser = journey.getUser();
        byte[] profilePictureData = userService.getProfilePictureData(commenter);



        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                profilePictureData, "journeyId", journey.getId());



        for(User recipient: oldRepliers){
            if(recipient.getEmail().equals(journeyUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;
            }
            sendHtmlMessage(profilePictureData,recipient, "journey-new-comment", variables, "email.journey.comment.notification.title", new Object[]{});
        }
        if(!commenter.getUsername().equals(journeyUser.getUsername())){
            sendHtmlMessage(profilePictureData, journeyUser, "journey-response", variables, "email.journey.reply.subject", new Object[]{});
        }

    }

/*
    @Override
    public void answerEventMail(User emailRecipient, String message, User commenter, Event event){
        User eventUser = event.getUser();
        if(emailRecipient.getEmail().equals(commenter.getEmail())){
            return;
        }
        byte[] profilePictureData = userService.getProfilePictureData(commenter);


        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                profilePictureData, "eventId", event.getId());


        sendHtmlMessage(profilePictureData,emailRecipient, "event-response", variables, "email.event.reply.title", new Object[]{});
    }

    @Override
    public void answerJourneyMail(User emailRecipient, String message, User commenter, Journey journey){

        User journeyUser = journey.getUser();
        if(emailRecipient.getEmail().equals(commenter.getEmail())){
            return;
        }
        byte[] profilePictureData = userService.getProfilePictureData(commenter);

        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                userService.getProfilePictureData(commenter), "journeyId", journey.getId());


        sendHtmlMessage(profilePictureData, emailRecipient, "journey-response", variables, "email.journey.reply.subject", new Object[]{});
    }*/
}


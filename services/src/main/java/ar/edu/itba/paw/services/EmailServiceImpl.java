package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Event;
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
        this.userService = userService; //@TODO preguntar.
    }

    private void sendHtmlMessage(byte[] image, EmailRecipient emailRecipient, EmailContent emailContent, String templateName, Map<String, Object> variables, String subjectKey, Object[] subjectArgs) {
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
            helper.setTo(emailRecipient.getToEmail());
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
    public void answerEventRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, User commenter, Event event) {
        User eventUser = event.getUser();

        byte[] profilePictureData = userService.getProfilePictureData(commenter);
//        try {  @TODO preguntar. no deberia fallar nunca ya que es non null en la bd.
//            profilePictureData = userService.getProfilePictureData(commenter);
//        }catch (Exception e){
//                LOGGER.error("Failed to retrieve profile picture for commenter with id {}", commenter.getId(), e);
//        }


            Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),emailContent.getMessage(),
                profilePictureData, "eventId", event.getId());

        for(EmailRecipient recipient : emailRecipients){
            if(recipient.getToEmail().isEmpty() || recipient.getToEmail().equals(eventUser.getEmail()) || recipient.getToEmail().equals(commenter.getEmail())){
                continue;  //no se si es buen estilo // o hace falta
            }
            sendHtmlMessage(profilePictureData, recipient, emailContent,"event-new-comment", variables, "email.event.comment.notification.title", new Object[]{});
        }
    }

    @Override
    public void answerJourneyRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, User commenter, Journey journey) {
        User journeyUser = journey.getUser();
        byte[] profilePictureData = userService.getProfilePictureData(commenter);



        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),emailContent.getMessage(),
                profilePictureData, "journeyId", journey.getId());



        for(EmailRecipient recipient: emailRecipients){
            if(recipient.getToEmail().isEmpty() || recipient.getToEmail().equals(journeyUser.getEmail()) || recipient.getToEmail().equals(commenter.getEmail())){
                continue;  //no se si es buen estilo // o hace falta
            }
            sendHtmlMessage(profilePictureData,recipient, emailContent,"journey-new-comment", variables, "email.journey.comment.notification.title", new Object[]{});
        }
    }


    @Override
    public void answerEventMail(EmailRecipient emailRecipient, EmailContent emailContent, User commenter, Event event) {
        User eventUser = event.getUser();
        if(emailRecipient.getToEmail().isEmpty() || emailRecipient.getToEmail().equals(commenter.getEmail())){
            return;
        }
        byte[] profilePictureData = userService.getProfilePictureData(commenter);


        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),emailContent.getMessage(),
                profilePictureData, "eventId", event.getId());


        sendHtmlMessage(profilePictureData,emailRecipient, emailContent,"event-response", variables, "email.event.reply.title", new Object[]{});
    }

    @Override
    public void answerJourneyMail(EmailRecipient emailRecipient,EmailContent emailContent, User commenter, Journey journey) {
        User journeyUser = journey.getUser();
        if(emailRecipient.getToEmail().isEmpty() || emailRecipient.getToEmail().equals(commenter.getEmail())){
            return;
        }
        byte[] profilePictureData = userService.getProfilePictureData(commenter);

        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),emailContent.getMessage(),
                userService.getProfilePictureData(commenter), "journeyId", journey.getId());


        sendHtmlMessage(profilePictureData, emailRecipient, emailContent, "journey-response", variables, "email.journey.reply.subject", new Object[]{});
    }
}


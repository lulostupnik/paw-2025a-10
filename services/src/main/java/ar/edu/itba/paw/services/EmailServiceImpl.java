package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Async
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final ImageService imageService;


    @Value("${email.from}")
    private String fromEmail;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            TemplateEngine templateEngine,
                            MessageSource messageSource,
                            ImageService imageService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.imageService = imageService;
    }

    private void sendHtmlMessage(Optional<byte[]> maybeImage,Optional<String> maybeImageCid, User emailRecipient, String templateName, Map<String, Object> variables, String subjectKey, Optional<Object[]> maybeSubjectArgs) {
        try {
            LOGGER.debug("Sending email to: {}", emailRecipient.getEmail());
            LOGGER.debug("Locale of recipient: {}", emailRecipient.getLocale());
            LOGGER.debug("Subject key: {}", subjectKey);
            LOGGER.debug("Subject args: {}", maybeSubjectArgs.orElse(null));
            String subject = messageSource.getMessage(
                    subjectKey,
                    maybeSubjectArgs.orElse(null),
                    emailRecipient.getLocale()
            );
            LOGGER.debug("Resolved subject: {}", subject);


            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Context context = new Context(emailRecipient.getLocale());
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            LOGGER.debug("Rendered template [{}] for locale [{}]", templateName,emailRecipient.getLocale());
            helper.setFrom(fromEmail);
            helper.setTo(emailRecipient.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            if(maybeImageCid.isPresent() && maybeImage.isPresent() && maybeImage.get().length > 0){
                LOGGER.debug("Attaching image with CID: {}", maybeImageCid.get());
                DataSource imageSource = new ByteArrayDataSource(maybeImage.get(), "image/jpeg");
                helper.addInline(maybeImageCid.get(), imageSource);
            }
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
    public void sendEventCommentDeletionNotification(EventResponse deletedComment, Event event , User commentAuthor, String adminMessage) {

        LOGGER.debug("Retrieved User (comment author): {}", commentAuthor);

        Map<String, Object> variables = new HashMap<>();
        variables.put("isEvent", true);
        variables.put("contentTitle", event.getTitle());
        variables.put("contentId", event.getId());
        variables.put("commentDate", deletedComment.getFormattedDate());
        variables.put("commentMessage", deletedComment.getMessage());
        variables.put("adminMessage", adminMessage);

        LOGGER.debug("Sending comment deletion email with variables: {}", variables);

        sendHtmlMessage(
                Optional.empty(),
                Optional.empty(),
                commentAuthor,
                "comment-deletion",
                variables,
                "email.comment.deletion.title",
                Optional.empty()
        );

        LOGGER.debug("Comment deletion notification sent successfully to user {}", commentAuthor.getEmail());
    }



    @Override
    public void sendJourneyCommentDeletionNotification(/*long journeyResponseId,*/ JourneyResponse deletedComment,  Journey journey, User commentAuthor , String adminMessage) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("isEvent", false);
        variables.put("contentId", journey.getId());
        variables.put("commentDate", deletedComment.getFormattedDate());
        variables.put("commentMessage", deletedComment.getMessage());
        variables.put("adminMessage", adminMessage);

        sendHtmlMessage(Optional.empty(), Optional.empty(), commentAuthor, "comment-deletion", variables,
                "email.comment.deletion.title", Optional.empty());
    }


    @Override
    public void answerEventNotification(List<User> oldRepliers, String message, User commenter, Event event) {
        User eventUser = event.getUser();


        byte[] profilePictureData = imageService.getImage(commenter.getProfilePictureId()).orElseThrow(() -> new IllegalStateException("User does not have profile picture")).getData();

        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                profilePictureData, "eventId", event.getId());

        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");

        for(User recipient : oldRepliers){
            if(recipient.getEmail().equals(eventUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;
            }
            sendHtmlMessage(profileImageOptional,profileCidOptional ,recipient,"event-new-comment", variables, "email.event.comment.notification.title", Optional.empty());
        }
        if(!commenter.getUsername().equals(eventUser.getUsername())){
            sendHtmlMessage(profileImageOptional,profileCidOptional,eventUser, "event-response", variables, "email.event.reply.title", Optional.empty());
        }

    }

    @Override
    public void answerJourneyNotification(List<User> oldRepliers, String message, User commenter, Journey journey) {
        User journeyUser = journey.getUser();
        byte[] profilePictureData = imageService.getImage(commenter.getProfilePictureId()).orElseThrow(() -> new IllegalStateException("User does not have profile picture")).getData();

        Map<String, Object> variables = buildVariables(
                commenter.getFirstname(), commenter.getLastname(),
                commenter.getUsername(), commenter.getCareer().getName(),
                commenter.getUniversity().getName(),message,
                profilePictureData, "journeyId", journey.getId());


        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");

        for(User recipient: oldRepliers){
            if(recipient.getEmail().equals(journeyUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;
            }
            sendHtmlMessage(profileImageOptional, profileCidOptional,recipient, "journey-new-comment", variables, "email.journey.comment.notification.title", Optional.empty());
        }
        if(!commenter.getUsername().equals(journeyUser.getUsername())){
            sendHtmlMessage(profileImageOptional,  profileCidOptional,journeyUser, "journey-response", variables, "email.journey.reply.subject", Optional.empty());
        }

    }
    @Override
    public void sendEventDeletionNotification(Event event, String adminMessage) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventTitle", event.getTitle());
        variables.put("eventId", event.getId());
        variables.put("adminMessage", adminMessage);

        sendHtmlMessage(Optional.empty(),Optional.empty(), event.getUser(), "event-deletion", variables,
                "email.event.deletion.title",Optional.of(new Object[]{event.getTitle()}));
    }

    @Override
    public void sendJourneyDeletionNotification(Journey journey, String adminMessage) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("journeyId", journey.getId());
        variables.put("adminMessage", adminMessage);

        sendHtmlMessage(Optional.empty(),Optional.empty(), journey.getUser(), "journey-deletion", variables,
                "email.journey.deletion.title", Optional.empty());
    }


    @Override
    public void sendUserBlockedNotification(User blockedUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", blockedUser.getUsername());

        sendHtmlMessage(Optional.empty(), Optional.empty(), blockedUser, "user-blocked", variables,
                "email.user.blocked.title", Optional.empty());
    }
    @Override
    public void sendUserUnblockedNotification(User unblockedUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", unblockedUser.getUsername());

        sendHtmlMessage(Optional.empty(), Optional.empty(), unblockedUser, "user-unblocked", variables,
                "email.user.unblocked.title", Optional.empty());
    }

    @Override
    public void sendValidationEmail(User user, String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", user.getUsername());
        variables.put("validationToken", token);

        sendHtmlMessage(Optional.empty(), Optional.empty(), user, "validation", variables,
                "email.validation.title", Optional.empty());
    }
    @Override
    public void sendForgotPassEmail(User user, String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", user.getUsername());
        variables.put("resetToken", token);

        sendHtmlMessage(Optional.empty(), Optional.empty(), user, "forgot-password", variables,
                "email.reset.title", Optional.empty());
    }


    @Override
    public void sendEventReminderNotification(Event event, List<User> attendees) {
        Optional<byte[]> eventImage = Optional.empty();
        Optional<String> eventImageCid = Optional.empty();

        if (event.getFlyerImageId() > 0) {
            eventImage = Optional.ofNullable(imageService.getImage(event.getFlyerImageId())
                    .map(Image::getData)
                    .orElse(null));
            eventImageCid = Optional.of("eventImage");
        }

        for (User attendee : attendees) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("firstname", attendee.getFirstname());
            variables.put("event", event);

            sendHtmlMessage(
                    eventImage,
                    eventImageCid,
                    attendee,
                    "event-reminder",
                    variables,
                    "email.event.reminder.title",
                    Optional.of(new Object[]{event.getTitle()})
            );

            LOGGER.debug("Event reminder notification sent successfully to user {}", attendee.getEmail());
        }
    }



}


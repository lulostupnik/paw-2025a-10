package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.InvalidImageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.*;

@PropertySource("classpath:email.properties")
@Service
@Async
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final ImageService imageService;

    @Value("${support.email}")
    private String supportEmail;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${base.link}")
    private String baseUrl;


    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(final JavaMailSender emailSender,
                            final TemplateEngine templateEngine,
                            final MessageSource messageSource,
                            final ImageService imageService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.imageService = imageService;
    }

    private void sendHtmlMessage(final Optional<byte[]> maybeImage,final Optional<String> maybeImageCid,final User emailRecipient, final String templateName, final Map<String, Object> variables,final String subjectKey, final Optional<Object[]> maybeSubjectArgs) {
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
            LOGGER.warn("Failed to send email", e);
        }
    }


    private Map<String, Object> buildAnswerVariables(User commenter, String message, String idKey, long id, boolean hasProfilePicture) {

        return Map.of(
                "firstname", commenter.getFirstname(),
                "lastname", commenter.getLastname(),
                "username", commenter.getUsername(),
                "career",  commenter.getCareer().getName(),
                "university", commenter.getUniversity().getName(),
                "message", message,
                "hasProfileImage", hasProfilePicture,
                idKey, id,
                "baseUrl", baseUrl
        );
    }
    private Map<String, Object> answerJourneyNotificationVariables(User commenter,  Journey journey,String message, byte[] profileArray) {
        return buildAnswerVariables(commenter, message, "journeyId", journey.getId(), profileArray != null && profileArray.length > 0);
    }
    private Map<String, Object> answerEventNotificationVariables(User commenter, Event event, String message, byte[] profileArray) {
        return buildAnswerVariables(commenter, message, "eventId", event.getId(), profileArray != null && profileArray.length > 0);
    }


    @Override
    public void sendEventCommentDeletionNotification(final EventResponse deletedComment,final Event event , final User commentAuthor,final  String adminMessage) {

        LOGGER.debug("Retrieved User (comment author): {}", commentAuthor);
        Map<String, Object> variables = new HashMap<>();
        variables.put("isEvent", true);
        variables.put("contentTitle", event.getTitle());
        variables.put("contentId", event.getId());
        variables.put("commentDate", deletedComment.getFormattedDate());
        variables.put("commentMessage", deletedComment.getMessage());
        variables.put("adminMessage", adminMessage);
        variables.put("baseUrl", baseUrl);


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
    public void sendJourneyCommentDeletionNotification(final JourneyResponse deletedComment,final  Journey journey, final User commentAuthor ,final String adminMessage) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("isEvent", false);
        variables.put("contentId", journey.getId());
        variables.put("commentDate", deletedComment.getFormattedDate());
        variables.put("commentMessage", deletedComment.getMessage());
        variables.put("adminMessage", adminMessage);
        variables.put("baseUrl", baseUrl);


        sendHtmlMessage(Optional.empty(), Optional.empty(), commentAuthor, "comment-deletion", variables,
                "email.comment.deletion.title", Optional.empty());
    }

    private byte[] getUserImageData(User user){
        return imageService.findImage(user.getProfilePictureId()).orElseThrow(() -> {
            LOGGER.error("User does not have profile picture");
            return new InvalidImageException("User does not have profile picture");
        }).getData();
    }
    @Override
    public void answerEventOwnerNotification(final String message, final User commenter, final Event event) {
        User eventUser = event.getUser();

        if(commenter.getEmail().equals(eventUser.getEmail())){
            return;
        }
        byte[] profilePictureData = getUserImageData(commenter);
        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");
        Map<String, Object> variables = answerEventNotificationVariables(commenter, event, message,  profilePictureData);
        sendHtmlMessage(profileImageOptional,profileCidOptional,eventUser, "event-response", variables, "email.event.reply.title", Optional.empty());
    }
    @Override
    public void answerEventNotification(final List<User> oldRepliers,final String message, final User commenter, final Event event) {
        User eventUser = event.getUser();

        byte[] profilePictureData = getUserImageData(commenter);
        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");
        Map<String, Object> variables = answerEventNotificationVariables(commenter, event, message,  profilePictureData);


        for(User recipient : oldRepliers){
            if(recipient.getEmail().equals(eventUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;
            }
            sendHtmlMessage(profileImageOptional,profileCidOptional ,recipient,"event-new-comment", variables, "email.event.comment.notification.title", Optional.empty());
        }

    }

    @Override
    public void answerJourneyOwnerNotification(final String message, final User commenter, final Journey journey) {
        User journeyUser = journey.getUser();
        if(commenter.getEmail().equals(journeyUser.getEmail())){
            return;
        }

        byte[] profilePictureData = getUserImageData(commenter);
        Map<String, Object> variables = answerJourneyNotificationVariables(commenter, journey, message,  profilePictureData);
        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");

        sendHtmlMessage(profileImageOptional,  profileCidOptional,journeyUser, "journey-response", variables, "email.journey.reply.subject", Optional.empty());

    }
    @Override
    public void answerJourneyNotification(final List<User> oldRepliers, final String message, final User commenter, final Journey journey) {
        User journeyUser = journey.getUser();

        byte[] profilePictureData = getUserImageData(commenter);
        Map<String, Object> variables = answerJourneyNotificationVariables(commenter, journey, message,  profilePictureData);
        Optional<byte[]> profileImageOptional = Optional.of(profilePictureData);
        Optional<String> profileCidOptional = Optional.of("profileImage");

        for(User recipient: oldRepliers){
            if(recipient.getEmail().equals(journeyUser.getEmail()) || recipient.getEmail().equals(commenter.getEmail())){
                continue;
            }
            sendHtmlMessage(profileImageOptional, profileCidOptional,recipient, "journey-new-comment", variables, "email.journey.comment.notification.title", Optional.empty());
        }
    }
    @Override
    public void sendEventDeletionNotification(final Event event,final String adminMessage) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventTitle", event.getTitle());
        variables.put("eventId", event.getId());
        variables.put("adminMessage", adminMessage);
        variables.put("baseUrl", baseUrl);


        sendHtmlMessage(Optional.empty(),Optional.empty(), event.getUser(), "event-deletion", variables,
                "email.event.deletion.title",Optional.of(new Object[]{event.getTitle()}));
    }

    @Override
    public void sendJourneyDeletionNotification(final Journey journey,final String adminMessage) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("journeyId", journey.getId());
        variables.put("adminMessage", adminMessage);
        variables.put("baseUrl", baseUrl);

        sendHtmlMessage(Optional.empty(),Optional.empty(), journey.getUser(), "journey-deletion", variables,
                "email.journey.deletion.title", Optional.empty());
    }


    @Override
    public void sendUserBlockedNotification(final User blockedUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", blockedUser.getUsername());
        variables.put("baseUrl", baseUrl);
        variables.put("supportEmail", supportEmail);


        sendHtmlMessage(Optional.empty(), Optional.empty(), blockedUser, "user-blocked", variables,
                "email.user.blocked.title", Optional.empty());
    }
    @Override
    public void sendUserUnblockedNotification(final User unblockedUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", unblockedUser.getUsername());
        variables.put("baseUrl", baseUrl);


        sendHtmlMessage(Optional.empty(), Optional.empty(), unblockedUser, "user-unblocked", variables,
                "email.user.unblocked.title", Optional.empty());
    }

    @Override
    public void sendValidationEmail(final User user,final String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", user.getUsername());
        variables.put("validationToken", token);
        variables.put("baseUrl", baseUrl);


        sendHtmlMessage(Optional.empty(), Optional.empty(), user, "validation", variables,
                "email.validation.title", Optional.empty());
    }
    @Override
    public void sendForgotPassEmail(final User user, final String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", user.getUsername());
        variables.put("resetToken", token);
        variables.put("baseUrl", baseUrl);


        sendHtmlMessage(Optional.empty(), Optional.empty(), user, "forgot-password", variables,
                "email.reset.title", Optional.empty());
    }


    @Override
    public void sendEventReminderNotification(final Event event, final List<User> attendees) {

        for (User attendee : attendees) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("firstname", attendee.getFirstname());
            variables.put("event", event);
            variables.put("baseUrl", baseUrl);


            sendHtmlMessage(
                    Optional.empty(),
                    Optional.empty(),
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


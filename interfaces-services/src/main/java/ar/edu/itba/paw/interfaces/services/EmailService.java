package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.*;
import java.util.List;


public interface EmailService {


    void answerEventOwnerNotification( String message, EmailUser commenter, EmailEvent event );

    void answerJourneyOwnerNotification( String message, EmailUser commenter, EmailJourney journey  );

    void answerEventNotification(List<EmailUser> oldRepliers, String message, EmailUser commenter, EmailEvent event );

    void answerJourneyNotification(List<EmailUser> oldRepliers, String message, EmailUser commenter, EmailJourney journey  );


    void sendEventDeletionNotification(EmailEvent event, String adminMessage);

    void sendJourneyDeletionNotification(EmailJourney journey, String adminMessage);

    void sendForgotPassEmail(EmailUser user, String token);
    void sendUserBlockedNotification(EmailUser blockedUser);
    void sendEventCommentDeletionNotification(EventResponse deletedComment, EmailEvent event, EmailUser commentAuthor, String adminMessage);
     void sendJourneyCommentDeletionNotification(JourneyResponse deletedComment,  EmailJourney journey, EmailUser commentAuthor , String adminMessage);
     void sendValidationEmail(EmailUser user, String token);
     void sendUserUnblockedNotification(EmailUser unblockedUser);

     void sendEventReminderNotification(EmailEvent event, List<EmailUser> attendees);
}

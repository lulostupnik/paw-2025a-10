package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.*;

import java.util.List;


public interface EmailService {

    void answerEventOwnerNotification( String message, User commenter, Event event );

    void answerJourneyOwnerNotification( String message, User commenter, Journey journey  );

    void answerEventNotification(List<User> oldRepliers, String message, User commenter, Event event );

    void answerJourneyNotification(List<User> oldRepliers, String message, User commenter, Journey journey  );


    void sendEventDeletionNotification(Event event, String adminMessage);

    void sendJourneyDeletionNotification(Journey journey, String adminMessage);

    void sendForgotPassEmail(User user, String token);
    void sendUserBlockedNotification(User blockedUser);
    void sendEventCommentDeletionNotification(EventResponse deletedComment, Event event, User commentAuthor, String adminMessage);
     void sendJourneyCommentDeletionNotification(JourneyResponse deletedComment,  Journey journey, User commentAuthor , String adminMessage);
     void sendValidationEmail(User user, String token);
     void sendUserUnblockedNotification(User unblockedUser);

     void sendEventReminderNotification(Event event, List<User> attendees);
}

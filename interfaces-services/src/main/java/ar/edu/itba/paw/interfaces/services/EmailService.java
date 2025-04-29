package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

import java.util.List;


public interface EmailService {

//    void answerJourneyMail(User emailRecipient, String message, User commenter, Journey journey);

//    void answerEventMail(User emailRecipient, String message, User commenter, Event event);

    void answerEventNotification(List<User> oldRepliers, String message, User commenter, Event event );

    void answerJourneyNotification(List<User> oldRepliers, String message, User commenter, Journey journey  );

}

package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

import java.util.List;


public interface EmailService {

    void answerEventNotification(List<User> oldRepliers, String message, User commenter, Event event );

    void answerJourneyNotification(List<User> oldRepliers, String message, User commenter, Journey journey  );


    void sendEventDeletionNotification(Event event, String adminMessage);

    void sendJourneyDeletionNotification(Journey journey, String adminMessage);

    void sendEventModificationNotification(Event event, String adminMessage);

    void sendJourneyModificationNotification(Journey journey, String adminMessage);

}

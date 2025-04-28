package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

import java.util.List;


public interface EmailService {

    void answerJourneyMail(User emailRecipient, String message, User commenter, Journey journey);

    void answerEventMail(User emailRecipient, String message, User commenter, Event event);

    void answerEventRespondersNotification(List<User> userRecipients, String message, User commenter, Event event );

    void answerJourneyRespondersNotification(List<User> userRecipients, String message, User commenter, Journey journey  );

}


//
//public interface EmailService {
//
//    void answerJourneyMail(String from, String to,
//                                  String firstName, String lastName,
//                                  String username, String career,
//                                  String originUniversity, String message,
//                                  Locale locale,byte[] imageBytes, long journeyId);
//
//    void answerEventMail(String from, String to,
//                                String firstName, String lastName,
//                                String username, String career,
//                                String originUniversity, String message,
//                                Locale locale, byte[] imageBytes, long eventId);
//
//    void answerEventRespondersNotification( String[] bcc,
//                                                  String firstName, String lastName,
//                                                  String username, String career,
//                                                  String originUniversity, String message,
//                                                  Locale locale, byte[] profilePicture,
//                                                  long eventId);
//
//    void answerJourneyRespondersNotification( String[] bcc,
//                                                     String firstName, String lastName,
//                                                     String username, String career,
//                                                     String originUniversity, String message,
//                                                     Locale locale, byte[] profilePicture,
//                                                     long journeyId);
//
//}

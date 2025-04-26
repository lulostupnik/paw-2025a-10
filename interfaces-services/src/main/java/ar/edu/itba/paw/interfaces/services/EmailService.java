package ar.edu.itba.paw.interfaces.services;


import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.valueObjects.EmailContent;
import ar.edu.itba.paw.models.valueObjects.EmailRecipient;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public interface EmailService {

    void answerJourneyMail(EmailRecipient emailRecipient, EmailContent emailContent, Journey journey);

    void answerEventMail(EmailRecipient emailRecipient, EmailContent emailContent, Event event);

    void answerEventRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, Event event );

    void answerJourneyRespondersNotification(List<EmailRecipient> emailRecipients, EmailContent emailContent, Journey journey  );

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

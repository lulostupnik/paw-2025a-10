package ar.edu.itba.paw.interfaces.services;


import java.util.Locale;
import java.util.Map;

public interface EmailService {

    void answerJourneyMail(String from, String to,
                                  String firstName, String lastName,
                                  String username, String career,
                                  String originUniversity, String message,
                                  Locale locale,byte[] imageBytes);

    void answerEventMail(String from, String to,
                                String firstName, String lastName,
                                String username, String career,
                                String originUniversity, String message,
                                Locale locale, byte[] imageBytes);

}

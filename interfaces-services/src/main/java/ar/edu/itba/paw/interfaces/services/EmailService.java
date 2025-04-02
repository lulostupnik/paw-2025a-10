package ar.edu.itba.paw.interfaces.services;


import java.util.Locale;
import java.util.Map;

public interface EmailService {
//    void sendSimpleMessage(String to, String subject, String text);
//    void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables);
    void sendHtmlMessage(String to,
                                String subjectKey,
                                Object[] subjectArgs,
                                String templateName,
                                Map<String, Object> variables,
                                Locale locale);

}

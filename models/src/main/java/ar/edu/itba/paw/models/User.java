package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public class User{
    private final long id;
    private final String email;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final University university;
    private final Career career;
    private final long profilePictureId;
    private final Locale locale;
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{userId: ");
        sb.append(id);
        sb.append(", university: ");
        sb.append(university);
        sb.append(", career: ");
        sb.append(career);
        sb.append(", email: \"");
        sb.append(email);        
        sb.append("\", username: \"");
        sb.append(username);
        sb.append("\", firstname: \"");
        sb.append(firstname);
        sb.append("\", lastname: \"");
        sb.append(lastname);
        sb.append("\", language: \"");
        sb.append(locale);
        sb.append("\", profilePictureId: ");
        sb.append(profilePictureId);

        sb.append("}");
        return sb.toString();
    }
}
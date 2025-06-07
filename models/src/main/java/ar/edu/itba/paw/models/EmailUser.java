package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import lombok.Getter;

import java.util.Locale;

@Getter
public class EmailUser {
    private Long id;
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private University university;
    private Career career;
    private long profilePictureId;
    private Locale locale;
    private boolean isBlocked;
    private boolean validated;

    public EmailUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.university = user.getUniversity();
        this.career = user.getCareer();
        this.profilePictureId = user.getProfilePictureId();
        this.locale = user.getLocale();
        this.isBlocked = user.isBlocked();
        this.validated = user.isValidated();
    }
}

package ar.edu.itba.paw.models;

import lombok.Getter;
import java.util.Locale;

@Getter
public class EmailUser {
    private final Long id;
    private final String email;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String universityName;
    private final String careerName;
    private final Long profilePictureId;
    private final Locale locale;
    private final boolean isBlocked;
    private final boolean validated;

    public EmailUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.universityName = user.getUniversity().getName();
        this.careerName = user.getCareer().getName();
        this.profilePictureId = user.getProfilePictureId();
        this.locale = user.getLocale();
        this.isBlocked = user.isBlocked();
        this.validated = user.isValidated();
    }
}

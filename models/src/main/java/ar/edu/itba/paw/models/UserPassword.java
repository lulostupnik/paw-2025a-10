package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;


@Getter
public class UserPassword extends User{
    private final String password;
    private final String role;

    public UserPassword(long id, String email, String username ,String firstname, String lastname,University university,Career career,long profilePictureId,
                        String password, Locale locale, String role, boolean isBlocked) {
        super(id, email, username, firstname, lastname, university, career, profilePictureId, locale, isBlocked);
        this.password = password;
        this.role = role;
    }


    @Override
    public String toString() {
        return "UserPassword{" +
                "id=" + getId() +
                ", name='" + getFirstname() + '\'' +
                ", lastname='" + getLastname() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}

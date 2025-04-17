package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserPassword extends User{
    private final String password;

    public UserPassword(long id, String email, String username ,String firstname, String lastname,University university,Career career,long profilePictureId , String password) {
        super(id, email, username, firstname, lastname, university, career, profilePictureId);
        this.password = password;
    }
    public String getPassword(){
        return this.password;
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

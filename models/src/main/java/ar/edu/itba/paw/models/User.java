package ar.edu.itba.paw.models;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Getter
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name =
            "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private  String email;
    @Column(name ="username", unique = true, nullable = false, length = 50)
    private  String username;
    @Column(length = 100, nullable = false)
    private  String firstname;
    @Column(length = 100, nullable = false)
    private  String lastname;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private  University university;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private  Career career;
    @Column(name = "profile_picture_id", nullable = false)
    private  long profilePictureId;
    @Column(length=2, nullable = false, name="language")
    private  Locale locale;
    @Column(name="blocked", nullable = false)
    private  boolean isBlocked;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> interests;

    /* For hibernate */ User(){

    }
    public User (final String email, final String username, final String firstname,
                final String lastname, final University university, final Career career,
                final long profilePictureId, final Locale locale, final List<UserInterest> interests) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.university = university;
        this.career = career;
        this.profilePictureId = profilePictureId;
        this.locale = locale;
        this.isBlocked = false;
        this.interests = interests;
    }
    public User (final Long id, final String email, final String username, final String firstname,
                final String lastname, final University university, final Career career,
                final long profilePictureId, final Locale locale, final boolean blocked, final List<UserInterest> interests) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.university = university;
        this.career = career;
        this.profilePictureId = profilePictureId;
        this.locale = locale;
        this.isBlocked = blocked;
        this.interests = interests;
    }


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
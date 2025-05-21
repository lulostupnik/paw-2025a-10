package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
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
    @JoinColumn(name = "university", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private  University university;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "career_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private  Career career;

    @Column(name = "profile_picture_id", nullable = false)
    private  long profilePictureId;

    @Column(length=2, nullable = false, name="language")
    private  Locale locale;

    @Column(name="blocked", nullable = false)
    private  boolean isBlocked;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> interests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    private List<Event> attendedEvents; //@TODO check. ni idea si esta bien.

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
    private Journey journey;

    //Creo que mejor eager
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
//    private Token token;
//



    @Column(name = "password", length = 100)
    private String password;
//    @Column(name = "validated")
//    private boolean validated;
//    @Column(name = "roles")
//    private String role;

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
        this.events = new ArrayList<>();
        this.journey = null;
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
        this.events = new ArrayList<>();
        this.journey = null;
    }


    public User(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.university = university;
        this.career = career;
        this.profilePictureId = profilePictureId;
        this.password = password;
        this.locale = locale;
        this.isBlocked = false;
        this.interests = List.of();
        this.events = new ArrayList<>();
        this.journey = null;
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
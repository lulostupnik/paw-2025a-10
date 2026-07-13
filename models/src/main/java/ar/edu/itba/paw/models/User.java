package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.UserRoles;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Locale;
import java.util.Objects;

@Getter
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;

    @Setter
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private  String email;

    @Setter
    @Column(name ="username", unique = true, nullable = false, length = 50)
    private  String username;

    @Setter
    @Column(length = 100, nullable = false)
    private  String firstname;

    @Setter
    @Column(length = 100, nullable = false)
    private  String lastname;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private  University university;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "career_id", nullable = false)
    private  Career career;

    @Setter
    @Column(name = "profile_picture_id")
    private  Long profilePictureId;

    @Setter
    @Column(length=2, nullable = false, name="language")
    private  Locale locale;

    @Column(name="blocked", nullable = false)
    @Setter
    private  boolean isBlocked;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
    private Journey journey;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL ,orphanRemoval = true, fetch = FetchType.LAZY)
    private Token token;

    @Column(name = "password", length = 100, nullable = false)
    @Setter
    private String password;

    @Column(name = "validated")
    @Setter
    private boolean validated;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", length = 50, nullable = false)
    private UserRoles role;

    public boolean hasActiveJourney() {
        return journey != null && !journey.isDeleted();
    }

    /* For hibernate */ User(){

    }
    public User (final String email, final String username, final String firstname,
                final String lastname, final University university, final Career career,
                final Long profilePictureId, final Locale locale,final boolean validated) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.university = university;
        this.career = career;
        this.profilePictureId = profilePictureId;
        this.locale = locale;
        this.isBlocked = false;
        this.journey = null;
        this.validated = validated;
        this.role =  UserRoles.USER;
    }
    public User (final Long id, final String email, final String username, final String firstname,
                final String lastname, final University university, final Career career,
                final Long profilePictureId, final Locale locale, final boolean blocked,final boolean validated) {
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
        this.journey = null;
        this.validated = validated;
        this.role = UserRoles.USER;

    }
    public User (final Long id, final String email, final String username, final String firstname,
                final String lastname, final University university, final Career career, final Journey journey,
                final Long profilePictureId, final Locale locale, final boolean blocked,final boolean validated) {
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
        this.journey = journey;
        this.validated = validated;
        this.role = UserRoles.USER;

    }


    public User(String email, String username, String firstname, String lastname, University university, Career career, Long profilePictureId, String password, Locale locale, boolean validated) {
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
        this.journey = null;
        this.validated = validated;
        this.role = UserRoles.USER;

    }

    @Override
    public String toString() {
        return "{userId: " +
                id +
                ", email: \"" +
                email +
                "\", username: \"" +
                username +
                "\", firstname: \"" +
                firstname +
                "\", lastname: \"" +
                lastname +
                "\", language: \"" +
                locale +
                "\", profilePictureId: " +
                profilePictureId +
                "}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return id != null && id.equals(user.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                email,
                username,
                firstname,
                lastname,
                profilePictureId,
                locale,
                isBlocked,
                validated,
                role,
                university != null ? university.getId() : null,
                career != null ? career.getId() : null,
                journey != null ? journey.getId() : null
        );
    }




}
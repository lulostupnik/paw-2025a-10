package ar.edu.itba.paw.models;

public class User{
    private final int id;
    private final String email;
    private final String username;
    // private final String password;
    private final String firstname;
    private final String lastname;
    private final University university;
    private final String career;
    // private final int profilePictureId;

    public User(int id, String email, String username, /* String password,*/ String firstname, String lastname, University university, String career/*,  int profilePictureId */){
        this.id = id;
        this.email = email;
        this.username = username;
        // this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.university = university;
        this.career = career;
        // this.profilePictureId = profilePictureId;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    /*
    public String getPassword() {
        return password;
    }
    */

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public University getUniversity() {
        return university;
    }

    public String getCareer() {
        return career;
    }

    /*
    public int getProfilePictureId() {
        return profilePictureId;
    }
    */
}
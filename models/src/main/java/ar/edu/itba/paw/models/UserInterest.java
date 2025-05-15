package ar.edu.itba.paw.models;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "user_interest")
public class UserInterest {

    @EmbeddedId
    private UserInterestId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Interest interest;

    @Column(nullable = false)
    private int score = 0;

    public UserInterest() {}

    public UserInterest(User user, Interest category, int score) {
        this.user = user;
        this.interest = category;
        this.score = score;
        this.id = new UserInterestId(user.getId(), category.getId());
    }

}

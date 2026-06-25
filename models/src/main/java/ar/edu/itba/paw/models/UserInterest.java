package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@Table(name = "user_interest")
public class UserInterest {

    @EmbeddedId
    private UserInterestId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false)
    private Interest interest;

    @Setter
    @Column(nullable = false)
    private int score = 0;

    public UserInterest() {}
    public UserInterest(final User user, final Interest category) {
        this.user = user;
        this.interest = category;
        this.id = new UserInterestId(user.getId(), category.getId());
    }

    public UserInterest(User user, Interest category, int score) {
        this.user = user;
        this.interest = category;
        this.score = score;
        this.id = new UserInterestId(user.getId(), category.getId());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInterest that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

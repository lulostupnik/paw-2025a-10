package ar.edu.itba.paw.models;

import lombok.Getter;

import java.io.Serializable;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
public class UserInterestId implements Serializable {
    private Long userId;
    private Long categoryId;

    public UserInterestId() {}

    public UserInterestId(Long userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }

    // equals() y hashCode() son necesarios para claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInterestId)) return false;
        UserInterestId that = (UserInterestId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, categoryId);
    }  //todo preguntar. segun una sucinta investigacion, puede llegar a estar mal en JPA.

    // getters y setters
}


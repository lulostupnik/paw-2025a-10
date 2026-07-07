package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_id_seq")
    @SequenceGenerator(sequenceName = "tokens_id_seq", name = "tokens_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Setter
    @Column(length = 128, nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Column(name = "token_expiration",nullable = false)
    private LocalDateTime expirationDate;

    /* para hibernate*/ Token() {

    }

    public Token(User user, String token, LocalDateTime expirationDate) {
        this.user = user;
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public Token(long id, User user, String token, LocalDateTime expirationDate) {
        this.user = user;
        this.token = token;
        this.expirationDate = expirationDate;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token1)) return false;
        return id != null && id.equals(token1.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }


}

package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_id_seq")
    @SequenceGenerator(sequenceName = "tokens_id_seq", name = "tokens_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private Long tokenId;

    @Setter
    @Column(length = 100, nullable = false, unique = true)
    private String token;  //todo En la BD no esta como NOT NULL el token pero deberia serlo.

    //TODO: Creo que eager, porque no tiene sentido cargar el token sin el usuario
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //TODO:Esto esta bueno cambiarlo de LocalDate porque asi es mas preciso, no?
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
        this.tokenId = id;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }


}

package ar.edu.itba.paw.models;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "tokens_token_id_seq")
    @SequenceGenerator(sequenceName = "tokens_token_id_seq",
            name = "tokens_token_id_seq",
            allocationSize = 1)
    @Column(name = "token_id", nullable = false, updatable = false)
    private long tokenId;

    @Column(length = 32, nullable = false)
    private String token;

    //TODO: Creo que eager, porque no tiene sentido cargar el token sin el usuario
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    //TODO:Esto esta bueno cambiarlo de LocalDate porque asi es mas preciso, no?
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /* para hibernate*/ Token() {

    }

    public Token(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

}

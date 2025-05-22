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
    @Column(length = 32, nullable = false)
    private String token;

    //TODO: Creo que eager, porque no tiene sentido cargar el token sin el usuario
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }


}

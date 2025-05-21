package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional
public class TokenHibernateDao implements TokenDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Token create(User user, String token, LocalDateTime expirationDate) {
        Token newToken = new Token(user, token, expirationDate);
        entityManager.persist(newToken);
        return newToken;
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return entityManager.createQuery("FROM Token t WHERE t.token = :token", Token.class)
                .setParameter("token", token)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Token> findByUserId(long userId) {
        return entityManager.createQuery("FROM Token WHERE user.id = :userId", Token.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }


    @Override
    public void deleteByToken(Token token) {
        final Token tkn = entityManager.getReference(Token.class, token.getTokenId());
        entityManager.remove(tkn);
        entityManager.flush();
        //Ver tema token no se encuentra en la base de datos
        //no deberia llegar a este punto
    }



    @Override
    public void deleteExpiredTokens() {
        entityManager.createQuery("DELETE FROM Token t WHERE t.expirationDate < :currentDate")
                .setParameter("currentDate", LocalDateTime.now())
                .executeUpdate();
    }
}

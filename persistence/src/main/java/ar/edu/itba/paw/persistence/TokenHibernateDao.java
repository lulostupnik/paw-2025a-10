package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
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
        return entityManager.createQuery(
                        "FROM Token t WHERE t.token = :token AND t.expirationDate IS NOT NULL AND t.expirationDate >= :now", Token.class)
                .setParameter("token", token)
                .setParameter("now", LocalDateTime.now())
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
            Token managedToken = entityManager.find(Token.class, token.getId());
            if (managedToken != null) {
                User user = managedToken.getUser();
                user.setToken(null);
                entityManager.flush();
            }
    }



    @Override
    public void deleteExpiredTokens() {
        entityManager.createQuery("DELETE FROM Token t WHERE t.expirationDate < :currentDate")
                .setParameter("currentDate", LocalDateTime.now())
                .executeUpdate();
    }
}

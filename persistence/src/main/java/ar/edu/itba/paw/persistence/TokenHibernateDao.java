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
        return entityManager.createQuery("FROM Token WHERE token = :token", Token.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Token> findByUserId(long userId) {
        return entityManager.createQuery("FROM Token WHERE user = :userId", Token.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsByToken(String token) {
        return entityManager.createQuery("SELECT COUNT(t) > 0 FROM Token t WHERE t.token = :token", Boolean.class)
                .setParameter("token", token)
                .getSingleResult();
    }

    @Override
    public boolean existsByTokenAndNotExpired(String token) {
        return entityManager.createQuery("SELECT COUNT(t) > 0 FROM Token t WHERE t.token = :token AND t.expirationDate > CURRENT_DATE", Boolean.class)
                .setParameter("token", token)
                .getSingleResult();
    }

    @Override
    public void deleteByToken(String token) {
        entityManager.createQuery("DELETE FROM Token WHERE token = :token")
                .setParameter("token", token)
                .executeUpdate();
    }

    @Override
    public void deleteByUserId(long userId) {
        entityManager.createQuery("DELETE FROM Token WHERE user = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public void updateExpiration(String token, LocalDateTime newExpirationDate) {
        entityManager.createQuery("UPDATE Token SET expiryDate = :newExpirationDate WHERE token = :token")
                .setParameter("newExpirationDate", newExpirationDate)
                .setParameter("token", token)
                .executeUpdate();
    }
}

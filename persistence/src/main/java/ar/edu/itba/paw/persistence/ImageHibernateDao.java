package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageHibernateDao implements ImageDao {
    @PersistenceContext
    private EntityManager em;


    @Override
    public long create(byte[] imageData) {
        final Image image = new Image(imageData);
        em.persist(image);
        return image.getId();
    }

    @Override
    public Optional<Image> findById(long id) {
        return  Optional.ofNullable(em.find(Image.class, id));

    }

    @Override
    public void delete(long id) {
        final Image image = em.find(Image.class, id);
        if (image != null) {
            em.remove(image);
        }
    }
}

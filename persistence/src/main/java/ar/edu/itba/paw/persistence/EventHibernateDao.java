package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Repository
public class EventHibernateDao implements EventDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Event create(User user, City city, LocalDate date, String description, long flyerImageId,
                        String title, LocalTime time, String address, Integer attendeesLimit) {
        Event event = new Event(user, date, description, flyerImageId, city, title, time, address, attendeesLimit);
        em.persist(event);
        return event;
    }

    @Override
    public Optional<Event> findById(long eventId) {
        return Optional.ofNullable(em.find(Event.class, eventId));
    }

    @Override
    public void delete(long id) {
        final Event event = em.find(Event.class, id);

        if (event == null){
            LOGGER.error("Event {} not found for deletion", id );
            return;
        }
        if(event.isDeleted()){
            LOGGER.error("Event {} was already deleted", id );
            return;
        }

        event.setDeleted(true);
        em.merge(event);
    }

}

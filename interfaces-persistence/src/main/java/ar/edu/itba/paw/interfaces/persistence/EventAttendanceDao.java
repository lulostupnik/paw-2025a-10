package ar.edu.itba.paw.interfaces.persistence;

public interface EventAttendanceDao {
    void create(long userId, long eventId);
    void delete(long userId, long eventId);
    boolean exists(long userId, long eventId);
    int countByEventId(long eventId);

}
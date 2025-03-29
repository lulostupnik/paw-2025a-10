package ar.edu.itba.paw.interfaces.persistence;

public interface JourneyResponseDao {
    JourneyResponseDao create(long user_id, long journey_id, String message);
}

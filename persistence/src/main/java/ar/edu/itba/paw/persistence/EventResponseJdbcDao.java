package ar.edu.itba.paw.persistence;

import java.time.LocalDateTime;
import java.util.*;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.valueObjects.EmailContent;
import ar.edu.itba.paw.models.valueObjects.EmailRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;

@Repository
public class EventResponseJdbcDao implements EventResponseDao {
    private static Logger LOGGER = LoggerFactory.getLogger(EventResponseJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<EventResponse> EVENT_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new EventResponse(
            rs.getLong("user_id"), // Event ID from `events` table
            rs.getString("username"),
            rs.getLong("event_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );

    private static final RowMapper<EmailRecipient> EMAIL_RECIPIENT_ROW_MAPPER = (rs, rowNum) -> EmailRecipient.builder().toEmail(rs.getString("email")).locale(  Locale.of(rs.getString("language"))).build();


/*
    private static final RowMapper<User> EVENT_USER_RESPONDERS_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            new University(rs.getLong("university_id"), rs.getString("university_name"), rs.getString("abbreviation"),
                                new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id"))),
            new Career(rs.getLong("career_id"), rs.getString("career_name")),
            rs.getLong("profile_picture_id"),
            Locale.of(rs.getString("language"))
    );*/

    private static final String QUERY_BY_EVENT_ID = """
        SELECT er.user_id, us.username as username, er.event_id, er.message, er.date_time\s
        FROM event_responses er\s
        JOIN users us\s
        ON er.user_id = us.id\s
        WHERE er.event_id = ?""";

    private static final String NOT_DELETED = " AND er.deleted = FALSE";

   /* private static final String QUERY_BY_EVENT_ID_GET_USERS = """
            SELECT distinct er.user_id as user_id, us.email, us.username, us.firstname, us.lastname, us.username, us.career_id, us.profile_picture_id, us.language
                , uni.id as university_id , uni.name as university_name, uni.abbreviation,
               ci.id as city_id, ci.name as city_name,
               ci.country_id as country_id, co.name as country_name,
               ca.id as career_id, ca.name as career_name
        FROM event_responses er
                 JOIN users us
                      ON er.user_id = us.id
                 JOIN universities as uni
                      on us.university = uni.id
                 JOIN cities as ci
                      on uni.city_id = ci.id
                 JOIN careers as ca
                      on ca.id = us.career_id
                 JOIN countries as co
                        on co.id = ci.country_id
        WHERE er.event_id = ?""";

*/


    @Autowired
    public EventResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("event_responses")
                                                            .usingGeneratedKeyColumns("id");
    }


    @Override
    public EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime) {
        LOGGER.debug("Registering new event response for event {} by user {} ({}) who says {} on {}", eventId, userId, username, message, dateTime);
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        args.put("date_time", dateTime);

        jdbcInsert.execute(args);
        LOGGER.debug("Successfully registered event response");
        return new EventResponse(userId, username, eventId, message, dateTime);
    }

    @Override
    public List<EventResponse> listAllFromEvent(long eventId){
        LOGGER.debug("Querying DB for replies to event {}", eventId);
        return jdbcTemplate.query(QUERY_BY_EVENT_ID + NOT_DELETED+ " ORDER BY date_time ", EVENT_RESPONSE_ROW_MAPPER, eventId);
    }

   /* @Override
    public List<User> listAllUsersResponders(long eventId){
        LOGGER.debug("Querying DB for list of users that replied to event {}", eventId);
        return jdbcTemplate.query(QUERY_BY_EVENT_ID_GET_USERS, EVENT_USER_RESPONDERS_ROW_MAPPER, eventId);
    }*/

//    @Override
//    public List<User> listAllUsersRespondersMinusUsers(long eventId, List<Long> user_ids){
//        StringBuilder query = new StringBuilder(QUERY_BY_EVENT_ID_GET_USERS);
//        for(int i=0; i<user_ids.size(); i++){
//            query.append(" and er.user_id !=").append(user_ids.get(i));
//        }
//        return jdbcTemplate.query(query.toString(), EVENT_USER_RESPONDERS_ROW_MAPPER, eventId);
//    }

    /*@Override
    public String[] listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds) {
        StringBuilder query = new StringBuilder("""
            SELECT DISTINCT us.email
            FROM event_responses er
                     JOIN users us ON er.user_id = us.id
            WHERE er.event_id = ?
        """);

        for (Long userId : userIds) {
            query.append(" AND er.user_id != ").append(userId);
        }

        List<String> emails = jdbcTemplate.queryForList(query.toString(), String.class, eventId);
        return emails.toArray(new String[0]);
    }*/
//    @Override
//    public String[] listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds) {
//        StringBuilder query = new StringBuilder("""
//        SELECT DISTINCT us.email
//        FROM event_responses er
//        JOIN users us ON er.user_id = us.id
//        WHERE er.event_id = ?
//    """);
//
//        List<Object> params = new ArrayList<>();
//        params.add(eventId);
//
//        if (userIds != null && !userIds.isEmpty()) {
//            query.append(" AND er.user_id NOT IN (");
//            query.append("?,".repeat(userIds.size()));
//            query.setLength(query.length() - 1); // Remove last comma
//            query.append(")");
//            params.addAll(userIds);
//        }
//
//        List<String> emails = jdbcTemplate.queryForList(query.toString(), String.class, params.toArray());
//        return emails.toArray(new String[0]);
//    }


    @Override
    public List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds) {
        StringBuilder query = new StringBuilder("""
        SELECT DISTINCT us.email, us.language
        FROM event_responses er
        JOIN users us ON er.user_id = us.id
        WHERE er.event_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(eventId);

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" AND er.user_id NOT IN (");
            query.append("?,".repeat(userIds.size()));
            query.setLength(query.length() - 1); // Remove last comma
            query.append(")");
            params.addAll(userIds);
        }

        List<EmailRecipient> emails = jdbcTemplate.query(query.toString(),EMAIL_RECIPIENT_ROW_MAPPER, params.toArray());
        return emails;
    }

    @Override
    public void delete(long id) {
        final String query = "UPDATE event_responses SET deleted = TRUE WHERE id = ?;";
        int updatedRows = jdbcTemplate.update(query, id);

        if (updatedRows == 0) {
            // Optionally log or throw an exception if no rows were updated
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }



}

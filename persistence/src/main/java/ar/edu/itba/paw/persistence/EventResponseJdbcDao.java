package ar.edu.itba.paw.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;

import static ar.edu.itba.paw.persistence.JdbcDaoUtils.offset;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.pageCount;

@Repository
public class EventResponseJdbcDao implements EventResponseDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventResponseJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<EventResponse> EVENT_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new EventResponse(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getLong("event_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );
    private static final String SQL_EVENT_RESPONSE_BASE =
            """
            SELECT er.id AS id, er.user_id, us.username AS username, er.event_id, er.message, er.date_time
            FROM event_responses er
            JOIN users us ON er.user_id = us.id
            """;
    private static final String SQL_FIND_EVENT_RESPONSE = SQL_EVENT_RESPONSE_BASE + " WHERE er.deleted = FALSE AND er.id = ?";
    private static final String SQL_LIST_ALL_BY_EVENT = SQL_EVENT_RESPONSE_BASE + " WHERE er.deleted = FALSE AND er.event_id = ? ORDER BY date_time";
    private static final String SQL_FIND_EVENT_RESPONSE_DELETED_OR_NOT = SQL_EVENT_RESPONSE_BASE + " WHERE er.id = ?";

    private static final String SQL_LIST_ALL_BY_EVENT_PAGED = SQL_LIST_ALL_BY_EVENT + " LIMIT ? OFFSET ?";





    @Autowired
    public EventResponseJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("event_responses")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<EventResponse> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_EVENT_RESPONSE, EVENT_RESPONSE_ROW_MAPPER, id).stream().findFirst();
    }
    @Override
    public Optional<EventResponse> findByIdDeletedOrNotDeleted(final long id) {
        return jdbcTemplate.query(SQL_FIND_EVENT_RESPONSE_DELETED_OR_NOT, EVENT_RESPONSE_ROW_MAPPER, id).stream().findFirst();
    }


    @Override
    public EventResponse create(final long userId, final String username, final long eventId, final String message, final LocalDateTime dateTime) {
        LOGGER.debug("Registering new event response for event {} by user {} ({}) who says {} on {}", eventId, userId, username, message, dateTime);
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        args.put("date_time", Timestamp.valueOf(dateTime));
        args.put("deleted", false);

        final Number keys = jdbcInsert.executeAndReturnKey(args);
        final EventResponse response = new EventResponse(keys.longValue(), userId, username, eventId, message, dateTime);
        LOGGER.info("Successfully registered event response {}", response);
        return response;
    }

    @Override
    public List<EventResponse> listAllFromEvent(final long eventId){
        return jdbcTemplate.query(SQL_LIST_ALL_BY_EVENT, EVENT_RESPONSE_ROW_MAPPER, eventId);
    }

    @Override
    public int getCount(final long eventId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_responses WHERE event_id = ? AND deleted = FALSE",
                Integer.class,
                eventId
        );
    }

    @Override
    public long getEventIdByResponseId(final long eventResponseId) {
        return jdbcTemplate.query(
                "SELECT event_id FROM event_responses WHERE id = ? ORDER BY date_time ",
                (rs, rowNum) -> rs.getLong("event_id"),
                eventResponseId
        ).getFirst();
    }

    @Override
    public void deletionMessage(final long id, final String message) {
        LOGGER.info("Setting event response delete message '{}' for response {}", message, id);
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No event_response found with id {}", id);
        }
    }

    @Override
    public void deleteByEventId(long eventId) {
        LOGGER.info("Setting event responses for event {} as deleted", eventId);
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted = TRUE WHERE event_id = ?;", eventId);
        if (updatedRows == 0) {
            LOGGER.warn("No event found with id {}", eventId);
        }
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Setting event response {} as deleted", id);
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No event_response found with id {}", id);
        }
    }

    @Override
    public Page<EventResponse> listAllFromEvent(final long eventId, PageParams pageParams) {

        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_responses WHERE event_id = ? AND deleted = FALSE",
                Integer.class,
                eventId
        );

        return new Page<>(
                jdbcTemplate.query(SQL_LIST_ALL_BY_EVENT_PAGED, EVENT_RESPONSE_ROW_MAPPER, eventId, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalItems, pageParams.getSize())
        );
    }

}


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


    /*@Override
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
    }*/

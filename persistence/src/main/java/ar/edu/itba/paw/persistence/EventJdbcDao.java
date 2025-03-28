package ar.edu.itba.paw.persistence;
//
//import ar.edu.itba.paw.interfaces.persistence.EventDao;
//import ar.edu.itba.paw.models.Event;
//import ar.edu.itba.paw.models.University;
//import ar.edu.itba.paw.models.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.Date;

//import java.util.List;
//import java.util.Map;
//




//NO FUE TESTEADO, NO SE SI ANDA




//public class EventJdbcDao implements EventDao {
//
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert jdbcInsert;
//
//    private final static RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
//            rs.getLong("event_id"),
//            null,
//            null,
//            rs.getDate("event_date"),
//            null,
//            rs.getString("event_description")
//    );
//
//
//    @Autowired
//    public EventJdbcDao(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("events")
//                .usingGeneratedKeyColumns("id");
//    }
//
//    public EventJdbcDao(JdbcTemplate jdbcTemplate, SimpleJdbcInsert jdbcInsert) {
//        this.jdbcTemplate = jdbcTemplate;
//        this.jdbcInsert = jdbcInsert;
//    }
//
//    @Override
//    public Event create(long userId, long cityId, Date date, String description, long flyerImageId) {
//        final Map<String, Object> parameters = Map.of(
//                "user_id", userId,
//                "city_id", cityId,
//                "event_date", date,
//                "event_description", description,
//                "flyer_image_id", flyerImageId
//                );
//        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
//        //VER TEMA JOIN PUSE NULL PORQUE SINO ROMPE
//        return new Event(keys.longValue(),null,null,date,null,description);
//    }
//
//    @Override
//    public List<Event> listByQuery(Long cityId, Date date) {
//        StringBuilder sqlBuilder = new StringBuilder(
//                "SELECT * FROM events e"
//        );
//        List<Object> params = new ArrayList<>();
//        boolean firstCondition = true;
//        if (cityId != null) {
//            sqlBuilder.append("WHERE")
//                    .append(" e.city_id = ?");
//            params.add(cityId);
//            firstCondition = false;
//        }
//
//        if (date != null) {
//            sqlBuilder.append(firstCondition ? " WHERE" : " AND")
//                    .append(" e.date AFTER ?");
//            params.add(date);
//        }
//
//        return jdbcTemplate.query(
//                sqlBuilder.toString(),
//                EVENT_ROW_MAPPER,
//                params.toArray()
//        );
//    }
//    }

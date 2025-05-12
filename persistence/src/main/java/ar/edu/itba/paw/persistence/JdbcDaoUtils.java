package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;

class JdbcDaoUtils {

    private JdbcDaoUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String likePattern(String text) {
        if (text == null || text.isEmpty()) {
            return "%";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("%").append(text.replace("%", "\\%").replace("_", "\\_")).append("%");
        return sb.toString();
    }


    public static int offset(PageParams pageParams) {
        return (Math.max(1, pageParams.getPage()) - 1) * pageParams.getSize();
    }

    public static int pageCount(int total, int size) {
        return (int) Math.ceil((double) total / size);
    }


    public static <T> Page<T> executePagedQuery(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper, String countQuery,
                                                String query, PageParams pageParams, Object... params) {

        int elementCount = jdbcTemplate.queryForObject(countQuery, Integer.class, params);

        Object[] allParams = new Object[params.length + 2];
        System.arraycopy(params, 0, allParams, 0, params.length);
        allParams[params.length] = pageParams.getSize();
        allParams[params.length + 1] = offset(pageParams);

        List<T> list = jdbcTemplate.query(query, rowMapper, allParams);


        return new Page<>(list, pageParams.getPage(), pageCount(elementCount, pageParams.getSize()));
    }



}
package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HibernateDaoUtils {

    private HibernateDaoUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String likePattern(String text) {
        if (text == null || text.isEmpty()) {
            return "%";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("%").append(text.replace("%", "\\%").replace("_", "\\_").toLowerCase()).append("%");

        return sb.toString();
    }


    public static int offset(PageParams pageParams) {
        return (Math.max(1, pageParams.getPage()) - 1) * pageParams.getSize();
    }

    public static int pageCount(int total, int size) {
        return (int) Math.ceil((double) total / size);
    }
    public static <T> Page<T> fetchPageByIds(
            EntityManager em,
            String countSql,
            String idSql,
            Map<String, Object> parameters,
            String jpqlFetchById,
            Class<T> clazz,
            PageParams pageParams
    ) {
        // Count total
        Query countQuery = em.createNativeQuery(countSql);
        parameters.forEach(countQuery::setParameter);
        int totalItems = ((Number) countQuery.getSingleResult()).intValue();

        // ID query with pagination
        Query idQuery = em.createNativeQuery(idSql);
        parameters.forEach(idQuery::setParameter);
        idQuery.setMaxResults(pageParams.getSize());
        idQuery.setFirstResult(offset(pageParams)); // modularized offset

        List<Long> ids = ((List<?>) idQuery.getResultList()).stream()
                .filter(Number.class::isInstance) // Ensure type safety
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return new Page<>(List.of(), pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
        }

        TypedQuery<T> fetchQuery = em.createQuery(jpqlFetchById, clazz);
        fetchQuery.setParameter("ids", ids);
        List<T> results = fetchQuery.getResultList();

        return new Page<>(results, pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
    }


}
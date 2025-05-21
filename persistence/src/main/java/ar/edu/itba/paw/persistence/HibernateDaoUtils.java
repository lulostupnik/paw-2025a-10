package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HibernateDaoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDaoUtils.class);

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
            PageParams pageParams //@TODO podria recibir el nombre SQL de los ids.
    ) {
        // Count total
        Query countQuery = em.createNativeQuery(countSql);
        LOGGER.error("Count SQL: {}", countSql);
        parameters.forEach(countQuery::setParameter);
        LOGGER.error("Count SQL parameters: {}", parameters);
        int totalItems = ((Number) countQuery.getSingleResult()).intValue();
        LOGGER.error("Total items: {}", totalItems);


        // ID query with pagination
        Query idQuery = em.createNativeQuery(idSql);
        LOGGER.error("ID SQL: {}", idSql);
        parameters.forEach(idQuery::setParameter);
        LOGGER.error("ID SQL parameters: {}", parameters);
        idQuery.setMaxResults(pageParams.getSize());
        LOGGER.error("ID SQL max results: {}", pageParams.getSize());
        idQuery.setFirstResult(offset(pageParams)); // modularized offset
        LOGGER.error("ID SQL first result: {}", offset(pageParams));

        List<Long> ids = ((List<?>) idQuery.getResultList()).stream()
                .filter(Number.class::isInstance) // Ensure type safety
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());
        LOGGER.error("IDs: {}", ids);

        if (ids.isEmpty()) {
            LOGGER.error("No IDs found");
            return new Page<>(List.of(), pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
        }

        TypedQuery<T> fetchQuery = em.createQuery(jpqlFetchById, clazz);
        LOGGER.error("Fetch SQL: {}", jpqlFetchById);
        fetchQuery.setParameter("ids", ids);
        LOGGER.error("Fetch SQL parameters: {}", ids);
        List<T> results = fetchQuery.getResultList();
        LOGGER.error("Results: {}", results);

        return new Page<>(results, pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
    }


}
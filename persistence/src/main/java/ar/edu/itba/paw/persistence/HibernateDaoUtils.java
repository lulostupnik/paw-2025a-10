package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class HibernateDaoUtils {


    private HibernateDaoUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String likePattern(String text) {
        if (text == null || text.isEmpty()) {
            return "%";
        }
        return "%%%s%%".formatted(text.replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_")
                        .toLowerCase()
        );
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
            PageParams pageParams){
        return fetchPageByIds(em,countSql,idSql,parameters,jpqlFetchById,clazz,pageParams,Map.of());
    }

    public static <T> Page<T> fetchPageByIds(
            EntityManager em,
            String countSql,
            String idSql,
            Map<String, Object> parameters,
            String jpqlFetchById,
            Class<T> clazz,
            PageParams pageParams,
            Map<String, Object> fetchParameters
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

        List<?> rawResults = idQuery.getResultList();
        List<Long> ids = new ArrayList<>();

        for (Object result : rawResults) {
            if (result != null) {
                if (!(result instanceof Number)) {
                    continue;
                }
                ids.add(((Number) result).longValue());

            }
        }

        if (ids.isEmpty()) {
            return new Page<>(List.of(), pageParams.getPage(), pageParams.getSize(), totalItems);
        }

        TypedQuery<T> fetchQuery = em.createQuery(jpqlFetchById, clazz);
        fetchQuery.setParameter("ids", ids);
        fetchParameters.forEach(fetchQuery::setParameter);
        List<T> results = fetchQuery.getResultList();

        return new Page<>(results, pageParams.getPage(), pageParams.getSize(), totalItems);
    }





}
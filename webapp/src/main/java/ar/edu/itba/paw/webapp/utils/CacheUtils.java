package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.*;
import java.util.function.Supplier;


public final class CacheUtils {

    public static final int ONE_YEAR = 365 * 24 * 60 * 60;
    public static final int ONE_MONTH = 30 * 24 * 60 * 60;
    public static final int ONE_WEEK = 7 * 24 * 60 * 60;
    public static final int ONE_DAY = 24 * 60 * 60;
    public static final int ONE_HOUR = 60 * 60;

    private CacheUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }


    public static Response.ResponseBuilder withMaxAge(Response.ResponseBuilder builder, int seconds) {
        CacheControl cache = new CacheControl();
        cache.setMaxAge(seconds);
        return builder.cacheControl(cache);
    }


    public static <T> Response withEtag(Request req, Object entity, Supplier<T> supplier) {
        return withEtagAndMaxAge(req, entity, supplier, 0);
    }


    public static <T> Response withEtagAndMaxAge(Request req, Object entity, Supplier<T> supplier, int seconds) {
        CacheControl cache = new CacheControl();

        if (seconds > 0) {
            cache.setMaxAge(seconds);
        } else {
            cache.setNoCache(true);
        }

        EntityTag etag = new EntityTag(Integer.toString(entity.hashCode()));
        Response.ResponseBuilder builder = req.evaluatePreconditions(etag);

        if (builder == null) {
            builder = Response.ok(supplier.get()).tag(etag);
        }

        return builder
                .cacheControl(cache)
                .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                .build();
    }
}

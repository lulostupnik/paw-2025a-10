package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;


public final class CacheUtils {

    public static final int ONE_YEAR = 365 * 24 * 60 * 60;

    private CacheUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }


    public static <T> Response withEtag(Request req, Object entity, Supplier<T> supplier) {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        return etagResponse(req, entity, supplier, cache);
    }



    public static <T> Response withEtag(Request req, Object entity, Object variant, Supplier<T> supplier) {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        return etagResponse(req, Objects.hash(etagHash(entity), etagHash(variant)), supplier, cache);
    }

    public static <T> Response privateWithEtag(Request req, Object entity, Object variant, Supplier<T> supplier) {
        CacheControl cache = new CacheControl();
        cache.setPrivate(true);
        cache.setNoCache(true);
        return etagResponse(req, Objects.hash(etagHash(entity), etagHash(variant)), supplier, cache);
    }

    public static Response withEtag(Request req, Object entity, Response.ResponseBuilder builder) {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        EntityTag etag = new EntityTag(Integer.toString(etagHash(entity)));
        Response.ResponseBuilder notModified = req.evaluatePreconditions(etag);
        if (notModified != null) {
            builder = notModified;
        }
        return builder
                .cacheControl(cache)
                .tag(etag)
                .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                .build();
    }

    public static <T> Response withLastModified(Request req, LocalDateTime lastModifiedLdt, Supplier<T> supplier) {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        Date lastModified = Date.from(
                lastModifiedLdt.truncatedTo(ChronoUnit.SECONDS)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());

        Response.ResponseBuilder builder = req.evaluatePreconditions(lastModified);
        if (builder == null) {
            builder = Response.ok(supplier.get());
        }
        return builder
                .cacheControl(cache)
                .lastModified(lastModified)
                .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                .build();
    }

    private static <T> Response etagResponse(Request req, Object entity, Supplier<T> supplier, CacheControl cache) {
        EntityTag etag = new EntityTag(Integer.toString(etagHash(entity)));
        Response.ResponseBuilder builder = req.evaluatePreconditions(etag);

        if (builder == null) {
            builder = Response.ok(supplier.get());
        }

        return builder
                .cacheControl(cache)
                .tag(etag)
                .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                .build();
    }

    private static int etagHash(Object entity) {
        switch (entity) {
            case null -> {
                return 0;
            }
            case Iterable<?> iterable -> {
                int result = 1;
                for (Object item : iterable) {
                    result = 31 * result + etagHash(item);
                }
                return result;
            }
            default -> {
            }
        }
        return entity.hashCode();
    }
}

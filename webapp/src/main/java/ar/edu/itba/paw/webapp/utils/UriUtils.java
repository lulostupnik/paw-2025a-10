package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public final class UriUtils {
    public static final String API_BASE_URL = "/api";
    public static final String USERS_URL = API_BASE_URL + "/users";


    public static URI getUsersUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).build();
    }

    public static URI getUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).path(String.valueOf(userId)).build();
    }


}

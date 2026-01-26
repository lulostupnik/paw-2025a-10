package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.Page;

public class PagingUtils {
    public static <T> ResponseBuilder insertPaginationLinks(ResponseBuilder responseBuilder, UriInfo uriInfo, Page<T> page){
        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder().replaceQueryParam("page", "{page}");
        if (page.getTotalPages() < page.getCurrentPage())
            responseBuilder.link(uriBuilder.build( page.getCurrentPage() + 1), "next");
        if (page.getCurrentPage() > 1)
            responseBuilder.link(uriBuilder.build( page.getCurrentPage() - 1), "prev");
        responseBuilder.link(uriBuilder.build(1), "first");
        responseBuilder.link(uriBuilder.build(page.getTotalPages()), "last");
        
        return responseBuilder;
    }
}
package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.paw.models.Page;

public class PagingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PagingUtils.class);

    public static <T> ResponseBuilder insertPaginationLinks(ResponseBuilder responseBuilder, UriInfo uriInfo, Page<T> page){
        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder().replaceQueryParam("page", "{page}");
        LOGGER.warn("" + page.getTotalPages() + page.getCurrentPage());
        if (page.getTotalPages() > page.getCurrentPage())
            responseBuilder.link(uriBuilder.build( page.getCurrentPage() + 1), "next");
        if (page.getCurrentPage() > 1)
            responseBuilder.link(uriBuilder.build( page.getCurrentPage() - 1), "prev");
        responseBuilder.link(uriBuilder.build(1), "first");
        responseBuilder.link(uriBuilder.build(page.getTotalPages()), "last");
        
        return responseBuilder;
    }
}
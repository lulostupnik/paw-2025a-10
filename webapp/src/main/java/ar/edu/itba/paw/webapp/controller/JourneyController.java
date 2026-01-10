package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.dto.JourneyDto;
import ar.edu.itba.paw.webapp.dto.JourneyResponseDto;
import ar.edu.itba.paw.webapp.dto.TipDto;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("journeys")
@Component
public class JourneyController {

    @Autowired
    private JourneyService journeyService;

    @Autowired
    private AccessHelper accessHelper;

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listJourneys(
            @QueryParam("destination") String destination,
            // @QueryParam("city") String city, // TODO: ver después
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("interest") String interest,
            @QueryParam("upcoming") @DefaultValue("false") boolean upcoming,
            @QueryParam("past") @DefaultValue("false") boolean past,
            @QueryParam("ongoing") @DefaultValue("false") boolean ongoing,
            @QueryParam("myDestination") @DefaultValue("false") boolean myDestination,
            @QueryParam("search") String search,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("8") int size
    ) {
        final Long userId = accessHelper.getCurrentUserId(); // TODO: raro?
        final LocalDate startDate = DateUtils.parseDate(startDateStr);
        final LocalDate endDate = DateUtils.parseDate(endDateStr);
        final SortFieldJourney sortField = SortFieldJourney.from(sort);
        final SortDirection sortDirection = SortDirection.from(direction);

        final List<Journey> journeys = journeyService.findJourneys(
                search,
                userId,
                sortField,
                sortDirection,
                destination,
                startDate,
                endDate,
                interest,
                past,
                upcoming,
                myDestination,
                ongoing,
                new PageParams(page + 1, size)
        ).getContent();

        final List<JourneyDto> journeyDtos = JourneyDto.fromJourneyCollection(uriInfo, journeys);
        return Response.ok(new GenericEntity<>(journeyDtos) {}).build();
    }

}

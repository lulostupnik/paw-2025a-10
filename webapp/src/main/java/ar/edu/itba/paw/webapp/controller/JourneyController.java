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
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import ar.edu.itba.paw.webapp.form.CreateJourneyResponseForm;
import ar.edu.itba.paw.webapp.form.CreateTipForm;
import ar.edu.itba.paw.webapp.form.DeleteMessageForm;
import ar.edu.itba.paw.webapp.form.UpdateJourneyForm;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
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
        final SortDirection sortDirection = SortDirection.from(direction); // TODO: revisar si funcionan bien los sorts.

        final List<Journey> journeys = journeyService.findJourneys(
                search,
                userId,
                sortField,        // TODO: revisar
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


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJourneyById(@PathParam("id") final long id) {
        final Optional<Journey> maybeJourney = journeyService.findJourneyById(id);
        if (maybeJourney.isPresent()) {
            return Response.ok(JourneyDto.fromJourney(uriInfo, maybeJourney.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createJourney(@Valid final CreateJourneyForm form) {
        final Long userId = accessHelper.getCurrentUserId();

        final Journey journey = journeyService.createJourney(
                userId,
                form.getDestinationUniversity(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription()
        );

        return Response.created(UriUtils.getJourneyUri(uriInfo, journey.getId()))
                .entity(JourneyDto.fromJourney(uriInfo, journey))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateJourney(@PathParam("id") final long id, @Valid final UpdateJourneyForm form) {
        final Journey journey = journeyService.updateJourney(
                id,
                form.getDestinationUniversity(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription()
        );

        return Response.ok(JourneyDto.fromJourney(uriInfo, journey)).build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteJourney(@PathParam("id") final long id, @Valid final DeleteMessageForm form) {
        final String message = form != null ? form.getMessage() : null;
        journeyService.deleteJourney(id, message);
        return Response.noContent().build();
    }


    // ==================== TIPS ====================

    @GET
    @Path("/{journeyId}/tips")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTips(
            @PathParam("journeyId") final long journeyId,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("6") int size
    ) {
        final List<Tip> tips = journeyService.findTipsByJourneyId(journeyId, new PageParams(page + 1, size)).getContent();
        final List<TipDto> tipDtos = TipDto.fromTipCollection(uriInfo, tips);
        return Response.ok(new GenericEntity<>(tipDtos) {}).build();
    }

    @GET
    @Path("/{journeyId}/tips/{tipId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipById(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId
    ) {
        // TODO: Verificar que el tip le pertenece al journey --> mandarle el journeyId el método de servicio
        //  ¿quizas hacer que en realidad la clave del tip sea una clave compuesta? No se, medio fiaca
        final Optional<Tip> maybeTip = journeyService.findTipById(tipId);
        if (maybeTip.isPresent()) {
            return Response.ok(TipDto.fromTip(uriInfo, maybeTip.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{journeyId}/tips")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTip(
            @PathParam("journeyId") final long journeyId,
            @Valid final CreateTipForm form
    ) {
        final Tip tip = journeyService.createTip(journeyId, form.getTitle(), form.getContent());
        return Response.created(UriUtils.getJourneyTipUri(uriInfo, journeyId, tip.getId()))
                .entity(TipDto.fromTip(uriInfo, tip))
                .build();
    }

    @PUT
    @Path("/{journeyId}/tips/{tipId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId,
            @Valid final CreateTipForm form
    ) {
        final Tip tip = journeyService.updateTip(tipId, form.getTitle(), form.getContent());
        return Response.ok(TipDto.fromTip(uriInfo, tip)).build();
    }

    @DELETE
    @Path("/{journeyId}/tips/{tipId}")
    public Response deleteTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId
    ) {
        journeyService.deleteTip(tipId);
        return Response.noContent().build();
    }

    // ==================== RESPONSES ====================

    @GET
    @Path("/{journeyId}/responses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listJourneyResponses(
            @PathParam("journeyId") final long journeyId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ) {
        final List<JourneyResponse> responses = journeyService.findJourneyResponses(journeyId, new PageParams(page + 1, size)).getContent();
        final List<JourneyResponseDto> responseDtos = JourneyResponseDto.fromJourneyResponseCollection(uriInfo, responses);
        return Response.ok(new GenericEntity<>(responseDtos) {}).build();
    }

    @GET
    @Path("/{journeyId}/responses/{responseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJourneyResponseById(
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId
    ) {
        // TODO: Verificar que la respuesta pertenezca al journey --> ¿pasar journeyID al servicio?
        final Optional<JourneyResponse> maybeResponse = journeyService.findJourneyResponseById(responseId);
        if (maybeResponse.isPresent()) {
            return Response.ok(JourneyResponseDto.fromJourneyResponse(uriInfo, maybeResponse.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{journeyId}/responses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @Valid final CreateJourneyResponseForm form
    ) {
        final Long userId = accessHelper.getCurrentUserId();
        final JourneyResponse response = journeyService.createJourneyResponse(userId, journeyId, form.getMessage());
        return Response.created(UriUtils.getJourneyResponseUri(uriInfo, journeyId, response.getId()))
                .entity(JourneyResponseDto.fromJourneyResponse(uriInfo, response))
                .build();
    }

    @DELETE
    @Path("/{journeyId}/responses/{responseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId,
            @Valid final DeleteMessageForm form
    ) {
        final String message = form != null ? form.getMessage() : null;
        journeyService.deleteJourneyResponse(responseId, message);
        return Response.noContent().build();
    }

}

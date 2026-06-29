package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.models.exceptions.JourneyResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.TipNotFoundException;
import ar.edu.itba.paw.webapp.auth.AuthUtils;
import ar.edu.itba.paw.webapp.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.JourneyDto;
import ar.edu.itba.paw.webapp.dto.JourneyResponseDto;
import ar.edu.itba.paw.webapp.dto.TipDto;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import ar.edu.itba.paw.webapp.form.CreateJourneyResponseForm;
import ar.edu.itba.paw.webapp.form.CreateTipForm;
import ar.edu.itba.paw.webapp.form.DeleteMessageForm;
import ar.edu.itba.paw.webapp.form.PatchTipForm;
import ar.edu.itba.paw.webapp.form.UpdateJourneyForm;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.time.LocalDate;
import java.util.List;

@Path("journeys")
@Component
public class JourneyController {

    @Autowired
    private JourneyService journeyService;

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces(CustomMediaType.APPLICATION_JOURNEY_LIST)
    public Response listJourneys(
            @QueryParam("city") String city,
            @QueryParam("university") String university,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("interest") String interest,
            @QueryParam("upcoming") @DefaultValue("false") boolean upcoming,
            @QueryParam("past") @DefaultValue("false") boolean past,
            @QueryParam("ongoing") @DefaultValue("false") boolean ongoing,
            @QueryParam("destinationCity") Long destinationCity,
            @QueryParam("excludeUser") Long excludeUser,
            @QueryParam("search") String search,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("8") int size
    ) {
        final LocalDate startDate = DateUtils.parseDate(startDateStr);
        final LocalDate endDate = DateUtils.parseDate(endDateStr);
        final SortFieldJourney sortField = SortFieldJourney.from(sort);
        final SortDirection sortDirection = SortDirection.from(direction);

        final Page<Journey> journeys = journeyService.findJourneys(
                search,
                excludeUser,
                destinationCity,
                sortField,
                sortDirection,
                city,
                university,
                startDate,
                endDate,
                interest,
                past,
                upcoming,
                ongoing,
                new PageParams(page, size)
        );

        final List<JourneyDto> journeyDtos = JourneyDto.fromJourneyCollection(uriInfo, journeys.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(journeyDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, journeys).build();
    }


    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_JOURNEY)
    public Response getJourneyById(@Context Request req, @PathParam("id") final long id) {
        final Journey journey = journeyService.findJourneyById(id).orElseThrow(() -> new JourneyNotFoundException(id));
        return CacheUtils.withEtag(req, journey, () -> JourneyDto.fromJourney(uriInfo, journey));
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_JOURNEY)
    @Produces(CustomMediaType.APPLICATION_JOURNEY)
    public Response createJourney(@Valid final CreateJourneyForm form) {
        final Long userId = AuthUtils.getCurrentUserId();

        final Journey journey = journeyService.createJourney(
                userId,
                form.getDestinationUniversityId(),
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
    @Consumes(CustomMediaType.APPLICATION_JOURNEY)
    @Produces(CustomMediaType.APPLICATION_JOURNEY)
    public Response updateJourney(@PathParam("id") final long id, @Valid final UpdateJourneyForm form) {
        final Journey journey = journeyService.updateJourney(
                id,
                form.getDestinationUniversityId(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription()
        );

        return Response.ok(JourneyDto.fromJourney(uriInfo, journey)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_JOURNEY)
    @PreAuthorize("@accessHelper.canPatchJourney(#id, #form)")
    public Response deleteJourney(@PathParam("id") final long id, @Valid final DeleteMessageForm form) {
        journeyService.deleteJourney(id, form != null ? form.getMessage() : null);
        return Response.noContent().build();
    }


    // ==================== TIPS ====================

    @GET
    @Path("/{journeyId}/tips")
    @Produces(CustomMediaType.APPLICATION_TIP_LIST)
    public Response listTips(
            @PathParam("journeyId") final long journeyId,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("6") int size
    ) {
        final Page<Tip> tips = journeyService.findTipsByJourneyId(journeyId, new PageParams(page, size));
        final List<TipDto> tipDtos = TipDto.fromTipCollection(uriInfo, tips.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(tipDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, tips).build();
    }

    @GET
    @Path("/{journeyId}/tips/{tipId}")
    @Produces(CustomMediaType.APPLICATION_TIP)
    public Response getTipById(
            @Context Request req,
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId
    ) {
        final Tip tip = journeyService.findTipById(journeyId, tipId).orElseThrow(() -> new TipNotFoundException(journeyId, tipId));
        return CacheUtils.withEtag(req, tip, () -> TipDto.fromTip(uriInfo, tip));
    }

    @POST
    @Path("/{journeyId}/tips")
    @Consumes(CustomMediaType.APPLICATION_TIP)
    @Produces(CustomMediaType.APPLICATION_TIP)
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
    @Consumes(CustomMediaType.APPLICATION_TIP)
    @Produces(CustomMediaType.APPLICATION_TIP)
    public Response updateTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId,
            @Valid final CreateTipForm form
    ) {
        final Tip tip = journeyService.updateTip(journeyId, tipId, form.getTitle(), form.getContent());
        return Response.ok(TipDto.fromTip(uriInfo, tip)).build();
    }

    @PATCH
    @Path("/{journeyId}/tips/{tipId}")
    @Consumes(CustomMediaType.APPLICATION_TIP)
    public Response patchTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId,
            @Valid final PatchTipForm form
    ) {
        final Tip tip = journeyService.patchTip(
                journeyId,
                tipId,
                form.getTitle(),
                form.getContent()
        );
        return Response.ok(TipDto.fromTip(uriInfo, tip)).build();
    }

    @DELETE
    @Path("/{journeyId}/tips/{tipId}")
    public Response deleteTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId
    ) {
        journeyService.deleteTip(journeyId, tipId);
        return Response.noContent().build();
    }

    // ==================== RESPONSES ====================

    @GET
    @Path("/{journeyId}/responses")
    @Produces(CustomMediaType.APPLICATION_JOURNEY_RESPONSE_LIST)
    public Response listJourneyResponses(
            @PathParam("journeyId") final long journeyId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ) {
        final Page<JourneyResponse> responses = journeyService.findJourneyResponses(journeyId, new PageParams(page, size));
        final List<JourneyResponseDto> responseDtos = JourneyResponseDto.fromJourneyResponseCollection(uriInfo, responses.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(responseDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, responses).build();
    }

    @GET
    @Path("/{journeyId}/responses/{responseId}")
    @Produces(CustomMediaType.APPLICATION_JOURNEY_RESPONSE)
    public Response getJourneyResponseById(
            @Context Request req,
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId
    ) {
        final JourneyResponse response = journeyService.findJourneyResponseById(journeyId, responseId).orElseThrow(() -> new JourneyResponseNotFoundException(journeyId, responseId));
        return CacheUtils.withEtag(req, response, () -> JourneyResponseDto.fromJourneyResponse(uriInfo, response));
    }

    @POST
    @Path("/{journeyId}/responses")
    @Consumes(CustomMediaType.APPLICATION_JOURNEY_RESPONSE)
    @Produces(CustomMediaType.APPLICATION_JOURNEY_RESPONSE)
    public Response createJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @Valid final CreateJourneyResponseForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final JourneyResponse response = journeyService.createJourneyResponse(userId, journeyId, form.getMessage());
        return Response.created(UriUtils.getJourneyResponseUri(uriInfo, journeyId, response.getId()))
                .entity(JourneyResponseDto.fromJourneyResponse(uriInfo, response))
                .build();
    }

    @DELETE
    @Path("/{journeyId}/responses/{responseId}")
    public Response deleteJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId,
            @Valid final DeleteMessageForm form
    ) {
        final String message = form != null ? form.getMessage() : null;
        journeyService.deleteJourneyResponse(journeyId, responseId, message);
        return Response.noContent().build();
    }

}

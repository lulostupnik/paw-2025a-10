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
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.JourneyDto;
import ar.edu.itba.paw.webapp.dto.JourneyResponseDto;
import ar.edu.itba.paw.webapp.dto.TipDto;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import ar.edu.itba.paw.webapp.form.CreateJourneyResponseForm;
import ar.edu.itba.paw.webapp.form.CreateTipForm;
import ar.edu.itba.paw.webapp.form.PatchDeletionForm;
import ar.edu.itba.paw.webapp.form.PatchJourneyForm;
import ar.edu.itba.paw.webapp.form.PatchTipForm;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY_LIST)
    @PreAuthorize("@accessHelper.canListRecommendedFor(#recommendedForUser)")
    public Response listJourneys(
            @QueryParam("recommendedForUser") Long recommendedForUser,
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
        final SortFieldJourney sortField = sort == null || sort.isBlank() ? null : SortFieldJourney.from(sort);
        final SortDirection sortDirection = direction == null || direction.isBlank() ? null : SortDirection.from(direction);

        final Page<Journey> journeys = journeyService.findJourneys(
                search,
                recommendedForUser,
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
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY)
    public Response getJourneyById(@Context Request req, @PathParam("id") final long id) {
        final Journey journey = journeyService.findJourneyById(id).orElseThrow(() -> new JourneyNotFoundException());
        return CacheUtils.withEtag(req, journey, () -> JourneyDto.fromJourney(uriInfo, journey));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_JOURNEY)
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY)
    public Response createJourney(@Valid @NotNull final CreateJourneyForm form) {
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

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_JOURNEY)
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY)
    @PreAuthorize("@accessHelper.canPatchJourney(#id, #form)")
    public Response patchJourney(@PathParam("id") final long id, @Valid @NotNull final PatchJourneyForm form) {
        final Journey journey = journeyService.patchJourney(
                id,
                form.getDestinationUniversityId(),
                form.getStartDate(),
                form.getEndDate(),
                form.getDescription(),
                form.getDeleted(),
                form.getDeletionMessage()
        );

        return Response.ok(JourneyDto.fromJourney(uriInfo, journey)).build();
    }


    @GET
    @Path("/{journeyId}/tips")
    @Produces(GoTogetherMediaType.APPLICATION_TIP_LIST)
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
    @Produces(GoTogetherMediaType.APPLICATION_TIP)
    public Response getTipById(
            @Context Request req,
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId
    ) {
        final Tip tip = journeyService.findTipById(journeyId, tipId).orElseThrow(() -> new TipNotFoundException());
        return CacheUtils.withEtag(req, tip, () -> TipDto.fromTip(uriInfo, tip));
    }

    @POST
    @Path("/{journeyId}/tips")
    @Consumes(GoTogetherMediaType.APPLICATION_TIP)
    @Produces(GoTogetherMediaType.APPLICATION_TIP)
    public Response createTip(
            @PathParam("journeyId") final long journeyId,
            @Valid @NotNull final CreateTipForm form
    ) {
        final Tip tip = journeyService.createTip(journeyId, form.getTitle(), form.getContent());
        return Response.created(UriUtils.getJourneyTipUri(uriInfo, journeyId, tip.getId()))
                .entity(TipDto.fromTip(uriInfo, tip))
                .build();
    }

    @PATCH
    @Path("/{journeyId}/tips/{tipId}")
    @Consumes(GoTogetherMediaType.APPLICATION_TIP)
    public Response patchTip(
            @PathParam("journeyId") final long journeyId,
            @PathParam("tipId") final long tipId,
            @Valid @NotNull final PatchTipForm form
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

    @GET
    @Path("/{journeyId}/responses")
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY_RESPONSE_LIST)
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
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY_RESPONSE)
    public Response getJourneyResponseById(
            @Context Request req,
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId
    ) {
        final JourneyResponse response = journeyService.findJourneyResponseById(journeyId, responseId).orElseThrow(() -> new JourneyResponseNotFoundException());
        return CacheUtils.withEtag(req, response, () -> JourneyResponseDto.fromJourneyResponse(uriInfo, response));
    }

    @POST
    @Path("/{journeyId}/responses")
    @Consumes(GoTogetherMediaType.APPLICATION_JOURNEY_RESPONSE)
    @Produces(GoTogetherMediaType.APPLICATION_JOURNEY_RESPONSE)
    public Response createJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @Valid @NotNull final CreateJourneyResponseForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final JourneyResponse response = journeyService.createJourneyResponse(userId, journeyId, form.getMessage());
        return Response.created(UriUtils.getJourneyResponseUri(uriInfo, journeyId, response.getId()))
                .entity(JourneyResponseDto.fromJourneyResponse(uriInfo, response))
                .build();
    }

    @PATCH
    @Path("/{journeyId}/responses/{responseId}")
    @Consumes(GoTogetherMediaType.APPLICATION_JOURNEY_RESPONSE)
    @PreAuthorize("@accessHelper.canPatchJourneyResponse(#journeyId, #responseId, #form)")
    public Response patchJourneyResponse(
            @PathParam("journeyId") final long journeyId,
            @PathParam("responseId") final long responseId,
            @Valid @NotNull final PatchDeletionForm form
    ) {
        journeyService.patchJourneyResponse(journeyId, responseId, form.getDeleted(), form.getDeletionMessage());
        return Response.noContent().build();
    }

}

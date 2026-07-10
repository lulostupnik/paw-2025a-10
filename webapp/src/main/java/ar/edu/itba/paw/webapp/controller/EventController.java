package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.EventAttendanceNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.webapp.auth.AuthUtils;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.EventAttendanceDto;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.dto.EventResponseDto;
import ar.edu.itba.paw.webapp.dto.EventStatisticsDto;
import ar.edu.itba.paw.webapp.dto.RatingDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.ImageUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Path("events")
@Component
public class EventController {

    @Autowired
    private EventService eventService;

    @Context
    private UriInfo uriInfo;

    // ==================== EVENTS ====================

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_LIST)
    @PreAuthorize("#recommendedForUser == null or @accessHelper.isCurrentUser(#recommendedForUser)")
    public Response listEvents(
            @QueryParam("recommendedForUser") @P("recommendedForUser") Long recommendedForUser,
            @QueryParam("destination") String destination,
            @QueryParam("interest") String interest,
            @QueryParam("afterDate") String afterDateStr,
            @QueryParam("beforeDate") String beforeDateStr,
            // @QueryParam("attending") @DefaultValue("false") boolean attending, --> usar attendedBy
            @QueryParam("search") String search,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("8") int size,
            @QueryParam("attendedBy") Long attendedByUserId,
            @QueryParam("university") String university,
            @QueryParam("minRating") Integer minRating, //TODO: check, anda raro
            @QueryParam("hasCapacity") Boolean hasCapacity,
            @QueryParam("top") Boolean top,
            @QueryParam("creatorId") Long creatorId
    ) {
        final LocalDate startDate = DateUtils.parseDate(afterDateStr);
        final LocalDate endDate = DateUtils.parseDate(beforeDateStr);
        final SortFieldEvent sortField = sort == null || sort.isBlank() ? null : SortFieldEvent.from(sort);
        final SortDirection sortDirection = direction == null || direction.isBlank() ? null : SortDirection.from(direction);


        final Page<Event> eventsPage = eventService.searchEventsWithFilters(
                search,
                recommendedForUser,
                creatorId,
                sortField,
                sortDirection,
                destination,
                startDate,
                endDate,
                interest,
                attendedByUserId,
                university,
                minRating,
                hasCapacity,
                top,
                new PageParams(page, size)
        );

        final List<EventDto> eventDtos = EventDto.fromEventCollection(uriInfo, eventsPage.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(eventDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, eventsPage).build();
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT)
    public Response getEventById(@Context Request req, @PathParam("id") final long id) {
        final Event event = eventService.findEventById(id).orElseThrow(() -> new EventNotFoundException(id));
        return CacheUtils.withEtag(req, event, () -> EventDto.fromEvent(uriInfo, event));
    }


    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT)
    @Produces(GoTogetherMediaType.APPLICATION_EVENT)
    public Response createEvent(@Valid @NotNull final CreateEventForm form) {
        final Long userId = AuthUtils.getCurrentUserId();

        final Event event = eventService.createEvent(
                userId,
                form.getCityId(),
                form.getDate(),
                form.getDescription(),
                form.getTitle(),
                form.getTime(),
                form.getAddress(),
                form.getAttendeesLimit()
        );

        return Response.created(UriUtils.getEventUri(uriInfo, event.getId()))
                .entity(EventDto.fromEvent(uriInfo, event))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT)
    @Produces(GoTogetherMediaType.APPLICATION_EVENT)
    public Response updateEvent(@PathParam("id") final long id, @Valid @NotNull final EditEventForm form) {
        final Event event = eventService.updateEvent(
                id,
                form.getCityId(),
                form.getDate(),
                form.getDescription(),
                form.getTitle(),
                form.getTime(),
                form.getAddress(),
                form.getAttendeesLimit()
        );

        return Response.ok(EventDto.fromEvent(uriInfo, event)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT)
    @PreAuthorize("@accessHelper.canPatchEvent(#id, #form)")
    public Response patchEvent(@PathParam("id") final long id, @Valid @NotNull final PatchDeletionForm form) {
        eventService.patchEvent(id, form.getDeleted(), form.getDeletionMessage());
        return Response.noContent().build();
    }


    @GET
    @Path("/{eventId}/statistics")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_STATISTICS)
    public Response getEventStatistics(@Context Request req, @PathParam("eventId") final long eventId) {
        final EventWithStatistics statistics = eventService.findEventWithStatistics(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return CacheUtils.withEtag(req, statistics, () -> EventStatisticsDto.fromEventWithStatistics(uriInfo, statistics));
    }


    // ==================== EVENT FLYER ====================z

    @GET
    @Path("/{id}/flyer")
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response getEventFlyer(@Context Request req, @PathParam("id") final long id) {
        final Image image = eventService.getEventFlyer(id).orElseThrow(() -> new ImageNotFoundException("Event flyer not found"));
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getData())
                .header(HttpHeaders.CONTENT_TYPE, ImageUtils.detectContentType(image.getData()))
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"event_%d_flyer\"", id));
        return CacheUtils.withEtag(req, image, responseBuilder);
    }

    @PUT
    @Path("/{id}/flyer")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response updateEventFlyer(
            @PathParam("id") final long id,
            @FormDataParam("flyer") final InputStream flyerStream
    ) {
        final byte[] bytes = ImageUtils.readImage(flyerStream);
        final Image image = eventService.updateEventFlyer(id, bytes);
        return Response.ok(image.getData())
                .contentLocation(UriUtils.getEventFlyerUri(uriInfo, id))
                .header(HttpHeaders.CONTENT_TYPE, ImageUtils.detectContentType(image.getData()))
                .build();
    }

    // ==================== EVENT RESPONSES ====================

    @GET
    @Path("/{eventId}/responses")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RESPONSE_LIST)
    public Response listEventResponses(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ) {
        final Page<EventResponse> responses = eventService.findEventResponses(eventId, new PageParams(page, size));
        final List<EventResponseDto> responseDtos = EventResponseDto.fromEventResponseCollection(uriInfo, responses.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(responseDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, responses).build();
    }

    @GET
    @Path("/{eventId}/responses/{responseId}")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RESPONSE)
    public Response getEventResponseById(
            @Context Request req,
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId
    ) {
        final EventResponse response = eventService.findEventResponseById(eventId, responseId).orElseThrow(() -> new EventResponseNotFoundException(eventId, responseId));
        return CacheUtils.withEtag(req, response, () -> EventResponseDto.fromEventResponse(uriInfo, response));
    }

    @POST
    @Path("/{eventId}/responses")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT_RESPONSE)
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RESPONSE)
    public Response createEventResponse(
            @PathParam("eventId") final long eventId,
            @Valid @NotNull final CreateEventResponseForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final EventResponse response = eventService.createEventResponse(userId, eventId, form.getMessage());
        return Response.created(UriUtils.getEventResponseUri(uriInfo, eventId, response.getId()))
                .entity(EventResponseDto.fromEventResponse(uriInfo, response))
                .build();
    }

    @PATCH
    @Path("/{eventId}/responses/{responseId}")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT_RESPONSE)
    @PreAuthorize("@accessHelper.canPatchEventResponse(#eventId, #responseId, #form)")
    public Response patchEventResponse(
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId,
            @Valid @NotNull final PatchDeletionForm form
    ) {
        eventService.patchEventResponse(eventId, responseId, form.getDeleted(), form.getDeletionMessage());
        return Response.noContent().build();
    }

    // ==================== ATTENDANCES ====================

    @GET
    @Path("/{eventId}/attendances")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_ATTENDANCE_LIST)
    public Response listEventAttendances(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final Page<EventAttendance> attendances = eventService.findEventAttendances(eventId, new PageParams(page, size));
        final List<EventAttendanceDto> attendanceDtos = EventAttendanceDto.fromEventAttendanceCollection(uriInfo, attendances.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(attendanceDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, attendances).build();
    }

    @POST
    @Path("/{eventId}/attendances")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_ATTENDANCE)
    public Response attendEvent(@PathParam("eventId") final long eventId) {
        final Long userId = AuthUtils.getCurrentUserId();
        final EventAttendance attendance = eventService.createEventAttendance(userId, eventId);
        return Response.created(UriUtils.getEventAttendanceUri(uriInfo, eventId, userId))
                .entity(EventAttendanceDto.fromEventAttendance(uriInfo, attendance))
                .build();
    }

    @GET
    @Path("/{eventId}/attendances/{userId}")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_ATTENDANCE)
    public Response getEventAttendance(
            @Context Request req,
            @PathParam("eventId") final long eventId,
            @PathParam("userId") final long userId
    ) {
        final EventAttendance attendance = eventService.findEventAttendance(userId, eventId)
                .orElseThrow(() -> new EventAttendanceNotFoundException(userId, eventId));
        return CacheUtils.withEtag(req, attendance, () -> EventAttendanceDto.fromEventAttendance(uriInfo, attendance));
    }


    @DELETE
    @Path("/{eventId}/attendances/{userId}")
    public Response unattendEventByUser(
            @PathParam("eventId") final long eventId,
            @PathParam("userId") final long userId
    ) {
        eventService.deleteEventAttendance(userId, eventId);
        return Response.noContent().build();
    }

    // ==================== RATINGS ====================

    @GET
    @Path("/{eventId}/ratings")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RATING_LIST)
    public Response listEventRatings(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final Page<Rating> ratings = eventService.findRatingsByEventId(eventId, new PageParams(page, size));
        final List<RatingDto> ratingDtos = RatingDto.fromRatingCollection(uriInfo, ratings.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(ratingDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, ratings).build();
    }

    @GET
    @Path("/{eventId}/ratings/{ratingId}")
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RATING)
    public Response getEventRatingById(
            @Context Request req,
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId
    ) {
        final Rating rating = eventService.findRatingById(eventId, ratingId).orElseThrow(() -> new RatingNotFoundException(eventId, ratingId, true));
        return CacheUtils.withEtag(req, rating, () -> RatingDto.fromRating(uriInfo, rating));
    }

    @POST
    @Path("/{eventId}/ratings")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT_RATING)
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RATING)
    public Response createEventRating(
            @PathParam("eventId") final long eventId,
            @Valid @NotNull final CreateRatingForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final Rating rating = eventService.rateEvent(userId, eventId, form.getRating());
        return Response.created(UriUtils.getEventRatingUri(uriInfo, eventId, rating.getId()))
                .entity(RatingDto.fromRating(uriInfo, rating))
                .build();
    }

    @PUT
    @Path("/{eventId}/ratings/{ratingId}")
    @Consumes(GoTogetherMediaType.APPLICATION_EVENT_RATING)
    @Produces(GoTogetherMediaType.APPLICATION_EVENT_RATING)
    public Response updateEventRating(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId,
            @Valid @NotNull final CreateRatingForm form
    ) {
        final Rating rating = eventService.updateEventRating(eventId, ratingId, form.getRating());
        return Response.ok(RatingDto.fromRating(uriInfo, rating)).build();
    }

    @DELETE
    @Path("/{eventId}/ratings/{ratingId}")
    public Response deleteEventRating(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId
    ) {
        eventService.deleteRating(eventId, ratingId);
        return Response.noContent().build();
    }
}

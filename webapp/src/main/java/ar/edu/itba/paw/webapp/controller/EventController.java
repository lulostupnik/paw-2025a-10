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
import ar.edu.itba.paw.webapp.CustomMediaType;
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
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.io.IOException;
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
    @Produces(CustomMediaType.APPLICATION_EVENT_LIST)
    public Response listEvents(
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
            @QueryParam("creatorId") Long creatorId
    ) {
        final LocalDate startDate = DateUtils.parseDate(afterDateStr);
        final LocalDate endDate = DateUtils.parseDate(beforeDateStr);
        final SortFieldEvent sortField = SortFieldEvent.from(sort);
        final SortDirection sortDirection = SortDirection.from(direction);


        final Page<Event> eventsPage = eventService.searchEventsWithFilters(
                search,
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
                new PageParams(page, size)
        );

        final List<EventDto> eventDtos = EventDto.fromEventCollection(uriInfo, eventsPage.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(eventDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, eventsPage).build();
    }

    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_EVENT)
    public Response getEventById(@Context Request req, @PathParam("id") final long id) {
        final Event event = eventService.findEventById(id).orElseThrow(() -> new EventNotFoundException(id));
        return CacheUtils.withEtag(req, event, () -> EventDto.fromEvent(uriInfo, event));
    }


    @POST
    @Consumes(CustomMediaType.APPLICATION_EVENT)
    public Response createEvent(@Valid final CreateEventForm form) {
        final Long userId = AuthUtils.getCurrentUserId();

        final Event event = eventService.createEvent(
                userId,
                form.getCity(),
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
    @Consumes(CustomMediaType.APPLICATION_EVENT)
    public Response updateEvent(@PathParam("id") final long id, @Valid final EditEventForm form) {
        final Event event = eventService.updateEvent(
                id,
                form.getCity(),
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
    @Consumes(CustomMediaType.APPLICATION_EVENT)
    public Response patchEvent(@PathParam("id") final long id, @Valid final PatchEventForm form) {
        final Event event = eventService.patchEvent(
                id,
                form.getCity(),
                form.getDate(),
                form.getDescription(),
                form.getTitle(),
                form.getTime(),
                form.getAddress(),
                form.getAttendeesLimit()
        );

        return Response.ok(EventDto.fromEvent(uriInfo, event)).build();
    }


    @GET
    @Path("/{eventId}/statistics")
    @Produces(CustomMediaType.APPLICATION_EVENT_STATISTICS)
    public Response getEventStatistics(@PathParam("eventId") final long eventId) {
        final EventWithStatistics statistics = eventService.findEventWithStatistics(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return Response.ok(EventStatisticsDto.fromEventWithStatistics(uriInfo, statistics)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") final long id, @Valid final DeleteMessageForm form) {
        final String message = form != null ? form.getMessage() : null;
        eventService.deleteEvent(id, message);
        return Response.noContent().build();
    }

    // ==================== EVENT FLYER ====================z

    @GET
    @Path("/{id}/flyer")
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response getEventFlyer(@PathParam("id") final long id) {
        final Image image = eventService.getEventFlyer(id).orElseThrow(() -> new ImageNotFoundException("Event flyer not found"));
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getData())
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"event_%d_flyer.jpeg\"", id));
        return CacheUtils.withMaxAge(responseBuilder, CacheUtils.ONE_MONTH).build();
    }

    // TODO: ¿Esto tiene lógica de negocios? Pareciera que si, moverlo a service-layer
    // TODO tiene sentido que este separado en otro endpoint? No me pueden quedan eventos inconsistentes sin flyers?
    // TODO: concluision: dejarlo asi pero habilitar para que hayan eventos sin flyers en el frontend
    @PUT
    @Path("/{id}/flyer")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"image/jpeg", "image/png", "image/webp"}) // TODO: ¿esto esta bien?
    public Response updateEventFlyer(
            @PathParam("id") final long id,
            @FormDataParam("flyer") final InputStream flyerStream
    ) {
        if (flyerStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Flyer is required").build();
        }
        try {
            final byte[] bytes = flyerStream.readAllBytes();
            eventService.updateEventFlyer(id, bytes);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to read flyer").build();
        }
        final Image image = eventService.getEventFlyer(id).orElseThrow(() -> new ImageNotFoundException("Event flyer not found"));
        return Response.ok(image.getData())
                .contentLocation(UriUtils.getEventFlyerUri(uriInfo, id))
                .header("Content-Type", "image/jpeg")
                .build();
    }

    // ==================== EVENT RESPONSES ====================

    @GET
    @Path("/{eventId}/responses")
    @Produces(CustomMediaType.APPLICATION_EVENT_RESPONSE_LIST)
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
    @Produces(CustomMediaType.APPLICATION_EVENT_RESPONSE)
    public Response getEventResponseById(
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId
    ) {
        final EventResponse response = eventService.findEventResponseById(eventId, responseId).orElseThrow(() -> new EventResponseNotFoundException(eventId, responseId));
        return Response.ok(EventResponseDto.fromEventResponse(uriInfo, response)).build();
    }

    @POST
    @Path("/{eventId}/responses")
    @Consumes(CustomMediaType.APPLICATION_EVENT_RESPONSE)
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

    @DELETE
    @Path("/{eventId}/responses/{responseId}")
    public Response deleteEventResponse(
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId,
            @Valid final DeleteMessageForm form
    ) {
        final String message = form != null ? form.getMessage() : null;
        eventService.deleteEventResponse(eventId, responseId, message);
        return Response.noContent().build();
    }

    // ==================== ATTENDANCES ====================

    @GET
    @Path("/{eventId}/attendances")
    @Produces(CustomMediaType.APPLICATION_EVENT_ATTENDANCE_LIST)
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
    public Response attendEvent(@PathParam("eventId") final long eventId) {
        final Long userId = AuthUtils.getCurrentUserId();
        final EventAttendance attendance = eventService.createEventAttendance(userId, eventId);
        return Response.created(UriUtils.getEventAttendanceUri(uriInfo, eventId, userId))
                .entity(EventAttendanceDto.fromEventAttendance(uriInfo, attendance))
                .build();
    }

    @GET
    @Path("/{eventId}/attendance")
    public Response getCurrentUserAttendance(@PathParam("eventId") final long eventId) {
        final Long userId = AuthUtils.getCurrentUserId();
        eventService.findEventAttendance(userId, eventId).orElseThrow(EventAttendanceNotFoundException::new);
        return Response.status(Response.Status.FOUND)
                .location(UriUtils.getEventAttendanceUri(uriInfo, eventId, userId))
                .build();
    }

    @GET
    @Path("/{eventId}/attendances/{userId}")
    @Produces(CustomMediaType.APPLICATION_EVENT_ATTENDANCE)
    public Response getEventAttendance(
            @PathParam("eventId") final long eventId,
            @PathParam("userId") final long userId
    ) {
        final EventAttendance attendance = eventService.findEventAttendance(userId, eventId)
                .orElseThrow(() -> new EventAttendanceNotFoundException(userId, eventId));
        return Response.ok(EventAttendanceDto.fromEventAttendance(uriInfo, attendance)).build();
    }


    @DELETE
    @Path("/{eventId}/attendance")
    public Response unattendEvent(@PathParam("eventId") final long eventId) {
        final Long userId = AuthUtils.getCurrentUserId();
        return Response.temporaryRedirect(UriUtils.getEventAttendanceUri(uriInfo, eventId, userId)).build(); // TODO: ¿tiene que retornar 404 si no existe o simplemente dejamos que el otro endpoint le tire el 404?
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
    @Produces(CustomMediaType.APPLICATION_EVENT_RATING_LIST)
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
    @Produces(CustomMediaType.APPLICATION_EVENT_RATING)
    public Response getEventRatingById(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId
    ) {
        final Rating rating = eventService.findRatingById(eventId, ratingId).orElseThrow(() -> new RatingNotFoundException(eventId, ratingId, true));
        return Response.ok(RatingDto.fromRating(uriInfo, rating)).build();
    }

    @POST
    @Path("/{eventId}/ratings")
    @Consumes(CustomMediaType.APPLICATION_EVENT_RATING)
    public Response createEventRating(
            @PathParam("eventId") final long eventId,
            @Valid final CreateRatingForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final Rating rating = eventService.rateEvent(userId, eventId, form.getRating());
        return Response.created(UriUtils.getEventRatingUri(uriInfo, eventId, rating.getId()))
                .entity(RatingDto.fromRating(uriInfo, rating))
                .build();
    }

    @PUT
    @Path("/{eventId}/ratings/{ratingId}")
    @Consumes(CustomMediaType.APPLICATION_EVENT_RATING)
    public Response updateEventRating(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId,
            @Valid final CreateRatingForm form
    ) {
        final Long userId = AuthUtils.getCurrentUserId();
        final Rating rating = eventService.updateEventRating(userId, eventId, form.getRating());
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

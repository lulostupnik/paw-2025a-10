package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.dto.EventResponseDto;
import ar.edu.itba.paw.webapp.dto.RatingDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Path("events")
@Component
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AccessHelper accessHelper;

    @Context
    private UriInfo uriInfo;

    // ==================== EVENTS ====================

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEvents(
            @QueryParam("destination") String destination,
            @QueryParam("interest") String interest,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("upcoming") @DefaultValue("false") boolean upcoming,
            @QueryParam("past") @DefaultValue("false") boolean past,
            @QueryParam("attending") @DefaultValue("false") boolean attending,
            @QueryParam("search") String search,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("8") int size,
            @QueryParam("attendedBy") Long attendedByUserId,
            @QueryParam("university") String university,
            @QueryParam("minRating") Integer minRating, //TODO: check, anda raro
            @QueryParam("hasCapacity") Boolean hasCapacity
    ) {
        final LocalDate startDate = DateUtils.parseDate(startDateStr);
        final LocalDate endDate = DateUtils.parseDate(endDateStr);
        final SortFieldEvent sortField = SortFieldEvent.from(sort);
        final SortDirection sortDirection = SortDirection.from(direction);

        // Use current user's ID if attending filter is enabled
        final Long userId = attending ? accessHelper.getCurrentUserId() : null;

        final Page<Event> eventsPage = eventService.searchEventsWithFilters(
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
                attending,
                attendedByUserId,
                university,
                minRating,
                hasCapacity,
                new PageParams(page + 1, size)
        );

        final List<EventDto> eventDtos = EventDto.fromEventCollection(uriInfo, eventsPage.getContent());
        return Response.ok(new GenericEntity<>(eventDtos) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("id") final long id) {
        final Event event = eventService.findEventById(id).orElseThrow(() -> new EventNotFoundException(id));
        return Response.ok(EventDto.fromEvent(uriInfo, event)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(@Valid final CreateEventForm form) {
        final Long userId = accessHelper.getCurrentUserId();
        final byte[] flyerBytes = Base64.getDecoder().decode(form.getFlyerBase64());

        final Event event = eventService.createEvent(
                userId,
                form.getCity(),
                form.getDate(),
                flyerBytes,
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@PathParam("id") final long id, @Valid final EditEventForm form) {
        byte[] flyerBytes = null;
        if (form.getFlyerBase64() != null && !form.getFlyerBase64().isEmpty()) {
            flyerBytes = Base64.getDecoder().decode(form.getFlyerBase64());
        }

        final Event event = eventService.updateEvent(
                id,
                form.getCity(),
                form.getDate(),
                flyerBytes,
                form.getDescription(),
                form.getTitle(),
                form.getTime(),
                form.getAddress(),
                form.getAttendeesLimit()
        );

        return Response.ok(EventDto.fromEvent(uriInfo, event)).build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@PathParam("id") final long id, @Valid final DeleteMessageForm form) {
        final String message = form != null ? form.getMessage() : null;
        eventService.deleteEvent(id, message);
        return Response.noContent().build();
    }

    // ==================== EVENT RESPONSES ====================

    @GET
    @Path("/{eventId}/responses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEventResponses(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ) {
        final List<EventResponse> responses = eventService.findEventResponses(eventId, new PageParams(page + 1, size)).getContent();
        final List<EventResponseDto> responseDtos = EventResponseDto.fromEventResponseCollection(uriInfo, responses);
        return Response.ok(new GenericEntity<>(responseDtos) {}).build();
    }

    @GET
    @Path("/{eventId}/responses/{responseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventResponseById(
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId
    ) {
        final EventResponse response = eventService.findEventResponseById(responseId).orElseThrow(() -> new EventResponseNotFoundException(responseId));
        return Response.ok(EventResponseDto.fromEventResponse(uriInfo, response)).build();
    }

    @POST
    @Path("/{eventId}/responses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEventResponse(
            @PathParam("eventId") final long eventId,
            @Valid @NotNull final CreateEventResponseForm form
    ) {
        final Long userId = accessHelper.getCurrentUserId();
        final EventResponse response = eventService.createEventResponse(userId, eventId, form.getMessage());
        return Response.created(UriUtils.getEventResponseUri(uriInfo, eventId, response.getId()))
                .entity(EventResponseDto.fromEventResponse(uriInfo, response))
                .build();
    }

    @DELETE
    @Path("/{eventId}/responses/{responseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEventResponse(
            @PathParam("eventId") final long eventId,
            @PathParam("responseId") final long responseId,
            @Valid final DeleteMessageForm form
    ) {
        // TODO: chequear en servicio que el responseId sea de una respuesta hecha al eventId? --> mandar eventId
        final String message = form != null ? form.getMessage() : null;
        eventService.deleteEventResponse(responseId, message);
        return Response.noContent().build();
    }

    // ==================== ATTENDANCES ====================

    @GET
    @Path("/{eventId}/attendances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEventAttendees(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final List<User> attendees = eventService.findEventAttendees(eventId, new PageParams(page + 1, size)).getContent();
        final List<UserDto> attendeeDtos = UserDto.fromUserCollection(uriInfo, attendees);
        return Response.ok(new GenericEntity<>(attendeeDtos) {}).build();
    }

    @POST
    @Path("/{eventId}/attendances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response attendEvent(@PathParam("eventId") final long eventId) {
        final Long userId = accessHelper.getCurrentUserId();
        final EventAttendance attendance = eventService.createEventAttendance(userId, eventId);
        return Response.created(UriUtils.getEventAttendancesUri(uriInfo, eventId)).build();
    }

    @DELETE
    @Path("/{eventId}/attendances")
    public Response unattendEvent(@PathParam("eventId") final long eventId) {
        final Long userId = accessHelper.getCurrentUserId();
        eventService.deleteEventAttendance(userId, eventId);
        return Response.noContent().build();
    }

    // ==================== RATINGS (Sub-resource of Events) ====================

    @GET
    @Path("/{eventId}/ratings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEventRatings(
            @PathParam("eventId") final long eventId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final List<Rating> ratings = eventService.findRatingsByEventId(eventId, new PageParams(page + 1, size)).getContent();
        final List<RatingDto> ratingDtos = RatingDto.fromRatingCollection(uriInfo, ratings);
        return Response.ok(new GenericEntity<>(ratingDtos) {}).build();
    }

    @GET
    @Path("/{eventId}/ratings/{ratingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventRatingById(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId
    ) {
        final Rating rating = eventService.findRatingById(ratingId).orElseThrow(() -> new RatingNotFoundException(ratingId));
        return Response.ok(RatingDto.fromRating(uriInfo, rating)).build();
    }

    @POST
    @Path("/{eventId}/ratings")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEventRating(
            @PathParam("eventId") final long eventId,
            @Valid final CreateRatingForm form
    ) {
        final Long userId = accessHelper.getCurrentUserId();
        final Rating rating = eventService.rateEvent(userId, eventId, form.getRating());
        return Response.created(UriUtils.getEventRatingUri(uriInfo, eventId, rating.getId()))
                .entity(RatingDto.fromRating(uriInfo, rating))
                .build();
    }

    @PUT
    @Path("/{eventId}/ratings/{ratingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEventRating(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId,
            @Valid final CreateRatingForm form
    ) {
        final Long userId = accessHelper.getCurrentUserId();
        final Rating rating = eventService.updateEventRating(userId, eventId, form.getRating());
        return Response.ok(RatingDto.fromRating(uriInfo, rating)).build();
    }

    @DELETE
    @Path("/{eventId}/ratings/{ratingId}")
    public Response deleteEventRating(
            @PathParam("eventId") final long eventId,
            @PathParam("ratingId") final long ratingId
    ) {
        eventService.deleteRating(ratingId);
        return Response.noContent().build();
    }
}

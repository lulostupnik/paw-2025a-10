package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserInterestNotFoundException;
import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.InterestDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserInterestDto;
import ar.edu.itba.paw.webapp.dto.UserRatingDto;
import ar.edu.itba.paw.webapp.form.AddUserInterestForm;
import ar.edu.itba.paw.webapp.form.BlockUserForm;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.EditUserForm;
import ar.edu.itba.paw.webapp.form.PasswordForm;
import ar.edu.itba.paw.webapp.form.PatchUserForm;
import ar.edu.itba.paw.webapp.form.ForgotPasswordForm;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ar.edu.itba.paw.webapp.utils.CacheUtils;

import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private InterestService interestService;

    @Autowired
    private AccessHelper accessHelper;

    @Context
    private UriInfo uriInfo;

    // TODO: Revisar: esto solo lo pueden pedir los admins, ¿no? -> ver que se este cumpliendo
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers(
            @QueryParam("attendingEvent") Long attendingEventId,
            @QueryParam("university") Long universityId,
            @QueryParam("career") Long careerId,
            @QueryParam("interest") Long interestId,
            @QueryParam("search") String search,
            @QueryParam("blocked") Boolean blocked,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final Page<User> allUsers = us.findUsers(search, new PageParams(page, size), attendingEventId, universityId, careerId, interestId, blocked);
        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, allUsers.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(userDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, allUsers).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        return Response.ok(UserDto.fromUser(uriInfo, user)).build(); // todo: este se cachea?
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid final CreateUserForm registerForm) {
        final User user = us.createUser(
                registerForm.getEmail(),
                registerForm.getUsername(),
                registerForm.getFirstName(),
                registerForm.getLastName(),
                registerForm.getOriginUniversity(),
                registerForm.getCareer(),
                registerForm.getInterests(),
                registerForm.getPassword(),
                LocaleContextHolder.getLocale()
        );
        return Response.created(UriUtils.getUserUri(uriInfo, user.getId()))
                .entity(UserDto.fromUser(uriInfo, user))
                .build();
    }

    @POST
    @Consumes(CustomMediaType.USER_PASSWORD)
    public Response requestPasswordReset(@Valid final ForgotPasswordForm form) {
        us.initiatePasswordReset(form.getEmail());
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") final long id, @Valid EditUserForm form) {
         final User user = us.updateUser(id, form.getUsername(), form.getFirstName(), form.getLastName(),
                 form.getOriginUniversity(), form.getCareer());
         return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchUser(@PathParam("id") final long id, @Valid PatchUserForm form) {
        final User user = us.patchUser(id, form.getUsername(), form.getFirstName(), form.getLastName(), form.getOriginUniversity(), form.getCareer());
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    // ==================== PASSWORD ====================

    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(
            @PathParam("id") final long id,
            @Valid final PasswordForm form
    ) {
        us.updatePassword(id, form.getPassword());
        return Response.noContent().build();
    }


    // ==================== BLOCKED STATUS ====================

    @PUT
    @Path("/{id}/blocked")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBlockedStatus(
            @PathParam("id") final long id,
            @Valid final BlockUserForm form
    ) {
        us.setBlockedStatus(id, form.getBlocked());
        return Response.noContent().build();
    }


    // ==================== USER INTERESTS ====================

    @GET
    @Path("/{userId}/interests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUserInterests(
            @PathParam("userId") final long userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final User user = us.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        final Page<UserInterest> userInterests = interestService.findInterestsByUser(user, new PageParams(page, size));
        final List<UserInterestDto> interestDtos = UserInterestDto.fromUserInterestCollection(uriInfo, userInterests.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(interestDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, userInterests).build();
    }

    @POST
    @Path("/{userId}/interests")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserInterest(
            @PathParam("userId") final long userId,
            @Valid final AddUserInterestForm form
    ) {
        final UserInterest userInterest = interestService.addUserInterest(userId, form.getInterestId());
        return Response.created(UriUtils.getUserInterestUri(uriInfo, userId, form.getInterestId()))
                .entity(UserInterestDto.fromUserInterest(uriInfo, userInterest))
                .build();
    }

    @GET
    @Path("/{userId}/interests/{interestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInterest(
            @PathParam("userId") final long userId,
            @PathParam("interestId") final long interestId
    ) {
        final UserInterest userInterest = interestService.findUserInterest(userId, interestId)
                .orElseThrow(() -> new UserInterestNotFoundException(userId, interestId));
        return Response.ok(UserInterestDto.fromUserInterest(uriInfo, userInterest)).build();
    }

    @DELETE
    @Path("/{userId}/interests/{interestId}")
    public Response removeUserInterest(
            @PathParam("userId") final long userId,
            @PathParam("interestId") final long interestId
    ) {
        interestService.removeUserInterest(userId, interestId);
        return Response.noContent().build();
    }

    // ==================== USER RATING (Computed value) ====================

    @GET
    @Path("/{userId}/rating")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRating(@PathParam("userId") final long userId) {
        us.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // This is a slow computed operation
        final Optional<Double> createdRating = us.findAverageRatingForCreatedEvents(userId);
        final Optional<Double> attendedRating = us.findAverageRatingForAttendedEvents(userId);
        // TODO: Get total ratings count (might need to add to UserService)

        return Response.ok(UserRatingDto.fromRatings(createdRating.orElse(null),attendedRating.orElse(null) )).build();
    }




    // ==================== USER PROFILE PICTURE ====================

    @GET
    @Path("/{userId}/profilePicture")
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response getUserProfilePicture(@PathParam("userId") final long userId) {
        final Image image = us.getProfilePicture(userId).orElseThrow(() -> new ImageNotFoundException("Profile picture not found"));
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getData())
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"profile_%d.jpg\"", userId));
        return CacheUtils.withMaxAge(responseBuilder, CacheUtils.ONE_MONTH).build();
    }

    // TODO: parece que hay business logic. Arreglar.
    @PUT
    @Path("/{userId}/profilePicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response updateUserProfilePicture(
            @PathParam("userId") final long userId,
            @FormDataParam("profilePicture") final InputStream profilePictureStream
    ) {
        if (profilePictureStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Profile picture is required").build();
        }
        try {
            final byte[] bytes = profilePictureStream.readAllBytes();
            us.updateProfilePicture(userId, bytes);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to read profile picture").build();
        }
        final Image image = us.getProfilePicture(userId).orElseThrow(() -> new ImageNotFoundException("Profile picture not found"));

        return Response.ok(image.getData())
                .contentLocation(UriUtils.getUserProfilePictureUri(uriInfo, userId))
                .header("Content-Type", "image/jpeg")
                .build();
    }
}
package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.dto.InterestDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserRatingDto;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.EditUserForm;
import ar.edu.itba.paw.webapp.form.ValidateUserForm;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        final List<User> allUsers = us.findUsers(search, new PageParams(page + 1, size), attendingEventId, universityId, careerId, interestId, blocked).getContent();
        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, allUsers);
        return Response.ok(new GenericEntity<>(userDtos) {}).build();
    }

    // TODO: ¿Esto quien lo puede acceder?
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
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



    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") final long id,  @Valid EditUserForm form ) {
        // TODO: Create form class for full user update
         final User user = us.updateUser(id, form.getUsername(), form.getFirstName(), form.getLastName(),
                 form.getOriginUniversity(), form.getCareer());
         return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }



    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchUser(
            @PathParam("id") final long id,
            @Valid final ValidateUserForm form
            /* TODO: Create a unified PatchUserForm that handles:
               - validationToken (email validation)
               - passwordResetToken + newPassword (password reset)
               - blocked (admin block/unblock)
               - password (password change - requires current password)
            */
    ) {
        // Currently only handles email validation
        if (form.getValidationToken() != null) {
            final User verifiedUser = us.verifyUser(form.getValidationToken());
            if (verifiedUser == null) {
                return Response.status(Response.Status.BAD_REQUEST).build(); // TODO: ¿es este el error correcto?
            }
            return Response.ok(UserDto.fromUser(uriInfo, verifiedUser)).build();
        }

        // TODO: Handle password reset with token
        // if (form.getPasswordResetToken() != null && form.getNewPassword() != null) {
        //     us.resetPassword(form.getPasswordResetToken(), form.getNewPassword());
        //     return Response.ok().build();
        // }

        // TODO: Handle block/unblock (admin only)
        // if (form.getBlocked() != null) {
        //     if (form.getBlocked()) {
        //         us.blockUser(id);
        //     } else {
        //         us.unblockUser(id);
        //     }
        //     return Response.ok(UserDto.fromUser(uriInfo, us.findUserById(id).get())).build();
        // }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") final long id) {
        // TODO: Implement user deletion (might need to add to UserService)
        // return Response.noContent().build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // ==================== USER INTERESTS (Sub-resource) ====================

    @GET
    @Path("/{userId}/interests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUserInterests(
            @PathParam("userId") final long userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final User user = us.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        final List<UserInterest> userInterests = interestService.findInterestsByUser(user, new PageParams(page + 1, size)).getContent();
        // TODO: Create UserInterestDto or reuse InterestDto
        final List<InterestDto> interestDtos = userInterests.stream()
                .map(ui -> InterestDto.fromInterest(uriInfo, ui.getInterest()))
                .toList();
        return Response.ok(new GenericEntity<>(interestDtos) {}).build();
    }

    //TODO: podria ser put? porque si ya existe no se crea uno nuevo
    @POST
    @Path("/{userId}/interests")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserInterest(
            @PathParam("userId") final long userId
            /* TODO: @Valid AddUserInterestForm form with interestId */
    ) {
        // TODO: Create form class with interestId
        // TODO: Implement adding interest to user
        // return Response.created(UriUtils.getUserInterestUri(uriInfo, userId, interestId)).build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    //TODO: Lo mismo, lo unificaria en un solo endpoint con PUT
    @DELETE
    @Path("/{userId}/interests/{interestId}")
    public Response removeUserInterest(
            @PathParam("userId") final long userId,
            @PathParam("interestId") final long interestId
    ) {
        // TODO: Implement removing interest from user
        // return Response.noContent().build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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

        return Response.ok(image.getData())
                .header("Content-Type", "image/jpeg")
                .header("Cache-Control", "max-age=31536000, immutable")
                .build();
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
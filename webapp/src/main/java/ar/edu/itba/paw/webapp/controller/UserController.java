package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.webapp.dto.InterestDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserRatingDto;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.ValidateUserForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Base64;
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
        // TODO: Implement filtering by attendingEvent, university, career, interest, blocked
        final List<User> allUsers = us.findUsers(search, new PageParams(page + 1, size)).getContent();
        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, allUsers);
        return Response.ok(new GenericEntity<>(userDtos) {}).build();
    }

    // TODO: ¿Esto quien lo puede acceder?
    //  Si solo debería poder pedirlo el mismo usuario: conseguir el id del jwt y comparar
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        final Optional<User> maybeUser = us.findUserById(id);
        if (maybeUser.isPresent()) {
            return Response.ok(UserDto.fromUser(uriInfo, maybeUser.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid final CreateUserForm registerForm) {
        byte[] profilePicture = getProfilePictureBytes(registerForm);

        final User user = us.createUser(
                registerForm.getEmail(),
                registerForm.getUsername(),
                registerForm.getFirstName(),
                registerForm.getLastName(),
                registerForm.getOriginUniversity(),
                registerForm.getCareer(),
                profilePicture,
                registerForm.getInterests(),
                registerForm.getPassword(),
                LocaleContextHolder.getLocale()
        );
        return Response.created(UriUtils.getUserUri(uriInfo, user.getId())).build();
    }



    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("@accessHelper.isCurrentUser(#id)")
    public Response updateUser(@PathParam("id") final long id /* TODO: @Valid UpdateUserForm form */) {
        // TODO: Create form class for full user update
        // final User user = us.updateUser(id, username, firstname, lastname, universityName, careerName);
        // return Response.ok(UserDto.fromUser(uriInfo, user)).build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // TODO: Bug - no se esta verificando que el id corresponda al del usuario que esta siendo verificado
    //      Cambiar entonces el verifyUser para que reciba el id del usuario a verificar
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
    @PreAuthorize("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")
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
        final Optional<User> maybeUser = us.findUserById(userId);
        if (maybeUser.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final List<UserInterest> userInterests = interestService.findInterestsByUser(maybeUser.get(), new PageParams(page + 1, size)).getContent();
        // TODO: Create UserInterestDto or reuse InterestDto
        final List<InterestDto> interestDtos = userInterests.stream()
                .map(ui -> InterestDto.fromInterest(uriInfo, ui.getInterest()))
                .toList();
        return Response.ok(new GenericEntity<>(interestDtos) {}).build();
    }

    @POST
    @Path("/{userId}/interests")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserInterest(
            @PathParam("userId") final long userId
            /* TODO: @Valid AddUserInterestForm form with interestId */
    ) {
        // TODO: Verify user is owner (from SecurityContext)
        // TODO: Create form class with interestId
        // TODO: Implement adding interest to user
        // return Response.created(UriUtils.getUserInterestUri(uriInfo, userId, interestId)).build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/{userId}/interests/{interestId}")
    public Response removeUserInterest(
            @PathParam("userId") final long userId,
            @PathParam("interestId") final long interestId
    ) {
        // TODO: Verify user is owner (from SecurityContext)
        // TODO: Implement removing interest from user
        // return Response.noContent().build();
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // ==================== USER RATING (Computed value) ====================

    @GET
    @Path("/{userId}/rating")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRating(@PathParam("userId") final long userId) {
        final Optional<User> maybeUser = us.findUserById(userId);
        if (maybeUser.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // This is a slow computed operation
        final Optional<Double> rating = us.findAverageRatingForCreatedEvents(userId);
        // TODO: Get total ratings count (might need to add to UserService)

        return Response.ok(UserRatingDto.fromRating(rating.orElse(null))).build();
    }




    private byte[] getProfilePictureBytes(CreateUserForm form) {
        // Try base64 first
        if (form.getProfilePictureBase64() != null && !form.getProfilePictureBase64().isEmpty()) {
            try {
                return Base64.getDecoder().decode(form.getProfilePictureBase64());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
            try {
                return form.getProfilePicture().getBytes();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}




    // SI NO QUEREMOS USAR BASE64 para el createUser() me funcionó así, pero medio raro
    /*
    @POST
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response createUser(
            @FormDataParam("email") String email,
            @FormDataParam("username") String username,
            @FormDataParam("firstName") String firstName,
            @FormDataParam("lastName") String lastName,
            @FormDataParam("password") String password,
            @FormDataParam("originUniversity") String originUniversity,
            @FormDataParam("career") String career,
            @FormDataParam("interests") List<FormDataBodyPart> interestParts,
            @FormDataParam("profilePicture") InputStream profilePictureStream,
            @FormDataParam("profilePicture") FormDataBodyPart profilePicturePart
    ) {
        byte[] profilePicture = null;
        if (profilePictureStream != null && profilePicturePart != null) {
            try {
                profilePicture = profilePictureStream.readAllBytes();
            } catch (IOException e) {
                // ignore
            }
        }

        List<String> interests = interestParts != null
                ? interestParts.stream().map(FormDataBodyPart::getValue).toList()
                : null;

        final User user = us.createUser(
                email,
                username,
                firstName,
                lastName,
                originUniversity,
                career,
                profilePicture,
                interests,
                password,
                LocaleContextHolder.getLocale()
        );

        return Response.created(UriUtils.getUserUri(uriInfo, user.getId())).build();
    }*/
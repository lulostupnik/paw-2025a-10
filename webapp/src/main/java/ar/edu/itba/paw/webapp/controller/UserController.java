package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.UserRating;
import ar.edu.itba.paw.models.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidImageException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserInterestNotFoundException;
import ar.edu.itba.paw.webapp.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserInterestDto;
import ar.edu.itba.paw.webapp.dto.UserPrivateDto;
import ar.edu.itba.paw.webapp.dto.UserRatingDto;
import ar.edu.itba.paw.webapp.form.AddUserInterestForm;
import ar.edu.itba.paw.webapp.form.BlockUserForm;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.EditUserForm;
import ar.edu.itba.paw.webapp.form.PasswordForm;
import ar.edu.itba.paw.webapp.form.PatchUserForm;
import ar.edu.itba.paw.webapp.form.ForgotPasswordForm;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.ValidateUserForm;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ar.edu.itba.paw.webapp.utils.CacheUtils;

import ar.edu.itba.paw.webapp.auth.JwtUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private InterestService interestService;

    @Autowired
    private JwtUtils jwtTokenUtil;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest request;

    private ResponseBuilder withAuthTokens(final ResponseBuilder builder, final User user) {
        final ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
        uriBuilder.path("/api");
        return builder
                .header("X-GoTogether-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user))
                .header("X-GoTogether-RefreshToken", jwtTokenUtil.generateRefreshToken(uriBuilder, user));
    }

    @GET
    @Produces(CustomMediaType.APPLICATION_USER_PRIVATE_LIST)
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
        final List<UserPrivateDto> userDtos = UserPrivateDto.fromUserCollection(uriInfo, allUsers.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(userDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, allUsers).build();
    }

    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_USER)
    public Response getById(@Context Request req, @PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        // Vary the ETag by media type so the public and private representations never share an ETag.
        return CacheUtils.withEtag(req, user, CustomMediaType.APPLICATION_USER,
                () -> UserDto.fromUser(uriInfo, user));
    }

    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_USER_PRIVATE)
    @PreAuthorize("hasRole('ADMIN') or @accessHelper.isCurrentUser(#id)")
    public Response getByIdAdmin(@Context Request req, @PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        return CacheUtils.privateWithEtag(req, user, CustomMediaType.APPLICATION_USER_PRIVATE,
                () -> UserPrivateDto.fromUser(uriInfo, user)); //@TODO creo que tiene sentido, usa mismo hash para el modelo USER, pero distingue el media type.
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_USER)
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
    @Consumes(CustomMediaType.APPLICATION_USER_PASSWORD)
    public Response requestPasswordReset(@Valid final ForgotPasswordForm form) {
        us.initiatePasswordReset(form.getEmail());
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_USER)
    @PreAuthorize("@accessHelper.isCurrentUser(#id) or hasRole('ADMIN')")
    public Response patchUser(@PathParam("id") final long id, @Valid PatchUserForm form) {
        final User user = us.patchUser(id, form.getUsername(), form.getFirstName(), form.getLastName(), form.getOriginUniversity(), form.getCareer());
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    // ==================== PASSWORD ====================

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_USER_PASSWORD)
    @PreAuthorize("@accessHelper.isCurrentUser(#id)")
    public Response updatePassword(
            @PathParam("id") final long id,
            @Valid final PasswordForm form
    ) {
        us.updatePassword(id, form.getPassword());
        return Response.noContent().build();
    }

    // ==================== PASSWORD RESET (token) ====================

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_USER_PASSWORD_RESET)
    public Response resetPassword(
            @PathParam("id") final long id,
            @Valid final ResetPasswordForm form
    ) {
        final User user = us.resetPassword(id, form.getToken(), form.getPassword());
        return withAuthTokens(Response.noContent(), user).build();
    }

    // ==================== ACCOUNT VERIFICATION (token) ====================

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_USER_VERIFICATION)
    public Response verifyUser(
            @PathParam("id") final long id,
            @Valid final ValidateUserForm form
    ) {
        final User user = us.verifyUser(id, form.getValidationToken());
        return withAuthTokens(Response.noContent(), user).build();
    }


    // ==================== BLOCKED STATUS ====================

    @PATCH
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_USER_BLOCKED)
    @PreAuthorize("hasRole('ADMIN')")
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
    @Produces(CustomMediaType.APPLICATION_USER_INTEREST_LIST)
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
    @Consumes(CustomMediaType.APPLICATION_USER_INTEREST)
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
    @Produces(CustomMediaType.APPLICATION_USER_INTEREST)
    public Response getUserInterest(
            @Context Request req,
            @PathParam("userId") final long userId,
            @PathParam("interestId") final long interestId
    ) {
        final UserInterest userInterest = interestService.findUserInterest(userId, interestId)
                .orElseThrow(() -> new UserInterestNotFoundException(userId, interestId));
        return CacheUtils.withEtag(req, userInterest, () -> UserInterestDto.fromUserInterest(uriInfo, userInterest));
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
    @Produces(CustomMediaType.APPLICATION_USER_RATING)
    public Response getUserRating(@Context Request req, @PathParam("userId") final long userId) {
        us.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        final UserRating rating = us.getUserRating(userId);
        return CacheUtils.withEtag(req, rating, () -> UserRatingDto.fromUserRating(uriInfo, rating));
    }




    // ==================== USER PROFILE PICTURE ====================

    @GET
    @Path("/{userId}/profilePicture")
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response getUserProfilePicture(@Context Request req, @PathParam("userId") final long userId) {
        final Image image = us.getProfilePicture(userId).orElseThrow(() -> new ImageNotFoundException("Profile picture not found"));
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getData())
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"profile_%d.jpg\"", userId));
        return CacheUtils.withEtag(req, image, responseBuilder);
    }

    // TODO: parece que hay business logic. Arreglar.
    // TODO: concluision: dejarlo asi pero habilitar para que hayan eventos sin foto de perfil en el frontend
    @PUT
    @Path("/{userId}/profilePicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response updateUserProfilePicture(
            @PathParam("userId") final long userId,
            @FormDataParam("profilePicture") final InputStream profilePictureStream
    ) {
        if (profilePictureStream == null) {
            throw new InvalidImageException("Profile picture is required");
        }
        try {
            final byte[] bytes = profilePictureStream.readAllBytes();
            us.updateProfilePicture(userId, bytes);
        } catch (IOException e) {
            throw new InvalidImageException("Failed to read profile picture");
        }
        final Image image = us.getProfilePicture(userId).orElseThrow(() -> new ImageNotFoundException("Profile picture not found"));

        return Response.ok(image.getData())
                .contentLocation(UriUtils.getUserProfilePictureUri(uriInfo, userId))
                .header("Content-Type", "image/jpeg")
                .build();
    }
}

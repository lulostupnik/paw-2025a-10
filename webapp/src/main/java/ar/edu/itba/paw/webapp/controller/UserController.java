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
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserInterestNotFoundException;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserInterestDto;
import ar.edu.itba.paw.webapp.dto.UserPrivateDto;
import ar.edu.itba.paw.webapp.dto.UserRatingDto;
import ar.edu.itba.paw.webapp.form.AddUserInterestForm;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.ForgotPasswordForm;
import ar.edu.itba.paw.webapp.form.PatchUserForm;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.ImageUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.io.InputStream;
import java.util.List;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private InterestService interestService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_USER_LIST)
    @PreAuthorize("@accessHelper.canListUsers(#search, #blocked)")
    public Response listUsers(
            @QueryParam("attendingEvent") Long attendingEventId,
            @QueryParam("university") Long universityId,
            @QueryParam("career") Long careerId,
            @QueryParam("interest") Long interestId,
            @QueryParam("search") @P("search") String search,
            @QueryParam("blocked") @P("blocked") Boolean blocked,
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
    @Produces(GoTogetherMediaType.APPLICATION_USER_PUBLIC)
    public Response getById(@Context Request req, @PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        // Vary the ETag by media type so the public and full representations never share an ETag.
        return CacheUtils.withEtag(req, user, GoTogetherMediaType.APPLICATION_USER_PUBLIC,
                () -> UserDto.fromUser(uriInfo, user));
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_USER)
    @PreAuthorize("hasRole('ADMIN') or @accessHelper.isCurrentUser(#id)")
    public Response getByIdAdmin(@Context Request req, @PathParam("id") final long id) {
        final User user = us.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
        return CacheUtils.privateWithEtag(req, user, GoTogetherMediaType.APPLICATION_USER,  ////@TODO creo que tiene sentido, usa mismo hash para el modelo USER, pero distingue el media type.
                () -> UserPrivateDto.fromUser(uriInfo, user));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_USER)
    @Produces(GoTogetherMediaType.APPLICATION_USER_PUBLIC)
    public Response createUser(@Valid @NotNull final CreateUserForm registerForm) {
        final User user = us.createUser(
                registerForm.getEmail(),
                registerForm.getUsername(),
                registerForm.getFirstName(),
                registerForm.getLastName(),
                registerForm.getUniversityId(),
                registerForm.getCareerId(),
                registerForm.getInterestIds(),
                registerForm.getPassword(),
                LocaleContextHolder.getLocale()
        );
        return Response.created(UriUtils.getUserUri(uriInfo, user.getId()))
                .entity(UserDto.fromUser(uriInfo, user))
                .build();
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_USER_PASSWORD)
    public Response requestPasswordReset(@Valid @NotNull final ForgotPasswordForm form) {
        us.initiatePasswordReset(form.getEmail());
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_USER)
    @PreAuthorize("@accessHelper.canPatchUser(#id, #form)")
    @Produces(GoTogetherMediaType.APPLICATION_USER_PUBLIC)
    public Response patchUser(@PathParam("id") final long id, @Valid @NotNull PatchUserForm form) {
        final User user = us.patchUser(id, form.getUsername(), form.getFirstName(), form.getLastName(),
                form.getUniversityId(), form.getCareerId(), form.getPassword(), form.getBlocked());
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }


    // ==================== USER INTERESTS ====================

    @GET
    @Path("/{userId}/interests")
    @Produces(GoTogetherMediaType.APPLICATION_USER_INTEREST_LIST)
    public Response listUserInterests(
            @PathParam("userId") final long userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final Page<UserInterest> userInterests = interestService.findInterestsByUser(userId, new PageParams(page, size));
        final List<UserInterestDto> interestDtos = UserInterestDto.fromUserInterestCollection(uriInfo, userInterests.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(interestDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, userInterests).build();
    }

    @POST
    @Path("/{userId}/interests")
    @Consumes(GoTogetherMediaType.APPLICATION_USER_INTEREST)
    @Produces(GoTogetherMediaType.APPLICATION_USER_INTEREST)
    public Response addUserInterest(
            @PathParam("userId") final long userId,
            @Valid @NotNull final AddUserInterestForm form
    ) {
        final UserInterest userInterest = interestService.addUserInterest(userId, form.getInterestId());
        return Response.created(UriUtils.getUserInterestUri(uriInfo, userId, form.getInterestId()))
                .entity(UserInterestDto.fromUserInterest(uriInfo, userInterest))
                .build();
    }

    @GET
    @Path("/{userId}/interests/{interestId}")
    @Produces(GoTogetherMediaType.APPLICATION_USER_INTEREST)
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
    @Produces(GoTogetherMediaType.APPLICATION_USER_RATING)
    public Response getUserRating(@Context Request req, @PathParam("userId") final long userId) {
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
                .header(HttpHeaders.CONTENT_TYPE, ImageUtils.detectContentType(image.getData()))
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"profile_%d\"", userId));
        return CacheUtils.withEtag(req, image, responseBuilder);
    }

    @PUT
    @Path("/{userId}/profilePicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({"image/jpeg", "image/png", "image/webp"})
    public Response updateUserProfilePicture(
            @PathParam("userId") final long userId,
            @FormDataParam("profilePicture") final InputStream profilePictureStream
    ) {
        final byte[] bytes = ImageUtils.readImage(profilePictureStream);
        final Image image = us.updateProfilePicture(userId, bytes);
        return Response.ok(image.getData())
                .contentLocation(UriUtils.getUserProfilePictureUri(uriInfo, userId))
                .header(HttpHeaders.CONTENT_TYPE, ImageUtils.detectContentType(image.getData()))
                .build();
    }
}

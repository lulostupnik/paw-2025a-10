package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.ValidateUserForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

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
    @Context
    private UriInfo uriInfo;
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers() {
        final List<User> allUsers = us.findUsers(null, new PageParams(1,10)).getContent(); //TODO: check page size

        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, allUsers);
        return Response.ok(new GenericEntity<>(userDtos) {}).build();
    }
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
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

    // SI NO QUEREMOS USAR BASE64 me funcionó así, pero medio raro
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


    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        final Optional<User> maybeUser = us.findUserById(id);
        if (maybeUser.isPresent()) {
            return Response.ok( UserDto.fromUser(uriInfo, maybeUser.get())).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // TODO: Bug - no se esta verificando que el id corresponda al del usuario que esta siendo verificado
    //      Cambiar entonces el verifyUser para que reciba el id del usuario a verificar
    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response validateUser(@PathParam("id") final long id,
                                 @Valid final ValidateUserForm form) {
        final User verifiedUser = us.verifyUser(form.getValidationToken());
        if (verifiedUser == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(UserDto.fromUser(uriInfo, verifiedUser)).build();
    }
}

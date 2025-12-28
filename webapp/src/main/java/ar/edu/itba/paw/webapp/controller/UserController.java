package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utils.ImageUtils.getBytes;

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
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid @NotNull final CreateUserForm registerForm) {
        byte[] profilePicture = getBytes(registerForm.getProfilePicture());

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

        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();
        return Response.created(UriUtils.getUserUri(uriInfo, user.getId())).build();
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
}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.webapp.form.UploadImageForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("images")
@Component
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces({"image/jpeg", "image/png", "image/gif", "image/webp"}) // ¿o dejar solo jpeg?
    public Response getImageById(@PathParam("id") final long id) {
        final Image image = imageService.findImage(id).orElseThrow(() -> new ImageNotFoundException("Image with id " + id + " not found"));
        // TODO: Determine actual content type from image data or store it
        return Response.ok(image.getData())
                .header("Content-Type", "image/jpeg")
                .header("Cache-Control", "max-age=31536000, immutable")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(@Valid final UploadImageForm form) {
        final long imageId = imageService.createImage(form.getImageData());
        return Response.created(UriUtils.getImageUri(uriInfo, imageId)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteImage(@PathParam("id") final long id) {
        imageService.deleteImage(id);
        return Response.noContent().build();
    }
}

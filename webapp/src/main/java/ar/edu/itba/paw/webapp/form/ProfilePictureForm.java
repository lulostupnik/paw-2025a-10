package ar.edu.itba.paw.webapp.form;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.NotNull;
import java.io.InputStream;

public class ProfilePictureForm {

    @FormDataParam("profilePicture")
    @NotNull
    private InputStream profilePictureStream;

    @FormDataParam("profilePicture")
    private FormDataContentDisposition profilePictureDetails;

    private byte[] bytes;

    public byte[] getBytes() {
        if (bytes == null && profilePictureStream != null) {
            try {
                bytes = profilePictureStream.readAllBytes();
            } catch (Exception e) {
                bytes = new byte[0];
            }
        }
        return bytes;
    }

    public InputStream getProfilePictureStream() {
        return profilePictureStream;
    }

    public void setProfilePictureStream(InputStream profilePictureStream) {
        this.profilePictureStream = profilePictureStream;
    }

    public org.glassfish.jersey.media.multipart.FormDataContentDisposition getProfilePictureDetails() {
        return profilePictureDetails;
    }

    public void setProfilePictureDetails(org.glassfish.jersey.media.multipart.FormDataContentDisposition profilePictureDetails) {
        this.profilePictureDetails = profilePictureDetails;
    }
}

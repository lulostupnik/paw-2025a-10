package ar.edu.itba.paw.webapp.form;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

public class FlyerForm {

    @FormDataParam("flyer")
    @NotNull
    private InputStream flyerStream;

    @FormDataParam("flyer")
    private FormDataContentDisposition flyerDetails;

    private byte[] bytes;

    public byte[] getBytes() {
        if (bytes == null && flyerStream != null) {
            try {
                bytes = flyerStream.readAllBytes();
            } catch (Exception e) {
                bytes = new byte[0];
            }
        }
        return bytes;
    }

    public InputStream getFlyerStream() {
        return flyerStream;
    }

    public void setFlyerStream(InputStream flyerStream) {
        this.flyerStream = flyerStream;
    }

    public org.glassfish.jersey.media.multipart.FormDataContentDisposition getFlyerDetails() {
        return flyerDetails;
    }

    public void setFlyerDetails(org.glassfish.jersey.media.multipart.FormDataContentDisposition flyerDetails) {
        this.flyerDetails = flyerDetails;
    }
}

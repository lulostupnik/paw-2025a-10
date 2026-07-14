package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidImage;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class UpdateFlyerForm {

    @ValidImage
    @FormDataParam("flyer")
    private byte[] flyer;

    public byte[] getFlyer() {
        return flyer;
    }

    public void setFlyer(byte[] flyer) {
        this.flyer = flyer;
    }
}

package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class BlockUserForm {

    @NotNull
    private Boolean blocked;

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}

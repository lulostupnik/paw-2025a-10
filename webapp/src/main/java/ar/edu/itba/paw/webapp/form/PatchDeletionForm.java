package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PatchDeletionForm {

    @NotNull
    private Boolean deleted;

    @Size(max = 1000)
    private String deletionMessage;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletionMessage() {
        return deletionMessage;
    }

    public void setDeletionMessage(String deletionMessage) {
        this.deletionMessage = deletionMessage;
    }
}

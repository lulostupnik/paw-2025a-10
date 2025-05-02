package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class DeleteForm {
    @Size
    (max = 50)
    private String description;

    public DeleteForm() {
    }
    public DeleteForm(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

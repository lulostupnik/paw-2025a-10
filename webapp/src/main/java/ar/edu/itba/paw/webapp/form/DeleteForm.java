package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.RoleBasedMessage;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RoleBasedMessage // Our custom validator
public class DeleteForm {

    @Min(1) // Better validation for ID than NotEmpty (which is for strings)
    private int id;

    @Size(min = 2, max = 2047) // This will only be checked if the field is not null
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{message: \"" + message + "\"}";
    }
}
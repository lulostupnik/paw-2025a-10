package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class DeleteForm {

    @Min(1)
    private int id;

    @Size( max = 2047)
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
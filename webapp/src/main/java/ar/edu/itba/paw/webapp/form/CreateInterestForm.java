package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateInterestForm {

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String name_en;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String name_es;

    public CreateInterestForm(String name_en, String name_es) {
        this.name_en = name_en;
        this.name_es = name_es;
    }

    public String getName_en() {
        return name_en;
    }
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
    public String getName_es() {
        return name_es;
    }
    public void setName_es(String name_es) {
        this.name_es = name_es;
    }

}

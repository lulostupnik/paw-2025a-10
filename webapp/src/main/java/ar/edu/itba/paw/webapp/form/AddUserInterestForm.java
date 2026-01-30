package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class AddUserInterestForm {

    @NotNull
    private Long interestId; // TODO: en realidad quizas estaría bueno que mande el nombre en vez del id.

    public Long getInterestId() {
        return interestId;
    }

    public void setInterestId(Long interestId) {
        this.interestId = interestId;
    }
}

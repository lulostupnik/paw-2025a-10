package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class AddUserInterestForm {

    @NotNull
    private Long interestId;

    public Long getInterestId() {
        return interestId;
    }

    public void setInterestId(Long interestId) {
        this.interestId = interestId;
    }
}

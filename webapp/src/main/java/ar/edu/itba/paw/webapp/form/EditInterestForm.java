package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidInterest;

public class EditInterestForm {
    @ValidInterest
    private long[] interests;

    public long[] getInterests() {
        return interests;
    }
    public void setInterests(long[] interests) {
        this.interests = interests;}

}

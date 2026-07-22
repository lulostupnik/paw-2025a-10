package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.enums.ReportStatus;

import javax.validation.constraints.NotNull;

public class UpdateReportStatusForm {

    @NotNull
    private ReportStatus status;

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
}

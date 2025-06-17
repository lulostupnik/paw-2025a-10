package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.enums.ReportReason;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateReportForm {
    @NotNull
    private String reportType;

    @NotNull
    private long targetId;


    @Size(min = 2, max = 2047)
    @NotNull
    private String description;

    @NotNull
    private ReportReason reason;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }


    public String getReportType() {
        return reportType;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

}

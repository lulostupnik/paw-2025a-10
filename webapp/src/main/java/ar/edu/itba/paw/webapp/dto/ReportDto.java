package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportDto {

    private long id;
    private String description;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Links links;

    public static ReportDto fromReport(final UriInfo uriInfo, final Report report) {
        final ReportDto dto = new ReportDto();
        dto.id = report.getId();
        dto.description = report.getDescription();
        dto.reason = report.getReason().name();
        dto.status = report.getStatus().name();
        dto.createdAt = report.getCreatedAt();
        dto.updatedAt = report.getUpdatedAt();

        final Links links = new Links();
        links.selfUrl = UriUtils.getReportUri(uriInfo, report.getId());
        links.reportedUserUrl = UriUtils.getUserUri(uriInfo, report.getReportedUser().getId());
        links.reportingUserUrl = UriUtils.getUserUri(uriInfo, report.getReportingUser().getId());
        links.targetUrl = resolveTargetUri(uriInfo, report);
        dto.links = links;

        return dto;
    }

    private static URI resolveTargetUri(final UriInfo uriInfo, final Report report) {
        if (report.getJourneyResponse() != null) {
            return UriUtils.getJourneyResponseUri(
                    uriInfo,
                    report.getJourneyResponse().getJourney().getId(),
                    report.getJourneyResponse().getId()
            );
        }
        if (report.getEventResponse() != null) {
            return UriUtils.getEventResponseUri(
                    uriInfo,
                    report.getEventResponse().getEvent().getId(),
                    report.getEventResponse().getId()
            );
        }
        if (report.getJourney() != null) {
            return UriUtils.getJourneyUri(uriInfo, report.getJourney().getId());
        }
        if (report.getEvent() != null) {
            return UriUtils.getEventUri(uriInfo, report.getEvent().getId());
        }
        return null;
    }

    public static List<ReportDto> fromReportCollection(final UriInfo uriInfo, final Collection<Report> reports) {
        return reports.stream().map(report -> fromReport(uriInfo, report)).toList();
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI reportedUserUrl;
        private URI reportingUserUrl;
        private URI targetUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getReportedUserUrl() { return reportedUserUrl; }
        public URI getReportingUserUrl() { return reportingUserUrl; }
        public URI getTargetUrl() { return targetUrl; }
    }
}

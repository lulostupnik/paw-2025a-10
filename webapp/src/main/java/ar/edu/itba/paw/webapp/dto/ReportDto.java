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

        // TODO: revisar. ¿No deberían ser else if en realidad?
        if (report.getJourney() != null) {
            links.journeyUrl = UriUtils.getJourneyUri(uriInfo, report.getJourney().getId());
        }
        if (report.getEvent() != null) {
            links.eventUrl = UriUtils.getEventUri(uriInfo, report.getEvent().getId());
        }
        if (report.getJourneyResponse() != null) {
            links.journeyResponseUrl = UriUtils.getJourneyResponseUri(uriInfo,
                    report.getJourneyResponse().getJourney().getId(),
                    report.getJourneyResponse().getId());
        }
        if (report.getEventResponse() != null) {
            links.eventResponseUrl = UriUtils.getEventResponseUri(uriInfo,
                    report.getEventResponse().getEvent().getId(),
                    report.getEventResponse().getId());
        }
        dto.links = links;

        return dto;
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
        private URI journeyUrl;
        private URI eventUrl;
        private URI journeyResponseUrl;
        private URI eventResponseUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getReportedUserUrl() { return reportedUserUrl; }
        public URI getReportingUserUrl() { return reportingUserUrl; }
        public URI getJourneyUrl() { return journeyUrl; }
        public URI getEventUrl() { return eventUrl; }
        public URI getJourneyResponseUrl() { return journeyResponseUrl; }
        public URI getEventResponseUrl() { return eventResponseUrl; }
    }
}

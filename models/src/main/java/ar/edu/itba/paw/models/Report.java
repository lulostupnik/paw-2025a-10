package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "reports_id_seq")
    @SequenceGenerator(sequenceName = "reports_id_seq", name =
            "reports_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    //FIXME:check eager vs lazy

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reporting_user_id", nullable = false)
    private User reportingUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journey_id")
    private Journey journey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journey_response_id")
    private JourneyResponse journeyResponse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_response_id")
    private EventResponse eventResponse;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 255, nullable = false)
    private ReportReason reason;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    /* hibernate */ Report() {
    }

    public Report(Long id, User reported, User reporting, String desc, ReportReason reason, Journey journey, Event event, EventResponse eventResponse, JourneyResponse journeyResponse, boolean deleted, ReportStatus status){
        this.id = id;
        this.reportedUser = reported;
        this.reportingUser = reporting;
        this.description = desc;
        this.reason = reason;
        this.deleted = deleted;
        this.status = status;
        this.journey = journey;
        this.event = event;
        this.journeyResponse = journeyResponse;
        this.eventResponse = eventResponse;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Report(User reportedUser, User reportingUser, String description, ReportReason reason) {
        this.reportedUser = reportedUser;
        this.reportingUser = reportingUser;
        this.description = description;
        this.reason = reason;
        this.deleted = false;
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Report(User reportedUser, User reportingUser, Journey journey, String description, ReportReason reason) {
        this(reportedUser, reportingUser, description, reason);
        this.journey = journey;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Report(User reportedUser, User reportingUser, Event event, String description, ReportReason reason) {
        this(reportedUser, reportingUser, description, reason);
        this.event = event;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Report(User reportedUser, User reportingUser, EventResponse eventResponse, String description, ReportReason reason) {
        this(reportedUser, reportingUser, description, reason);
        this.eventResponse = eventResponse;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Report(User reportedUser, User reportingUser, JourneyResponse journeyResponse, String description, ReportReason reason) {
        this(reportedUser, reportingUser, description, reason);
        this.journeyResponse = journeyResponse;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void resolve() {
        this.status = ReportStatus.RESOLVED;
    }

    public void dismiss() {
        this.status = ReportStatus.DISMISSED;
    }

    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                '}';
    }
}


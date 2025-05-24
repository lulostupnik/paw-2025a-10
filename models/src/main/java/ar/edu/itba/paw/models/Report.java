package ar.edu.itba.paw.models;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporting_user_id", nullable = false)
    private User reportingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id")
    private Journey journey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

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

    public Report(User reportedUser, User reportingUser, String description, String reason) {
        this.reportedUser = reportedUser;
        this.reportingUser = reportingUser;
        this.description = description;
        this.reason = reason;
        this.deleted = false;
        this.status = ReportStatus.PENDING;
    }

    public Report(User reportedUser, User reportingUser, Journey journey, String description, String reason) {
        this(reportedUser, reportingUser, description, reason);
        this.journey = journey;
    }

    public Report(User reportedUser, User reportingUser, Event event, String description, String reason) {
        this(reportedUser, reportingUser, description, reason);
        this.event = event;
    }

    // Business methods
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

// Enum for report status
enum ReportStatus {
    PENDING,
    UNDER_REVIEW,
    RESOLVED,
    DISMISSED
}

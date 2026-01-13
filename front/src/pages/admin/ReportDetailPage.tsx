import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import ErrorState from "@/components/ErrorState";

type ReportStatus = "PENDING" | "UNDER_REVIEW" | "RESOLVED" | "DISMISSED" | string;
type ReportReason =
    | "SPAM"
    | "HARRASMENT"
    | "INAPPROPRIATE_CONTENT"
    | "MISINFORMATION"
    | "HATE_SPEECH"
    | "VIOLENCE"
    | "OTHER"
    | string;

interface ReportUser {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    blocked: boolean;
}

interface ReportJourney {
    id: number;
    description: string;
    startDate?: string | null;
    endDate?: string | null;
    deleted: boolean;
}

interface ReportEvent {
    id: number;
    title: string;
    description: string;
    date?: string | null;
    address?: string | null;
    deleted: boolean;
}

interface ReportJourneyResponse {
    id: number;
    message: string;
    dateTime?: string | null;
    deleted: boolean;
    journey: {
        id: number;
        user: {
            username: string;
        };
    };
}

interface ReportEventResponse {
    id: number;
    message: string;
    dateTime?: string | null;
    deleted: boolean;
    event: {
        id: number;
        title: string;
    };
}

interface ReportDetail {
    id: number;
    reportedUser: ReportUser;
    reportingUser: ReportUser;
    reason: ReportReason;
    description?: string | null;
    status: ReportStatus;
    journey?: ReportJourney | null;
    event?: ReportEvent | null;
    journeyResponse?: ReportJourneyResponse | null;
    eventResponse?: ReportEventResponse | null;
}

const REPORTS_LIST_PATH = "/dashboard/reports";
const API_ENDPOINTS = {
    reportDetail: "/api/reports/:id", // TODO: replace with real endpoint.
    updateStatus: "/api/reports/:id/status", // TODO: replace with real endpoint.
    deleteJourney: "/api/journeys/:id/delete", // TODO: replace with real endpoint.
    deleteEvent: "/api/events/:id/delete", // TODO: replace with real endpoint.
    deleteJourneyComment: "/api/journeys/reply/:id/delete", // TODO: replace with real endpoint.
    deleteEventComment: "/api/events/reply/:id/delete", // TODO: replace with real endpoint.
    blockUser: "/api/users/:id/block", // TODO: replace with real endpoint.
    unblockUser: "/api/users/:id/unblock", // TODO: replace with real endpoint.
};

const withId = (template: string, value: number | string) => template.replace(":id", String(value));

const FALLBACK_REPORT: ReportDetail = {
    id: 0,
    reportedUser: {
        id: 101,
        firstname: "Alex",
        lastname: "Smith",
        username: "alexsmith",
        blocked: false,
    },
    reportingUser: {
        id: 202,
        firstname: "Jamie",
        lastname: "Lee",
        username: "jamie",
        blocked: false,
    },
    reason: "SPAM",
    description: "Placeholder description for visual QA.",
    status: "UNDER_REVIEW",
    journey: {
        id: 301,
        description: "Sample journey description used as placeholder content.",
        startDate: "2025-02-10",
        endDate: "2025-02-18",
        deleted: false,
    },
    event: null,
    journeyResponse: null,
    eventResponse: null,
};

const formatDateTime = (value: string, locale: string) => {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return new Intl.DateTimeFormat(locale, {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
};

const getReasonLabel = (reason: ReportReason, t: (key: string, options?: { defaultValue?: string }) => string) => {
    const reasonKey = {
        SPAM: "report.reason.spam",
        HARRASMENT: "report.reason.harassment",
        INAPPROPRIATE_CONTENT: "report.reason.inappropriate",
        MISINFORMATION: "report.reason.misinformation",
        HATE_SPEECH: "report.reason.hate_speech",
        VIOLENCE: "report.reason.violence",
        OTHER: "report.reason.other",
    }[reason];

    return reasonKey ? t(reasonKey) : reason;
};

const getStatusLabel = (status: ReportStatus, t: (key: string, options?: { defaultValue?: string }) => string) => {
    const statusKey = {
        PENDING: "report.status.pending",
        UNDER_REVIEW: "report.status.under_review",
        RESOLVED: "report.status.resolved",
        DISMISSED: "report.status.dismissed",
    }[status];

    return statusKey ? t(statusKey) : status;
};

const getStatusClass = (status: ReportStatus) => {
    switch (status) {
        case "PENDING":
            return "status-pending";
        case "UNDER_REVIEW":
            return "status-under-review";
        case "RESOLVED":
            return "status-resolved";
        case "DISMISSED":
            return "status-dismissed";
        default:
            return undefined;
    }
};

const getContentBadge = (report: ReportDetail, t: (key: string, options?: { defaultValue?: string }) => string) => {
    if (report.journey) {
        return { label: t("report.type.journey"), className: "journey-badge" };
    }
    if (report.event) {
        return { label: t("report.type.event"), className: "event-badge" };
    }
    if (report.journeyResponse) {
        return { label: t("report.type.journey.comment"), className: "comment-badge" };
    }
    if (report.eventResponse) {
        return { label: t("report.type.event.comment"), className: "comment-badge" };
    }
    return null;
};

export default function ReportDetailPage() {
    const { t, locale } = useI18n();
    const { id } = useParams();
    const location = useLocation();
    const stateReport = (location.state as { report?: ReportDetail } | null)?.report ?? null;
    const [report, setReport] = useState<ReportDetail | null>(stateReport);
    const [loading, setLoading] = useState(!stateReport);
    const [blockModalOpen, setBlockModalOpen] = useState(false);

    useEffect(() => {
        if (stateReport) {
            return;
        }
        if (!id) {
            setLoading(false);
            return;
        }

        setLoading(true);
        const detailUrl = withId(API_ENDPOINTS.reportDetail, id);
        // TODO: fetch report detail by id from detailUrl and update state.
        setReport({ ...FALLBACK_REPORT, id: Number(id) || FALLBACK_REPORT.id });
        setLoading(false);
    }, [id, stateReport]);

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (loading) {
        return null;
    }

    if (!report) {
        return <ErrorState variant="404" />;
    }

    const reasonLabel = getReasonLabel(report.reason, t);
    const statusLabel = getStatusLabel(report.status, t);
    const statusClass = getStatusClass(report.status);
    const contentBadge = getContentBadge(report, t);
    const canDismiss =
        !report.reportedUser.blocked ||
        (report.journey && !report.journey.deleted) ||
        (report.event && !report.event.deleted) ||
        (report.journeyResponse && !report.journeyResponse.deleted) ||
        (report.eventResponse && !report.eventResponse.deleted);

    const handleStatusChange = (status: ReportStatus) => (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const statusUrl = withId(API_ENDPOINTS.updateStatus, report.id);
        // TODO: call report status update service at statusUrl with { status }.
        console.info("TODO: update report status", { statusUrl, reportId: report.id, status });
    };

    const handleDeleteContent = (type: "journey" | "event" | "journey-comment" | "event-comment", targetId: number) => {
        const deleteUrlMap = {
            journey: withId(API_ENDPOINTS.deleteJourney, targetId),
            event: withId(API_ENDPOINTS.deleteEvent, targetId),
            "journey-comment": withId(API_ENDPOINTS.deleteJourneyComment, targetId),
            "event-comment": withId(API_ENDPOINTS.deleteEventComment, targetId),
        };
        const deleteUrl = deleteUrlMap[type];
        // TODO: call delete endpoint for the reported content at deleteUrl.
        console.info("TODO: delete content", { deleteUrl, type, targetId, reportId: report.id });
    };

    const handleBlockUser = () => {
        const blockUrl = report.reportedUser.blocked
            ? withId(API_ENDPOINTS.unblockUser, report.reportedUser.id)
            : withId(API_ENDPOINTS.blockUser, report.reportedUser.id);
        // TODO: call block/unblock user endpoint at blockUrl.
        console.info("TODO: toggle block user", {
            blockUrl,
            userId: report.reportedUser.id,
            reportId: report.id,
        });
        setBlockModalOpen(false);
    };

    return (
        <div className="report-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">
                                    {t("report.detail.title.for")} {report.reportedUser.username}
                                </h1>
                                <p className="journey-card-subtitle">
                                    {contentBadge && (
                                        <span className={classNames("content-type-badge", contentBadge.className)}>
                                            {contentBadge.label}
                                        </span>
                                    )}{" "}
                                    - {reasonLabel}
                                </p>
                            </div>

                            <div className="detail-content">
                                <h2 className="section-title-landing">{t("report.detail.information")}</h2>

                                <div className="features-grid">
                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.reportedUser")}</h3>
                                        <p className="feature-description">
                                            <strong>{report.reportedUser.firstname}</strong> (@{report.reportedUser.username})
                                            {report.reportedUser.blocked && (
                                                <span className="user-status-badge blocked">
                                                    {t("user.status.blocked")}
                                                </span>
                                            )}
                                        </p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.reportingUser")}</h3>
                                        <p className="feature-description">
                                            <strong>{report.reportingUser.firstname}</strong> (@{report.reportingUser.username})
                                        </p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.reason")}</h3>
                                        <p className="feature-description">{reasonLabel}</p>
                                    </div>

                                    {report.journey && (
                                        <div className="feature-card reported-content-card">
                                            <h3 className="feature-title">{t("report.detail.reported.journey")}</h3>
                                            <div className="reported-content">
                                                <p className="content-preview">{report.journey.description}</p>
                                                <div className="content-meta">
                                                    {report.journey.startDate && (
                                                        <span>
                                                            {t("journey.detail.startDate")}: {report.journey.startDate}
                                                        </span>
                                                    )}
                                                    {report.journey.endDate && (
                                                        <span>
                                                            {t("journey.detail.endDate")}: {report.journey.endDate}
                                                        </span>
                                                    )}
                                                </div>
                                                {report.journey.deleted ? (
                                                    <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                ) : (
                                                    <Link to={`/journeys/${report.journey.id}`} className="view-content-link">
                                                        {t("report.view.original.content")} ↗
                                                    </Link>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {report.event && (
                                        <div className="feature-card reported-content-card">
                                            <h3 className="feature-title">{t("report.detail.reported.event")}</h3>
                                            <div className="reported-content">
                                                <h4>{report.event.title}</h4>
                                                <p className="content-preview">{report.event.description}</p>
                                                <div className="content-meta">
                                                    {report.event.date && (
                                                        <span>
                                                            {t("event.detail.date")}: {report.event.date}
                                                        </span>
                                                    )}
                                                    <span>
                                                        {t("event.detail.location")}: {report.event.address}
                                                    </span>
                                                </div>
                                                {report.event.deleted ? (
                                                    <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                ) : (
                                                    <Link to={`/events/${report.event.id}`} className="view-content-link">
                                                        {t("report.view.original.content")} ↗
                                                    </Link>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {report.journeyResponse && (
                                        <div className="feature-card reported-content-card">
                                            <h3 className="feature-title">{t("report.detail.reported.journey.comment")}</h3>
                                            <div className="reported-content">
                                                <div className="comment-content">
                                                    <p className="content-preview">"{report.journeyResponse.message}"</p>
                                                    <div className="content-meta">
                                                        {report.journeyResponse.dateTime && (
                                                            <span>
                                                                {t("comment.posted.on")}:{" "}
                                                                {formatDateTime(report.journeyResponse.dateTime, locale)}
                                                            </span>
                                                        )}
                                                    </div>
                                                </div>
                                                <div className="parent-content">
                                                    <h5>{t("report.detail.parent.journey")}:</h5>
                                                    <p>{report.journeyResponse.journey.user.username}</p>
                                                    {report.journeyResponse.deleted ? (
                                                        <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                    ) : (
                                                        <Link
                                                            to={`/journeys/${report.journeyResponse.journey.id}`}
                                                            className="view-content-link"
                                                        >
                                                            {t("report.view.original.content")} ↗
                                                        </Link>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                    {report.eventResponse && (
                                        <div className="feature-card reported-content-card">
                                            <h3 className="feature-title">{t("report.detail.reported.event.comment")}</h3>
                                            <div className="reported-content">
                                                <div className="comment-content">
                                                    <p className="content-preview">"{report.eventResponse.message}"</p>
                                                    <div className="content-meta">
                                                        {report.eventResponse.dateTime && (
                                                            <span>
                                                                {t("comment.posted.on")}:{" "}
                                                                {formatDateTime(report.eventResponse.dateTime, locale)}
                                                            </span>
                                                        )}
                                                    </div>
                                                </div>
                                                <div className="parent-content">
                                                    <h5>{t("report.detail.parent.event")}:</h5>
                                                    <p>{report.eventResponse.event.title}</p>
                                                    {report.eventResponse.deleted ? (
                                                        <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                    ) : (
                                                        <Link
                                                            to={`/events/${report.eventResponse.event.id}`}
                                                            className="view-content-link"
                                                        >
                                                            {t("report.view.original.content")} ↗
                                                        </Link>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.description")}</h3>
                                        <p className="feature-description">
                                            {report.description?.trim() ? (
                                                report.description
                                            ) : (
                                                <em>{t("report.no.additional.details")}</em>
                                            )}
                                        </p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.status")}</h3>
                                        <div className="feature-description">
                                            <span className={classNames("status-badge", statusClass)}>{statusLabel}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="detail-actions">
                                <Link to={REPORTS_LIST_PATH} className="btn-text">
                                    {t("report.back")}
                                </Link>

                                <div className="admin-actions">
                                    {report.status === "PENDING" && (
                                        <form className="report-inline-form" onSubmit={handleStatusChange("UNDER_REVIEW")}>
                                            <button type="submit" className="cta-button btn-dele">
                                                {t("report.action.review")}
                                            </button>
                                        </form>
                                    )}

                                    {report.status === "UNDER_REVIEW" && (
                                        <>
                                            {report.journey && !report.journey.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("journey", report.journey!.id)}
                                                >
                                                    {t("report.action.delete_journey")}
                                                </button>
                                            )}
                                            {report.event && !report.event.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("event", report.event!.id)}
                                                >
                                                    {t("report.action.delete_event")}
                                                </button>
                                            )}
                                            {report.journeyResponse && !report.journeyResponse.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("journey-comment", report.journeyResponse!.id)}
                                                >
                                                    {t("report.action.delete_journey_comment")}
                                                </button>
                                            )}
                                            {report.eventResponse && !report.eventResponse.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("event-comment", report.eventResponse!.id)}
                                                >
                                                    {t("report.action.delete_event_comment")}
                                                </button>
                                            )}
                                        </>
                                    )}

                                    {(report.status === "UNDER_REVIEW" || report.status === "RESOLVED") && (
                                        <button
                                            type="button"
                                            className={classNames(
                                                "cta-button",
                                                report.reportedUser.blocked ? "btn-primary" : "btn-danger"
                                            )}
                                            onClick={() => setBlockModalOpen(true)}
                                        >
                                            {report.reportedUser.blocked ? t("user.unblock") : t("user.block")}
                                        </button>
                                    )}

                                    {report.status === "UNDER_REVIEW" && (
                                        <>
                                            <form className="report-inline-form" onSubmit={handleStatusChange("RESOLVED")}>
                                                <button type="submit" className="cta-button btn-tertiary">
                                                    {t("report.action.resolve")}
                                                </button>
                                            </form>
                                            {canDismiss && (
                                                <form className="report-inline-form" onSubmit={handleStatusChange("DISMISSED")}>
                                                    <button type="submit" className="cta-button btn-primary">
                                                        {t("report.action.dismiss")}
                                                    </button>
                                                </form>
                                            )}
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {blockModalOpen && (
                <div className="modal" role="presentation" onClick={() => setBlockModalOpen(false)}>
                    <div
                        className="modal-content"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="blockModalTitle"
                        aria-describedby="blockModalMessage"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="modal-header">
                            <h2 id="blockModalTitle">
                                {report.reportedUser.blocked ? t("user.unblock.confirm.title") : t("user.block.confirm.title")}
                            </h2>
                            <button
                                type="button"
                                className="close-modal"
                                aria-label="Close"
                                onClick={() => setBlockModalOpen(false)}
                            >
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p id="blockModalMessage">
                                {report.reportedUser.blocked
                                    ? t("user.unblock.confirm.message", {
                                          values: { 0: `${report.reportedUser.firstname} ${report.reportedUser.lastname}` },
                                      })
                                    : t("user.block.confirm.message", {
                                          values: { 0: `${report.reportedUser.firstname} ${report.reportedUser.lastname}` },
                                      })}
                            </p>
                            {!report.reportedUser.blocked && (
                                <p className="warning-text">{t("user.block.confirm.warning")}</p>
                            )}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setBlockModalOpen(false)}>
                                {t("user.block.cancel")}
                            </button>
                            <button
                                type="button"
                                className={classNames(
                                    "cta-button",
                                    report.reportedUser.blocked ? "btn-primary" : "btn-danger"
                                )}
                                onClick={handleBlockUser}
                            >
                                {report.reportedUser.blocked ? t("user.unblock.confirm") : t("user.block.confirm")}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

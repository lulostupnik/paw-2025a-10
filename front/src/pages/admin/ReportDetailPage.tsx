import { useEffect, useState, type FormEvent } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import ErrorState from "@/components/ErrorState";
import { deleteEvent, deleteEventResponse } from "@/lib/api/events";
import { deleteJourney, deleteJourneyResponse } from "@/lib/api/journeys";
import { getReportDetail, updateReportStatus, type ReportDetail, type ReportStatus } from "@/lib/api/reports";
import { updateUserBlocked } from "@/lib/api/users";

type ReportReason =
    | "SPAM"
    | "HARASSMENT"
    | "INAPPROPRIATE_CONTENT"
    | "MISINFORMATION"
    | "HATE_SPEECH"
    | "VIOLENCE"
    | "OTHER"
    | string;

const REPORTS_LIST_PATH = "/admin/reports";

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
        HARASSMENT: "report.reason.harassment",
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
    const queryClient = useQueryClient();
    const stateReport = (location.state as { report?: ReportDetail } | null)?.report ?? null;
    const [report, setReport] = useState<ReportDetail | null>(stateReport);
    const [loading, setLoading] = useState(!stateReport);
    const [errorMessage, setErrorMessage] = useState("");
    const [actionError, setActionError] = useState("");
    const [blockModalOpen, setBlockModalOpen] = useState(false);

    useEffect(() => {
        if (stateReport) {
            return;
        }
        if (!id) {
            setLoading(false);
            return;
        }

        let active = true;
        const controller = new AbortController();
        setLoading(true);
        setErrorMessage("");
        getReportDetail(Number(id), controller.signal)
            .then((data) => {
                if (!active) {
                    return;
                }
                setReport(data);
            })
            .catch((error) => {
                if (!active) {
                    return;
                }
                console.error("Failed to load report detail", error);
                setErrorMessage(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
            })
            .finally(() => {
                if (!active) {
                    return;
                }
                setLoading(false);
            });

        return () => {
            active = false;
            controller.abort();
        };
    }, [id, stateReport, t]);

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (loading) {
        return <div className="report-detail-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (errorMessage) {
        return (
            <div className="report-detail-page">
                <ErrorState variant="500" />
            </div>
        );
    }

    if (!report) {
        return <ErrorState variant="404" />;
    }

    const reasonLabel = getReasonLabel(report.reason, t);
    const statusLabel = getStatusLabel(report.status, t);
    const statusClass = getStatusClass(report.status);
    const contentBadge = getContentBadge(report, t);
    const canDismiss = true;
    const reportedUserLabel = report.reportedUser.username;
    const reportingUserLabel = report.reportingUser.username;

    const handleStatusChange = (status: ReportStatus) => (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setActionError("");
        updateReportStatus(report.id, status)
            .then(() => {
                setReport((prev) => (prev ? { ...prev, status } : prev));
                queryClient.invalidateQueries({ queryKey: ["reports"] });
                queryClient.invalidateQueries({ queryKey: ["report", report.id] });
            })
            .catch((error) => {
                console.error("Failed to update report status", error);
                setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
            });
    };

    const markJourneyDeleted = () =>
        setReport((prev) => (prev?.journey ? { ...prev, journey: { ...prev.journey, deleted: true } } : prev));
    const markEventDeleted = () =>
        setReport((prev) => (prev?.event ? { ...prev, event: { ...prev.event, deleted: true } } : prev));
    const markJourneyResponseDeleted = () =>
        setReport((prev) =>
            prev?.journeyResponse ? { ...prev, journeyResponse: { ...prev.journeyResponse, deleted: true } } : prev
        );
    const markEventResponseDeleted = () =>
        setReport((prev) =>
            prev?.eventResponse ? { ...prev, eventResponse: { ...prev.eventResponse, deleted: true } } : prev
        );

    const handleDeleteContent = async (type: "journey" | "event" | "journey-comment" | "event-comment") => {
        setActionError("");
        try {
            if (type === "journey" && report.journey) {
                await deleteJourney(report.journey.id);
                markJourneyDeleted();
            }
            if (type === "event" && report.event) {
                await deleteEvent(report.event.id);
                markEventDeleted();
            }
            if (type === "journey-comment" && report.journeyResponse) {
                await deleteJourneyResponse(report.journeyResponse.journey.id, report.journeyResponse.id);
                markJourneyResponseDeleted();
            }
            if (type === "event-comment" && report.eventResponse) {
                await deleteEventResponse(report.eventResponse.event.id, report.eventResponse.id);
                markEventResponseDeleted();
            }
        } catch (error) {
            console.error("Failed to delete reported content", error);
            setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
        }
    };

    const handleBlockUser = () => {
        setActionError("");
        updateUserBlocked(report.reportedUser.id, !report.reportedUser.blocked)
            .then(() => {
                setReport((prev) =>
                    prev
                        ? {
                              ...prev,
                              reportedUser: { ...prev.reportedUser, blocked: !prev.reportedUser.blocked },
                          }
                        : prev
                );
            })
            .catch((error) => {
                console.error("Failed to update user status", error);
                setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
            })
            .finally(() => {
                setBlockModalOpen(false);
            });
    };

    const reportHeader = report.description?.trim()
        ? report.description.trim()
        : t("report.no.additional.details");

    return (
        <div className="report-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">
                                    {t("report.detail.title.for")} {reportedUserLabel}
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
                                            <strong>@{reportedUserLabel}</strong>
                                            {report.reportedUser.blocked && (
                                                <span className="user-status-badge blocked">
                                                    {t("user.status.blocked")}
                                                </span>
                                            )}
                                        </p>
                                        {report.reportedUser.email && (
                                            <p className="feature-description">{report.reportedUser.email}</p>
                                        )}
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.reportingUser")}</h3>
                                        <p className="feature-description">
                                            <strong>@{reportingUserLabel}</strong>
                                        </p>
                                        {report.reportingUser.email && (
                                            <p className="feature-description">{report.reportingUser.email}</p>
                                        )}
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
                                                    ) : report.journeyResponse.journey.id > 0 ? (
                                                        <Link
                                                            to={`/journeys/${report.journeyResponse.journey.id}`}
                                                            className="view-content-link"
                                                        >
                                                            {t("report.view.original.content")} ↗
                                                        </Link>
                                                    ) : (
                                                        <span className="view-content-link">{t("report.view.original.content")}</span>
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
                                                    ) : report.eventResponse.event.id > 0 ? (
                                                        <Link
                                                            to={`/events/${report.eventResponse.event.id}`}
                                                            className="view-content-link"
                                                        >
                                                            {t("report.view.original.content")} ↗
                                                        </Link>
                                                    ) : (
                                                        <span className="view-content-link">{t("report.view.original.content")}</span>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("report.detail.description")}</h3>
                                        <p className="feature-description">{reportHeader}</p>
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
                                    {actionError && <span className="warning-text">{actionError}</span>}
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
                                                    onClick={() => handleDeleteContent("journey")}
                                                >
                                                    {t("report.action.delete_journey")}
                                                </button>
                                            )}
                                            {report.event && !report.event.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("event")}
                                                >
                                                    {t("report.action.delete_event")}
                                                </button>
                                            )}
                                            {report.journeyResponse && !report.journeyResponse.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("journey-comment")}
                                                >
                                                    {t("report.action.delete_journey_comment")}
                                                </button>
                                            )}
                                            {report.eventResponse && !report.eventResponse.deleted && (
                                                <button
                                                    type="button"
                                                    className="cta-button btn-dele"
                                                    onClick={() => handleDeleteContent("event-comment")}
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

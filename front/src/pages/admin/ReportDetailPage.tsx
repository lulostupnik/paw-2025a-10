import { apiErrorMessage, apiErrorStatus } from "@/lib/api/client";
import { useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import ErrorState from "@/components/ErrorState";
import { deleteEvent, deleteEventResponse } from "@/lib/api/events";
import { deleteJourney, deleteJourneyResponse } from "@/lib/api/journeys";
import { deleteReport, getReportDetail, updateReportStatus, type ReportDetail, type ReportListItem, type ReportStatus } from "@/lib/api/reports";
import { invalidateUserViewQueries, updateUserBlocked } from "@/lib/api/users";
import ActionMenu, { type ActionMenuItem } from "@/components/ui/ActionMenu";
import PageStatus from "@/components/ui/PageStatus";
import { parseApiDate } from "@/lib/utils/date";

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
    const date = parseApiDate(value);
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
    if (report.journey || report.contentType === "journey") {
        return { label: t("report.type.journey"), className: "journey-badge" };
    }
    if (report.event || report.contentType === "event") {
        return { label: t("report.type.event"), className: "event-badge" };
    }
    if (report.journeyResponse || report.contentType === "journeyResponse") {
        return { label: t("report.type.journey.comment"), className: "comment-badge" };
    }
    if (report.eventResponse || report.contentType === "eventResponse") {
        return { label: t("report.type.event.comment"), className: "comment-badge" };
    }
    return null;
};

const getReportedContentTitleKey = (contentType: ReportDetail["contentType"]) => {
    switch (contentType) {
        case "journey":
            return "report.detail.reported.journey";
        case "event":
            return "report.detail.reported.event";
        case "journeyResponse":
            return "report.detail.reported.journey.comment";
        case "eventResponse":
            return "report.detail.reported.event.comment";
        default:
            return "report.detail.reported.event";
    }
};

const hasResolvedReportContent = (report: ReportDetail | ReportListItem | null): report is ReportDetail =>
    Boolean(report && ("journey" in report || "event" in report || "journeyResponse" in report || "eventResponse" in report));

export default function ReportDetailPage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();
    const location = useLocation();
    const queryClient = useQueryClient();
    const reportId = id ? Number(id) : null;
    const navigationState = location.state as { report?: ReportDetail | ReportListItem; from?: string } | null;
    const stateReport = navigationState?.report ?? null;
    const listReturnPath = navigationState?.from ?? REPORTS_LIST_PATH;
    const matchingStateReport = stateReport && reportId === stateReport.id ? stateReport : null;
    const initialReport = hasResolvedReportContent(matchingStateReport) ? matchingStateReport : null;
    const [report, setReport] = useState<ReportDetail | null>(initialReport);
    const [loading, setLoading] = useState(Boolean(id) && !hasResolvedReportContent(matchingStateReport));
    const [errorMessage, setErrorMessage] = useState("");
    const [errorStatus, setErrorStatus] = useState<number | undefined>(undefined);
    const [actionError, setActionError] = useState("");
    const [blockModalOpen, setBlockModalOpen] = useState(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [actionsOpen, setActionsOpen] = useState(false);
    const actionsButtonRef = useRef<HTMLButtonElement>(null);
    const [syncedId, setSyncedId] = useState(id);
    const [syncedStateReport, setSyncedStateReport] = useState(matchingStateReport);

    if (syncedId !== id || syncedStateReport !== matchingStateReport) {
        setSyncedId(id);
        setSyncedStateReport(matchingStateReport);
        setReport(initialReport);
        setErrorMessage("");
        setErrorStatus(undefined);
        setLoading(Boolean(id) && !hasResolvedReportContent(matchingStateReport));
    }

    useEffect(() => {
        if (!id || hasResolvedReportContent(matchingStateReport)) {
            return;
        }

        let active = true;
        const controller = new AbortController();
        getReportDetail(Number(id), controller.signal)
            .then((data) => {
                if (!active) {
                    return;
                }
                setErrorMessage("");
                setErrorStatus(undefined);
                setReport(data);
            })
            .catch((error) => {
                if (!active) {
                    return;
                }
                console.error("Failed to load report detail", error);
                setErrorStatus(apiErrorStatus(error));
                setErrorMessage(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
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
    }, [id, matchingStateReport, t]);

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (loading) {
        return <PageStatus className="report-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (errorMessage) {
        return (
            <div className="report-detail-page">
                <ErrorState variant={errorStatus === 404 ? "404" : "500"} />
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
    const deletedContentTitle = t(getReportedContentTitleKey(report.contentType));
    const canDismiss = true;
    const reportedUserLabel = report.reportedUser.username;
    const reportingUserLabel = report.reportingUser.username;

    const handleStatusChange = (status: ReportStatus) => {
        setActionError("");
        updateReportStatus(report.id, status)
            .then(() => {
                setReport((prev) => (prev ? { ...prev, status } : prev));
                queryClient.invalidateQueries({ queryKey: ["reports"] });
                queryClient.invalidateQueries({ queryKey: ["report", report.id] });
            })
            .catch((error) => {
                console.error("Failed to update report status", error);
                setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
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
            setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        }
    };

    const handleBlockUser = () => {
        setActionError("");
        updateUserBlocked(report.reportedUser.id, !report.reportedUser.blocked)
            .then(async () => {
                await invalidateUserViewQueries(queryClient, report.reportedUser.id);
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
                setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
            })
            .finally(() => {
                setBlockModalOpen(false);
            });
    };

    const handleDeleteReport = async () => {
        if (!report) {
            return;
        }

        setActionError("");
        try {
            await deleteReport(report.id);
            setDeleteModalOpen(false);
            queryClient.invalidateQueries({ queryKey: ["reports"] });
            queryClient.invalidateQueries({ queryKey: ["report", report.id] });
            navigate(listReturnPath, { replace: true });
        } catch (error) {
            console.error("Failed to delete report", error);
            setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        }
    };

    const reportHeader = report.description?.trim()
        ? report.description.trim()
        : t("report.no.additional.details");

    const wrapAction = (action: () => void | Promise<void>): (() => void) => () => {
        setActionsOpen(false);
        void action();
    };

    const actionItems: ActionMenuItem[] = [];

    if (report.status === "PENDING") {
        actionItems.push({
            id: "start-review",
            label: t("report.action.review"),
            variant: "info",
            onSelect: wrapAction(() => handleStatusChange("UNDER_REVIEW")),
        });
    }

    if (report.status === "UNDER_REVIEW") {
        if (report.journey && !report.journey.deleted) {
            actionItems.push({
                id: "delete-journey",
                label: t("report.action.delete_journey"),
                variant: "danger",
                onSelect: wrapAction(() => handleDeleteContent("journey")),
            });
        }
        if (report.event && !report.event.deleted) {
            actionItems.push({
                id: "delete-event",
                label: t("report.action.delete_event"),
                variant: "danger",
                onSelect: wrapAction(() => handleDeleteContent("event")),
            });
        }
        if (report.journeyResponse && !report.journeyResponse.deleted) {
            actionItems.push({
                id: "delete-journey-comment",
                label: t("report.action.delete_journey_comment"),
                variant: "danger",
                onSelect: wrapAction(() => handleDeleteContent("journey-comment")),
            });
        }
        if (report.eventResponse && !report.eventResponse.deleted) {
            actionItems.push({
                id: "delete-event-comment",
                label: t("report.action.delete_event_comment"),
                variant: "danger",
                onSelect: wrapAction(() => handleDeleteContent("event-comment")),
            });
        }

        actionItems.push({
            id: "resolve",
            label: t("report.action.resolve"),
            variant: "success",
            onSelect: wrapAction(() => handleStatusChange("RESOLVED")),
        });

        if (canDismiss) {
            actionItems.push({
                id: "dismiss",
                label: t("report.action.dismiss"),
                variant: "warning",
                onSelect: wrapAction(() => handleStatusChange("DISMISSED")),
            });
        }
    }

    if (report.status === "UNDER_REVIEW" || report.status === "RESOLVED") {
        actionItems.push({
            id: "toggle-block",
            label: report.reportedUser.blocked ? t("user.unblock") : t("user.block"),
            variant: report.reportedUser.blocked ? "info" : "danger",
            onSelect: wrapAction(() => setBlockModalOpen(true)),
        });
    }

    actionItems.push({
        id: "delete-report",
        label: t("report.detail.delete"),
        variant: "danger",
        onSelect: wrapAction(() => setDeleteModalOpen(true)),
    });

    const hasActions = actionItems.length > 0;

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
                                                    <Link
                                                        to={`/journeys/${report.journey.id}`}
                                                        state={{ from: listReturnPath }}
                                                        className="view-content-link"
                                                    >
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
                                                    <Link
                                                        to={`/events/${report.event.id}`}
                                                        state={{ from: listReturnPath }}
                                                        className="view-content-link"
                                                    >
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
                                                    {report.journeyResponse.deleted || report.journeyResponse.journey.deleted ? (
                                                        <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                    ) : report.journeyResponse.journey.id > 0 ? (
                                                        <Link
                                                            to={`/journeys/${report.journeyResponse.journey.id}`}
                                                            state={{ from: listReturnPath }}
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
                                                    {report.eventResponse.deleted || report.eventResponse.event.deleted ? (
                                                        <span className="deleted-badge">{t("report.content.deleted")}</span>
                                                    ) : report.eventResponse.event.id > 0 ? (
                                                        <Link
                                                            to={`/events/${report.eventResponse.event.id}`}
                                                            state={{ from: listReturnPath }}
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

                                    {report.contentDeleted && (
                                        <div className="feature-card reported-content-card">
                                            <h3 className="feature-title">{deletedContentTitle}</h3>
                                            <div className="reported-content">
                                                <span className="deleted-badge">{t("report.content.deleted")}</span>
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
                                <Link to={listReturnPath} className="btn-text">
                                    {t("report.back")}
                                </Link>

                                <div className="admin-actions">
                                    {actionError && <span className="warning-text">{actionError}</span>}
                                    <button
                                        type="button"
                                        ref={actionsButtonRef}
                                        className="admin-actions__trigger"
                                        onClick={() => {
                                            if (!hasActions) {
                                                setActionsOpen(false);
                                                return;
                                            }
                                            setActionsOpen((prev) => !prev);
                                        }}
                                        aria-haspopup="menu"
                                        aria-expanded={actionsOpen && hasActions}
                                        disabled={!hasActions}
                                    >
                                        {t("report.actions.manage")}
                                        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                                            <path d="M6 9l6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                                        </svg>
                                    </button>
                                    <ActionMenu
                                        open={actionsOpen}
                                        anchorRef={actionsButtonRef}
                                        onClose={() => setActionsOpen(false)}
                                        title={t("report.actions.menu")}
                                        items={actionItems}
                                    />
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
                                aria-label={t("common.close")}
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

            {deleteModalOpen && (
                <div className="modal" role="presentation" onClick={() => setDeleteModalOpen(false)}>
                    <div
                        className="modal-content"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="deleteReportModalTitle"
                        aria-describedby="deleteReportModalMessage"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <div className="modal-header">
                            <h2 id="deleteReportModalTitle">{t("report.delete.confirm.title")}</h2>
                            <button
                                type="button"
                                className="close-modal"
                                aria-label={t("common.close")}
                                onClick={() => setDeleteModalOpen(false)}
                            >
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p id="deleteReportModalMessage">{t("report.delete.confirm.message")}</p>
                            <p className="warning-text">{t("report.delete.confirm.warning")}</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setDeleteModalOpen(false)}>
                                {t("report.delete.cancel")}
                            </button>
                            <button type="button" className="cta-button btn-danger" onClick={handleDeleteReport}>
                                {t("report.delete.confirm")}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

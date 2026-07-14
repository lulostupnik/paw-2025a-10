import { apiErrorMessage } from "@/lib/api/client";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import { deleteEvent } from "@/lib/api/events";
import { invalidateEventDetailQueries, invalidateEventListQueries } from "@/lib/api/queryInvalidation";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";
import { getUserId, isAdmin } from "@/lib/auth/auth";
import { parseApiDate } from "@/lib/utils/date";

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

export default function EventDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { showToast } = useToast();
    const queryClient = useQueryClient();
    const { id } = useParams();
    const { data, isLoading, isError } = useEventDetailData({ eventId: id });
    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const isOwner = data?.user?.id === getUserId();
    const admin = isAdmin();

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous) {
            navigate(previous);
            return;
        }
        if (id) {
            navigate(`/events/${id}`);
            return;
        }
        navigate("/events");
    };

    const handleSubmit = async () => {
        if (!id) {
            setSubmitError(t("event.edit.error", { defaultValue: "Missing event id." }));
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await deleteEvent(Number(id), message.trim() ? { message: message.trim() } : undefined);
            await Promise.all([
                invalidateEventDetailQueries(queryClient, id),
                invalidateEventListQueries(queryClient),
            ]);
            showToast(t("event.toast.deleted"), { variant: "success" });
            navigate("/events");
        } catch (err) {
            console.error("Failed to delete event", err);
            setSubmitError(apiErrorMessage(err, t("event.deleteWarning", { defaultValue: "Error al eliminar el evento." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading) {
        return <PageStatus className="event-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="event-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data) {
        return <PageStatus className="event-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    // El organizador y los administradores son los únicos que pueden dar de baja
    // el evento; el resto no debe llegar a ver la confirmación.
    if (!isOwner && !admin) {
        return <ForbiddenPage />;
    }

    return (
        <div className="event-detail-page event-delete-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-navigation">
                            <button type="button" onClick={handleBack} className="back-link">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{t("event.back", { defaultValue: "Back" })}</span>
                            </button>
                        </div>

                        <div className="content-card">
                            <div className="card-header">
                                <h1 className="card-title">{t("event.delete")}</h1>
                            </div>
                            <div className="card-content">
                                <div className="journey-summary">
                                    <h3>{t("event.delete.summary")}</h3>
                                    <ul className="summary-list">
                                        <li>
                                            <strong>{t("event.name")}:</strong> {data.title}
                                        </li>
                                        <li>
                                            <strong>{t("event.location")}:</strong> {data.city?.name ?? "—"}
                                        </li>
                                        <li>
                                            <strong>{t("event.date")}:</strong> {formatDate(data.date, locale)}
                                        </li>
                                        {data.time && (
                                            <li>
                                                <strong>{t("event.time")}:</strong> {data.time}
                                            </li>
                                        )}
                                        <li>
                                            <strong>{t("event.attendees")}:</strong> {data.attendeesCount}
                                        </li>
                                    </ul>
                                </div>

                                <div className="warning-message">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                        <line x1="12" y1="9" x2="12" y2="13"></line>
                                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                                    </svg>
                                    <p>{t("event.deleteWarning")}</p>
                                </div>

                                {!isOwner && (
                                    <div className="form-group">
                                        <label className="form-label" htmlFor="event-delete-message">
                                            {t("delete.reason.label")}
                                        </label>
                                        <textarea
                                            id="event-delete-message"
                                            className="form-textarea"
                                            placeholder={t("delete.reason.placeholder")}
                                            value={message}
                                            onChange={(event) => setMessage(event.target.value)}
                                            rows={4}
                                        />
                                    </div>
                                )}

                                {submitError && <p className="error-message">{submitError}</p>}

                                <div className="form-actions">
                                    <button type="button" className="btn-secondary" onClick={handleBack} disabled={submitting}>
                                        {t("event.cancel")}
                                    </button>
                                    <button type="button" className="btn-danger" onClick={handleSubmit} disabled={submitting}>
                                        {t("event.delete")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

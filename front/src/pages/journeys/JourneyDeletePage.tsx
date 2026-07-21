import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { deleteJourney } from "@/lib/api/journeys";
import { invalidateJourneyDetailQueries, invalidateJourneyListQueries } from "@/lib/api/queryInvalidation";
import { getUserId, isAdmin } from "@/lib/auth/auth";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";
import { parseApiDate } from "@/lib/utils/date";
const DELETE_MESSAGE_MAX_LENGTH = 1000;

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

export default function JourneyDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { showToast } = useToast();
    const queryClient = useQueryClient();
    const { id } = useParams();
    const { data, isLoading, isError, isNotFound } = useJourneyDetailData({ journeyId: id });
    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const isOwner = useMemo(() => data?.user?.id === getUserId(), [data?.user?.id]);
    const admin = isAdmin();

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous) {
            navigate(previous);
            return;
        }
        if (id) {
            navigate(`/journeys/${id}`);
            return;
        }
        navigate("/journeys");
    };

    const handleSubmit = async () => {
        if (!id) {
            setSubmitError(t("journey.edit.error", { defaultValue: "No journey id provided." }));
            return;
        }
        if (message.trim().length > DELETE_MESSAGE_MAX_LENGTH) {
            document.getElementById("journey-delete-message")?.focus();
            document.getElementById("journey-delete-message")?.scrollIntoView({ block: "center", behavior: "smooth" });
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await deleteJourney(id, message.trim() ? { message: message.trim() } : undefined);
            await Promise.all([
                invalidateJourneyDetailQueries(queryClient, id),
                invalidateJourneyListQueries(queryClient),
            ]);
            showToast(t("journey.toast.deleted"), { variant: "success" });
            navigate("/journeys");
        } catch (err) {
            console.error("Failed to delete journey", err);
            setSubmitError(apiErrorMessage(err, t("journey.deleteWarning", { defaultValue: "Error al eliminar el viaje." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading) {
        return <PageStatus className="journey-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isNotFound) {
        return (
            <div className="journey-detail-page">
                <div className="empty-state">
                    <p className="empty-message">{t("journey.not.found.title", { defaultValue: "Journey not found." })}</p>
                    <p className="empty-message">{t("journey.not.found.message")}</p>
                </div>
            </div>
        );
    }

    if (isError) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (!isOwner && !admin) {
        return <ForbiddenPage />;
    }

    return (
        <div className="journey-detail-page journey-delete-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-navigation">
                            <button type="button" onClick={handleBack} className="back-link">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{t("journey.edit.back")}</span>
                            </button>
                        </div>

                        <div className="content-card">
                            <div className="card-header">
                                <h1 className="card-title">{t("journey.delete")}</h1>
                            </div>
                            <div className="card-content">
                                <div className="journey-summary">
                                    <h3>{t("journey.delete.summary")}</h3>
                                    <ul className="summary-list">
                                        <li>
                                            <strong>{t("journey.destination")}:</strong> {data.destinationUniversity?.city ?? "—"} -{" "}
                                            {data.destinationUniversity?.name ?? "—"}
                                        </li>
                                        <li>
                                            <strong>{t("journey.dates")}:</strong> {formatDate(data.startDate, locale)} → {formatDate(data.endDate, locale)}
                                        </li>
                                    </ul>
                                </div>

                                <div className="warning-message">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                        <line x1="12" y1="9" x2="12" y2="13"></line>
                                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                                    </svg>
                                    <p>{t("journey.deleteWarning")}</p>
                                </div>

                                {!isOwner && (
                                    <div className="form-group">
                                        <label className="form-label" htmlFor="journey-delete-message">
                                            {t("delete.reason.label")}
                                        </label>
                                        <textarea
                                            id="journey-delete-message"
                                            className="form-textarea"
                                            placeholder={t("delete.reason.placeholder")}
                                            value={message}
                                            onChange={(event) => setMessage(event.target.value)}
                                            rows={4}
                                        />
                                        <p className={`character-counter ${message.trim().length > DELETE_MESSAGE_MAX_LENGTH ? "is-error" : ""}`}>
                                            {message.trim().length}/{DELETE_MESSAGE_MAX_LENGTH}
                                        </p>
                                    </div>
                                )}

                                {submitError && <p className="error-message">{submitError}</p>}

                                <div className="form-actions">
                                    <button type="button" className="btn-secondary" onClick={handleBack} disabled={submitting}>
                                        {t("event.cancel")}
                                    </button>
                                    <button type="button" className="btn-danger" onClick={handleSubmit} disabled={submitting}>
                                        {t("journey.delete")}
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

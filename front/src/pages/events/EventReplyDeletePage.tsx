import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import { deleteEventResponse, getEventResponse } from "@/lib/api/events";
import { useBackNavigation } from "@/lib/navigation";
import { parseApiDate } from "@/lib/utils/date";
import { getUserId, isAdmin } from "@/lib/auth/auth";
import NotFoundPage from "@/pages/errors/NotFoundPage";

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/(\d+)\/?$/);
    return match ? Number(match[1]) : null;
};

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

export default function EventReplyDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { showToast } = useToast();
    const { goBack } = useBackNavigation();
    const { responseId } = useParams();
    const [searchParams] = useSearchParams();
    const eventId = searchParams.get("eventId");
    const { data, isLoading, isError } = useEventDetailData({ eventId: eventId ?? undefined });
    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const parsedEventId = eventId && Number.isFinite(Number(eventId)) ? Number(eventId) : null;
    const parsedResponseId = responseId && Number.isFinite(Number(responseId)) ? Number(responseId) : null;

    const response = useMemo(() => {
        if (parsedResponseId == null) {
            return null;
        }
        return data?.comments?.find((comment) => comment.id === parsedResponseId) ?? null;
    }, [data?.comments, parsedResponseId]);

    const responseQuery = useQuery({
        queryKey: ["eventResponse", parsedEventId, parsedResponseId],
        queryFn: ({ signal }) => getEventResponse(parsedEventId!, parsedResponseId!, signal),
        enabled: parsedEventId != null && parsedResponseId != null,
    });

    const handleBack = () => {
        goBack(eventId ? `/events/${eventId}` : "/events");
    };

    const handleSubmit = async () => {
        if (!eventId || !responseId) {
            setSubmitError(t("event.edit.error", { defaultValue: "Missing identifiers." }));
            return;
        }
        const parsedResponseId = Number(responseId);
        if (!Number.isFinite(parsedResponseId)) {
            setSubmitError(t("event.edit.error", { defaultValue: "Invalid response id." }));
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await deleteEventResponse(
                Number(eventId),
                parsedResponseId,
                message.trim() ? { message: message.trim() } : undefined
            );
            showToast(t("eventResponse.toast.deleted"), { variant: "success" });
            navigate(`/events/${eventId}`);
        } catch (err) {
            console.error("Failed to delete event response", err);
            setSubmitError(apiErrorMessage(err, t("eventResponse.deleteWarning", { defaultValue: "Error al eliminar el comentario." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading || responseQuery.isLoading) {
        return <PageStatus className="event-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !data || !response) {
        return <NotFoundPage/>
    }

    const authorId = parseIdFromUrl(responseQuery.data?.links?.authorUrl);
    if (!isAdmin() && (authorId == null || authorId !== getUserId())) {
        return <ForbiddenPage />;
    }

    return (
        <div className="event-detail-page event-reply-delete-page">
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
                                <h1 className="card-title">{t("eventResponse.delete")}</h1>
                            </div>
                            <div className="card-content">
                                <div className="journey-summary">
                                    <h3>{t("eventResponse.delete.summary")}</h3>
                                    {response ? (
                                        <div className="reply-content-preview">
                                            <p>
                                                <strong>{t("eventResponse.author")}:</strong> {response.user.username}
                                            </p>
                                            <p>
                                                <strong>{t("eventResponse.date")}:</strong> {formatDateTime(response.dateTime, locale)}
                                            </p>
                                            <p>
                                                <strong>{t("eventResponse.content")}:</strong>
                                            </p>
                                            <div className="message-preview">{response.message}</div>
                                        </div>
                                    ) : (
                                        <p className="empty-message">{t("event.no.responses")}</p>
                                    )}
                                </div>

                                <div className="warning-message">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                        <line x1="12" y1="9" x2="12" y2="13"></line>
                                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                                    </svg>
                                    <p>{t("eventResponse.deleteWarning")}</p>
                                </div>

                                {isAdmin() && (
                                <div className="form-group">
                                    <label className="form-label" htmlFor="event-reply-delete-message">
                                        {t("delete.reason.label")}
                                    </label>
                                    <textarea
                                        id="event-reply-delete-message"
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
                                        {t("eventResponse.confirmDelete")}
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

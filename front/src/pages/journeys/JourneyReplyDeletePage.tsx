import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { deleteJourneyResponse, getJourneyResponse, getJourneyResponses, getUserByUrl } from "@/lib/api/journeys";
import { useBackNavigation } from "@/lib/navigation";
import { parseApiDate } from "@/lib/utils/date";
import { getUserId, isAdmin } from "@/lib/auth/auth";
import NotFoundPage from "@/pages/errors/NotFoundPage";

const COMMENTS_PAGE_SIZE = 4;

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/(\d+)\/?$/);
    return match ? Number(match[1]) : null;
};

const parsePageParam = (value: string | null) => {
    const parsed = Number(value ?? "1");
    return Number.isFinite(parsed) && parsed > 0 ? parsed : 1;
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

export default function JourneyReplyDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const { goBack } = useBackNavigation();
    const { responseId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const commentsPage = parsePageParam(searchParams.get("commentsPage"));
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });
    const [message, setMessage] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const parsedResponseId = useMemo(() => {
        if (!responseId) {
            return null;
        }
        const parsed = Number(responseId);
        return Number.isFinite(parsed) ? parsed : null;
    }, [responseId]);

    const parsedJourneyId = useMemo(() => {
        if (!journeyId) {
            return null;
        }
        const parsed = Number(journeyId);
        return Number.isFinite(parsed) ? parsed : null;
    }, [journeyId]);

    const fallbackResponse = useMemo(() => {
        if (parsedResponseId == null) {
            return null;
        }
        return data?.comments?.find((comment) => comment.id === parsedResponseId) ?? null;
    }, [data?.comments, parsedResponseId]);

    const responseQuery = useQuery({
        queryKey: ["journeyResponse", parsedJourneyId, parsedResponseId],
        queryFn: async ({ signal }) => {
            if (parsedJourneyId == null || parsedResponseId == null) {
                throw new Error("missing-identifiers");
            }
            const response = await getJourneyResponse(parsedJourneyId, parsedResponseId, signal);
            const user = response.links?.authorUrl ? await getUserByUrl(response.links.authorUrl, signal, queryClient) : null;
            return {
                id: response.id,
                message: response.message,
                dateTime: response.dateTime,
                authorId: parseIdFromUrl(response.links?.authorUrl),
                user: {
                    username: user?.username ?? fallbackResponse?.user.username ?? "—",
                },
            };
        },
        enabled: parsedJourneyId != null && parsedResponseId != null,
    });

    const response = responseQuery.data ?? fallbackResponse;

    const clearJourneyResponseData = async (id: string | number) => {
        await queryClient.cancelQueries({
            predicate: (query) =>
                (query.queryKey[0] === "journeyComments" ||
                    query.queryKey[0] === "journeyDetail" ||
                    query.queryKey[0] === "journeyResponse") &&
                String(query.queryKey[1]) === String(id),
        });
        queryClient.removeQueries({
            predicate: (query) => query.queryKey[0] === "journeyComments" && String(query.queryKey[1]) === String(id),
        });
        queryClient.removeQueries({
            predicate: (query) => query.queryKey[0] === "journeyResponse" && String(query.queryKey[1]) === String(id),
        });
        await queryClient.invalidateQueries({
            predicate: (query) => query.queryKey[0] === "journeyDetail" && String(query.queryKey[1]) === String(id),
        });
    };

    const handleBack = () => {
        const fallback = journeyId
            ? commentsPage > 1
                ? `/journeys/${journeyId}?commentsPage=${commentsPage}`
                : `/journeys/${journeyId}`
            : "/journeys";
        goBack(fallback);
    };

    const handleSubmit = async () => {
        if (parsedJourneyId == null || parsedResponseId == null) {
            setSubmitError(t("journey.edit.error", { defaultValue: "Missing identifiers." }));
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await deleteJourneyResponse(
                parsedJourneyId,
                parsedResponseId,
                message.trim() ? { message: message.trim() } : undefined
            );
            showToast(t("journeyResponse.toast.deleted"), { variant: "success" });
            await clearJourneyResponseData(parsedJourneyId);

            let destinationPage = commentsPage;
            try {
                const refreshedResponses = await getJourneyResponses(parsedJourneyId, {
                    page: 1,
                    size: COMMENTS_PAGE_SIZE,
                });
                const lastPage = Math.max(1, refreshedResponses.totalPages || 1);
                destinationPage = Math.min(destinationPage, lastPage);
            } catch (responsesError) {
                console.warn("Failed to resolve destination page after journey reply delete", responsesError);
            }

            navigate(
                destinationPage > 1
                    ? `/journeys/${parsedJourneyId}?commentsPage=${destinationPage}`
                    : `/journeys/${parsedJourneyId}`
            );
        } catch (err) {
            console.error("Failed to delete journey response", err);
            setSubmitError(apiErrorMessage(err, t("journeyResponse.deleteWarning", { defaultValue: "Error al eliminar el comentario." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading || responseQuery.isLoading) {
        return <PageStatus className="journey-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data || !response) {
        return <NotFoundPage/>
    }

    const authorId = responseQuery.data?.authorId ?? null;
    if (!isAdmin() && (authorId == null || authorId !== getUserId())) {
        return <ForbiddenPage />;
    }

    return (
        <div className="journey-detail-page journey-reply-delete-page">
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
                                <h1 className="card-title">{t("journeyResponse.delete")}</h1>
                            </div>
                            <div className="card-content">
                                <div className="reply-summary">
                                    <h3>{t("journeyResponse.delete.summary")}</h3>
                                    {response ? (
                                        <div className="reply-content-preview">
                                            <p>
                                                <strong>{t("journeyResponse.author")}:</strong> {response.user.username}
                                            </p>
                                            <p>
                                                <strong>{t("journeyResponse.date")}:</strong> {formatDateTime(response.dateTime, locale)}
                                            </p>
                                            <p>
                                                <strong>{t("journeyResponse.content")}:</strong>
                                            </p>
                                            <div className="message-preview">{response.message}</div>
                                        </div>
                                    ) : (
                                        <p className="empty-message">{t("journey.detail.no.responses")}</p>
                                    )}
                                </div>

                                <div className="warning-message">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                        <line x1="12" y1="9" x2="12" y2="13"></line>
                                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                                    </svg>
                                    <p>{t("journeyResponse.deleteWarning")}</p>
                                </div>

                                {isAdmin() && (
                                <div className="form-group">
                                    <label className="form-label" htmlFor="journey-reply-delete-message">
                                        {t("delete.reason.label")}
                                    </label>
                                    <textarea
                                        id="journey-reply-delete-message"
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
                                        {t("journeyResponse.confirmDelete")}
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

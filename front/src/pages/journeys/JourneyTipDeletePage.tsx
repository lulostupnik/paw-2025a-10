import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { deleteJourneyTip, getJourneyTip, listJourneyTips } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";
import { parseApiDate } from "@/lib/utils/date";
import { getUserId, isAdmin } from "@/lib/auth/auth";

const TIPS_PAGE_SIZE = 4;

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

export default function JourneyTipDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const { tipId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const tipsPage = searchParams.get("tipsPage");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const clearJourneyTipData = async (id: string | number) => {
        await queryClient.cancelQueries({
            predicate: (query) =>
                (query.queryKey[0] === "journeyTips" || query.queryKey[0] === "journeyTip") &&
                String(query.queryKey[1]) === String(id),
        });
        queryClient.removeQueries({
            predicate: (query) => query.queryKey[0] === "journeyTips" && String(query.queryKey[1]) === String(id),
        });
        queryClient.removeQueries({
            predicate: (query) => query.queryKey[0] === "journeyTip" && String(query.queryKey[1]) === String(id),
        });
        await queryClient.invalidateQueries({
            predicate: (query) => query.queryKey[0] === "journeyDetail" && String(query.queryKey[1]) === String(id),
        });
    };

    const parsedTipId = useMemo(() => {
        if (!tipId) {
            return null;
        }
        const parsed = Number(tipId);
        return Number.isFinite(parsed) ? parsed : null;
    }, [tipId]);

    const parsedJourneyId = useMemo(() => {
        if (!journeyId) {
            return null;
        }
        const parsed = Number(journeyId);
        return Number.isFinite(parsed) ? parsed : null;
    }, [journeyId]);

    const fallbackTip = useMemo(() => {
        if (parsedTipId == null) {
            return null;
        }
        return data?.tips?.find((item) => item.id === parsedTipId) ?? null;
    }, [data?.tips, parsedTipId]);

    const tipQuery = useQuery({
        queryKey: ["journeyTip", parsedJourneyId, parsedTipId],
        queryFn: ({ signal }) => {
            if (parsedJourneyId == null || parsedTipId == null) {
                throw new Error("missing-identifiers");
            }
            return getJourneyTip(parsedJourneyId, parsedTipId, signal);
        },
        enabled: parsedJourneyId != null && parsedTipId != null,
    });

    const tip = tipQuery.data ?? fallbackTip;

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous) {
            navigate(previous);
            return;
        }
        if (journeyId) {
            navigate(tipsPage ? `/journeys/${journeyId}?tipsPage=${tipsPage}` : `/journeys/${journeyId}`);
            return;
        }
        navigate("/journeys");
    };

    const handleDelete = async () => {
        if (!journeyId || !tipId) {
            setSubmitError(t("journey.edit.error", { defaultValue: "Missing identifiers." }));
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await deleteJourneyTip(Number(journeyId), Number(tipId));
            showToast(t("tip.toast.deleted"), { variant: "success" });
            await clearJourneyTipData(journeyId);

            let destinationPage = Number(tipsPage ?? "1");
            if (!Number.isFinite(destinationPage) || destinationPage < 1) {
                destinationPage = 1;
            }
            try {
                const refreshedTips = await listJourneyTips(Number(journeyId), { page: 1, size: TIPS_PAGE_SIZE });
                const lastPage = Math.max(1, refreshedTips.totalPages || 1);
                destinationPage = Math.min(destinationPage, lastPage);
            } catch (tipListError) {
                console.warn("Failed to resolve destination page after tip delete", tipListError);
            }

            navigate(destinationPage > 1 ? `/journeys/${journeyId}?tipsPage=${destinationPage}` : `/journeys/${journeyId}`);
        } catch (err) {
            console.error("Failed to delete tip", err);
            setSubmitError(apiErrorMessage(err, t("tip.deleteWarning", { defaultValue: "No se pudo eliminar el consejo." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading) {
        return <PageStatus className="journey-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (data.user?.id !== getUserId() && !isAdmin()) {
        return <ForbiddenPage />;
    }

    return (
        <div className="journey-detail-page journey-tip-delete-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-button-container">
                            <button type="button" onClick={handleBack} className="back-link">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{t("journey.tip.back.to.journey")}</span>
                            </button>
                        </div>

                        <div className="content-card">
                            <div className="card-header">
                                <h1 className="card-title">{t("journey.tip.delete.title")}</h1>
                            </div>
                            <div className="card-content">
                                <div className="journey-summary">
                                    <h3>{t("journey.tip.preview.title")}</h3>
                                    {tip ? (
                                        <div className="reply-content-preview">
                                            <p>
                                                <strong>{t("tip.title")}:</strong> {tip.title}
                                            </p>
                                            <p>
                                                <strong>{t("tip.created.on")}:</strong> {formatDate(tip.dateTime, locale)}
                                            </p>
                                            <div className="message-preview">{tip.content}</div>
                                        </div>
                                    ) : (
                                        <p className="empty-message">{t("journey.detail.no.tips")}</p>
                                    )}
                                </div>

                                <div className="warning-message">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                        <line x1="12" y1="9" x2="12" y2="13"></line>
                                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                                    </svg>
                                    <p>{t("tip.deleteWarning")}</p>
                                </div>

                                {submitError && <p className="error-message">{submitError}</p>}

                                <div className="form-actions">
                                    <button type="button" className="btn-secondary" onClick={handleBack} disabled={submitting}>
                                        {t("tip.confirmDelete.reject")}
                                    </button>
                                    <button type="button" className="btn-danger" onClick={handleDelete} disabled={submitting}>
                                        {t("tip.delete.confirm")}
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

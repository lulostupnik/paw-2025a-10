import { useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import { deleteJourneyTip } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";

const formatDate = (value: string, locale: string) => {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

export default function JourneyTipDeletePage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { tipId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const tip = useMemo(() => {
        if (!tipId) {
            return null;
        }
        const parsed = Number(tipId);
        if (!Number.isFinite(parsed)) {
            return null;
        }
        return data.tips.find((item) => item.id === parsed) ?? null;
    }, [data.tips, tipId]);

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous) {
            navigate(previous);
            return;
        }
        if (journeyId) {
            navigate(`/journeys/${journeyId}`);
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
            navigate(`/journeys/${journeyId}`);
        } catch (err) {
            console.error("Failed to delete tip", err);
            setSubmitError(t("tip.deleteWarning", { defaultValue: "No se pudo eliminar el consejo." }));
        } finally {
            setSubmitting(false);
        }
    };

    if (isLoading) {
        return <div className="journey-detail-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="journey-detail-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
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

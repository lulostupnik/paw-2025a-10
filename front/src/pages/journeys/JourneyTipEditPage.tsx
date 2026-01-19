import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import { updateJourneyTip } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";

export default function JourneyTipEditPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { tipId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [seeded, setSeeded] = useState(false);

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

    useEffect(() => {
        if (!tip || seeded) {
            return;
        }
        setTitle(tip.title);
        setContent(tip.content);
        setSeeded(true);
    }, [seeded, tip]);

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

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!journeyId || !tipId) {
            setSubmitError(t("journey.edit.error", { defaultValue: "Missing identifiers." }));
            return;
        }
        if (!title.trim() || !content.trim()) {
            setSubmitError(t("NotEmpty.createTipForm.content", { defaultValue: "Completa todos los campos." }));
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await updateJourneyTip(Number(journeyId), Number(tipId), {
                title: title.trim(),
                content: content.trim(),
            });
            navigate(`/journeys/${journeyId}`);
        } catch (err) {
            console.error("Failed to update tip", err);
            setSubmitError(t("journey.edit.error", { defaultValue: "No se pudo actualizar el consejo." }));
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
        <div className="journey-detail-page journey-tip-form-page">
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

                        <div className="event-detail-container">
                            <div className="event-top-section">
                                <div className="event-header">
                                    <h1 className="event-title">{t("journey.tip.update.title")}</h1>
                                </div>

                                <div className="journey-info-card">
                                    <div className="journey-info-header">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <line x1="12" y1="16" x2="12" y2="12"></line>
                                            <line x1="12" y1="8" x2="12.01" y2="8"></line>
                                        </svg>
                                        <span>{t("journey.tip.updating.for.journey")}</span>
                                    </div>
                                    <div className="journey-info-content">
                                        <h3 className="journey-info-title">
                                            {t("journey.detail.section.title", {
                                                values: { 0: data.user?.firstname ?? "—", 1: data.user?.lastname ?? "" },
                                            })}
                                        </h3>
                                        <div className="journey-info-meta">
                                            <div className="meta-item">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                    <circle cx="12" cy="10" r="3"></circle>
                                                </svg>
                                                <span className="destination-text">
                                                    {data.destinationUniversity?.city ?? "—"} - {data.destinationUniversity?.name ?? "—"}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {tip && (
                                    <div className="tip-info-card">
                                        <div className="tip-info-header">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                <circle cx="12" cy="12" r="3"></circle>
                                            </svg>
                                            <span>{t("journey.tip.editing")}</span>
                                        </div>
                                        <div className="tip-info-content">
                                            <h4 className="tip-info-title">{tip.title}</h4>
                                        </div>
                                    </div>
                                )}

                                <div className="tip-form-container">
                                    <h3 className="tip-form-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                        </svg>
                                        {t("journey.tip.update.form.title")}
                                    </h3>

                                    <form className="tip-form" onSubmit={handleSubmit} noValidate>
                                        <div className="form-group">
                                            <label className="form-label required-field" htmlFor="tip-title">
                                                {t("tip.title")}
                                            </label>
                                            <input
                                                id="tip-title"
                                                className="form-input"
                                                placeholder={t("tip.title.hint")}
                                                value={title}
                                                onChange={(event) => setTitle(event.target.value)}
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label className="form-label required-field" htmlFor="tip-content">
                                                {t("tip.content")}
                                            </label>
                                            <textarea
                                                id="tip-content"
                                                className="form-textarea"
                                                placeholder={t("tip.content.hint")}
                                                value={content}
                                                onChange={(event) => setContent(event.target.value)}
                                                rows={6}
                                            />
                                        </div>

                                        {submitError && <p className="error-message">{submitError}</p>}

                                        <div className="form-actions">
                                            <button type="button" className="btn-secondary" onClick={handleBack} disabled={submitting}>
                                                {t("tip.cancel")}
                                            </button>
                                            <button type="submit" className="btn btn-primary" disabled={submitting}>
                                                {t("journey.tip.update.title")}
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

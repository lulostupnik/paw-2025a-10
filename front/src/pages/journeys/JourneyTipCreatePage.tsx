import { apiErrorMessage } from "@/lib/api/client";
import { useState, type FormEvent } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { createJourneyTip, listJourneyTips } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";

const TIPS_PAGE_SIZE = 4;

export default function JourneyTipCreatePage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const { journeyId } = useParams();
    const [searchParams] = useSearchParams();
    const tipsPage = searchParams.get("tipsPage");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId });
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [touched, setTouched] = useState({ title: false, content: false });
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const invalidateJourneyTipData = async (id: string | number) => {
        await Promise.all([
            queryClient.invalidateQueries({
                predicate: (query) => query.queryKey[0] === "journeyTips" && String(query.queryKey[1]) === String(id),
            }),
            queryClient.invalidateQueries({
                predicate: (query) => query.queryKey[0] === "journeyDetail" && String(query.queryKey[1]) === String(id),
            }),
        ]);
    };

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

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ title: true, content: true });
        if (!journeyId) {
            setSubmitError(t("journey.edit.error", { defaultValue: "Missing journey id." }));
            return;
        }
        if (!title.trim() || !content.trim()) {
            setSubmitError(null);
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await createJourneyTip(Number(journeyId), { title: title.trim(), content: content.trim() });
            await invalidateJourneyTipData(journeyId);
            let destination = `/journeys/${journeyId}`;
            try {
                const tips = await listJourneyTips(Number(journeyId), { page: 1, size: TIPS_PAGE_SIZE });
                const lastPage = Math.max(1, tips.totalPages);
                destination = lastPage > 1 ? `/journeys/${journeyId}?tipsPage=${lastPage}` : `/journeys/${journeyId}`;
            } catch (tipListError) {
                console.warn("Failed to resolve last tips page after tip creation", tipListError);
            }
            showToast(t("tip.toast.created"), { variant: "success" });
            navigate(destination);
        } catch (err) {
            console.error("Failed to create tip", err);
            setSubmitError(apiErrorMessage(err, t("journey.edit.error", { defaultValue: "No se pudo crear el consejo." })));
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

    const titleError = touched.title && !title.trim()
        ? t("NotEmpty.createTipForm.title")
        : "";
    const contentError = touched.content && !content.trim()
        ? t("NotEmpty.createTipForm.content")
        : "";

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
                                    <h1 className="event-title">{t("journey.tip.add")}</h1>
                                </div>

                                <div className="journey-info-card">
                                    <div className="journey-info-header">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <line x1="12" y1="16" x2="12" y2="12"></line>
                                            <line x1="12" y1="8" x2="12.01" y2="8"></line>
                                        </svg>
                                        <span>{t("journey.tip.for.journey")}</span>
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

                                <div className="tip-form-container">
                                    <h3 className="tip-form-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                        </svg>
                                        {t("journey.tip.form.title")}
                                    </h3>
                                    <p className="journey-create-meta">{t("form.requiredHint")}</p>

                                    <form className="tip-form" onSubmit={handleSubmit} noValidate>
                                        <div className="form-group">
                                            <label className="form-label required-field" htmlFor="tip-title">
                                                {t("tip.title")}
                                            </label>
                                            <input
                                                id="tip-title"
                                                className={titleError ? "form-input error" : "form-input"}
                                                placeholder={t("tip.title.hint")}
                                                value={title}
                                                onChange={(event) => {
                                                    setTitle(event.target.value);
                                                    setSubmitError(null);
                                                }}
                                                onBlur={() => setTouched((prev) => ({ ...prev, title: true }))}
                                            />
                                            {titleError && <p className="form-field__text form-field__text--error">{titleError}</p>}
                                        </div>

                                        <div className="form-group">
                                            <label className="form-label required-field" htmlFor="tip-content">
                                                {t("tip.content")}
                                            </label>
                                            <textarea
                                                id="tip-content"
                                                className={contentError ? "form-textarea error" : "form-textarea"}
                                                placeholder={t("tip.content.hint")}
                                                value={content}
                                                onChange={(event) => {
                                                    setContent(event.target.value);
                                                    setSubmitError(null);
                                                }}
                                                onBlur={() => setTouched((prev) => ({ ...prev, content: true }))}
                                                rows={6}
                                            />
                                            {contentError && <p className="form-field__text form-field__text--error">{contentError}</p>}
                                        </div>

                                        {submitError && <p className="form-field__text form-field__text--error">{submitError}</p>}

                                        <div className="form-actions">
                                            <button type="button" className="btn-secondary" onClick={handleBack} disabled={submitting}>
                                                {t("tip.cancel")}
                                            </button>
                                            <button type="submit" className="btn btn-primary" disabled={submitting}>
                                                {t("tip.submit")}
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

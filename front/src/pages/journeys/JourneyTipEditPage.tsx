import { apiErrorMessage } from "@/lib/api/client";
import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import { getJourneyTip, updateJourneyTip } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

export default function JourneyTipEditPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { tipId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const tipsPage = searchParams.get("tipsPage");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [touched, setTouched] = useState({ title: false, content: false });
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [seeded, setSeeded] = useState(false);

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
        ...DETAIL_QUERY_OPTIONS,
        enabled: parsedJourneyId != null && parsedTipId != null,
    });

    const tip = tipQuery.data ?? fallbackTip;

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
            navigate(tipsPage ? `/journeys/${journeyId}?tipsPage=${tipsPage}` : `/journeys/${journeyId}`);
            return;
        }
        navigate("/journeys");
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ title: true, content: true });
        if (!journeyId || !tipId) {
            setSubmitError(t("journey.edit.error", { defaultValue: "Missing identifiers." }));
            return;
        }
        if (!title.trim() || !content.trim()) {
            setSubmitError(null);
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await updateJourneyTip(Number(journeyId), Number(tipId), {
                title: title.trim(),
                content: content.trim(),
            });
            await invalidateJourneyTipData(journeyId);
            navigate(tipsPage ? `/journeys/${journeyId}?tipsPage=${tipsPage}` : `/journeys/${journeyId}`);
        } catch (err) {
            console.error("Failed to update tip", err);
            setSubmitError(apiErrorMessage(err, t("journey.edit.error", { defaultValue: "No se pudo actualizar el consejo." })));
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
        ? t("NotEmpty.createTipForm.title", { defaultValue: "Completa este campo." })
        : "";
    const contentError = touched.content && !content.trim()
        ? t("NotEmpty.createTipForm.content", { defaultValue: "Completa este campo." })
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

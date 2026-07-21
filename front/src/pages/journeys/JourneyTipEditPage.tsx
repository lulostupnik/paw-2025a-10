import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState, type FormEvent } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { getUserId } from "@/lib/auth/auth";
import { getJourneyTip, updateJourneyTip } from "@/lib/api/journeys";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";
import type { JourneyDetail, JourneyTip } from "@/types/journey";

const TIP_TITLE_MAX_LENGTH = 255;
const TIP_CONTENT_MAX_LENGTH = 2047;

export default function JourneyTipEditPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { tipId } = useParams();
    const [searchParams] = useSearchParams();
    const journeyId = searchParams.get("journeyId");
    const tipsPage = searchParams.get("tipsPage");
    const { data, isLoading, isError } = useJourneyDetailData({ journeyId: journeyId ?? undefined });

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

    const tip = tipQuery.data ?? null;

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

    if (isLoading || tipQuery.isLoading) {
        return <PageStatus className="journey-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (data.user?.id !== getUserId()) {
        return <ForbiddenPage />;
    }

    if (!tip || parsedJourneyId == null || parsedTipId == null) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    return (
        <JourneyTipEditForm
            key={tip.id}
            journey={data}
            tip={tip}
            journeyId={parsedJourneyId}
            tipsPage={tipsPage}
            onBack={handleBack}
        />
    );
}

interface JourneyTipEditFormProps {
    journey: JourneyDetail;
    tip: JourneyTip;
    journeyId: number;
    tipsPage: string | null;
    onBack: () => void;
}

function JourneyTipEditForm({ journey, tip, journeyId, tipsPage, onBack }: JourneyTipEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const [title, setTitle] = useState(() => tip.title);
    const [content, setContent] = useState(() => tip.content);
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

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ title: true, content: true });
        if (!title.trim() || !content.trim()) {
            setSubmitError(null);
            return;
        }
        if (title.length > TIP_TITLE_MAX_LENGTH || content.length > TIP_CONTENT_MAX_LENGTH) {
            setSubmitError(null);
            return;
        }
        try {
            setSubmitting(true);
            setSubmitError(null);
            await updateJourneyTip(journeyId, tip.id, {
                title: title.trim(),
                content: content.trim(),
            });
            await invalidateJourneyTipData(journeyId);
            showToast(t("tip.toast.updated"), { variant: "success" });
            navigate(tipsPage ? `/journeys/${journeyId}?tipsPage=${tipsPage}` : `/journeys/${journeyId}`);
        } catch (err) {
            console.error("Failed to update tip", err);
            setSubmitError(apiErrorMessage(err, t("journey.edit.error", { defaultValue: "No se pudo actualizar el consejo." })));
        } finally {
            setSubmitting(false);
        }
    };

    const titleError = touched.title && !title.trim()
        ? t("NotEmpty.createTipForm.title")
        : title.length > TIP_TITLE_MAX_LENGTH
          ? t("tip.validation.title.length")
          : "";
    const contentError = touched.content && !content.trim()
        ? t("NotEmpty.createTipForm.content")
        : content.length > TIP_CONTENT_MAX_LENGTH
          ? t("tip.validation.content.length")
          : "";

    return (
        <div className="journey-detail-page journey-tip-form-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-button-container">
                            <button type="button" onClick={onBack} className="back-link">
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
                                                values: { 0: journey.user?.firstname ?? "—", 1: journey.user?.lastname ?? "" },
                                            })}
                                        </h3>
                                        <div className="journey-info-meta">
                                            <div className="meta-item">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                    <circle cx="12" cy="10" r="3"></circle>
                                                </svg>
                                                <span className="destination-text">
                                                    {journey.destinationUniversity?.city ?? "—"} - {journey.destinationUniversity?.name ?? "—"}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

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
                                                {t("tip.title")} <span className="required-indicator" aria-hidden="true">*</span>
                                            </label>
                                            <input
                                                id="tip-title"
                                                className={titleError || title.length > TIP_TITLE_MAX_LENGTH ? "form-input error" : "form-input"}
                                                placeholder={t("tip.title.hint")}
                                                value={title}
                                                onChange={(event) => {
                                                    setTitle(event.target.value);
                                                    setSubmitError(null);
                                                }}
                                                onBlur={() => setTouched((prev) => ({ ...prev, title: true }))}
                                            />
                                            <p className={`character-counter ${title.length > TIP_TITLE_MAX_LENGTH ? "is-error" : ""}`}>
                                                {title.length}/{TIP_TITLE_MAX_LENGTH}
                                            </p>
                                            {titleError && <p className="form-field__text form-field__text--error">{titleError}</p>}
                                        </div>

                                        <div className="form-group">
                                            <label className="form-label required-field" htmlFor="tip-content">
                                                {t("tip.content")} <span className="required-indicator" aria-hidden="true">*</span>
                                            </label>
                                            <textarea
                                                id="tip-content"
                                                className={contentError || content.length > TIP_CONTENT_MAX_LENGTH ? "form-textarea error" : "form-textarea"}
                                                placeholder={t("tip.content.hint")}
                                                value={content}
                                                onChange={(event) => {
                                                    setContent(event.target.value);
                                                    setSubmitError(null);
                                                }}
                                                onBlur={() => setTouched((prev) => ({ ...prev, content: true }))}
                                                rows={6}
                                            />
                                            <p className={`character-counter ${content.length > TIP_CONTENT_MAX_LENGTH ? "is-error" : ""}`}>
                                                {content.length}/{TIP_CONTENT_MAX_LENGTH}
                                            </p>
                                            {contentError && <p className="form-field__text form-field__text--error">{contentError}</p>}
                                        </div>

                                        {submitError && <p className="form-field__text form-field__text--error">{submitError}</p>}

                                        <div className="form-actions">
                                            <button type="button" className="btn-secondary" onClick={onBack} disabled={submitting}>
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

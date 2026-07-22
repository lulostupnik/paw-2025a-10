import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useRef, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import { createReport, type ReportReason, type ReportType } from "@/lib/api/reports";
import { useI18n } from "@/lib/i18n";
import { useBackNavigation } from "@/lib/navigation";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";

const REASONS: ReportReason[] = [
    "SPAM",
    "HARASSMENT",
    "INAPPROPRIATE_CONTENT",
    "MISINFORMATION",
    "HATE_SPEECH",
    "VIOLENCE",
    "OTHER",
];

const MAX_DESCRIPTION_LENGTH = 2047;

interface ReportCreatePageProps {
    reportType: ReportType;
}

export default function ReportCreatePage({ reportType }: ReportCreatePageProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { goBack } = useBackNavigation();
    const { id } = useParams();
    const targetId = Number(id);
    const [reason, setReason] = useState<ReportReason>(REASONS[0]);
    const [description, setDescription] = useState("");
    const [touched, setTouched] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [serverError, setServerError] = useState("");
    const formRef = useRef<HTMLFormElement>(null);

    const descriptionError = useMemo(() => {
        const trimmed = description.trim();
        if (!trimmed) {
            return t("report.description.required");
        }
        if (trimmed.length > MAX_DESCRIPTION_LENGTH) {
            return t("report.description.too_long");
        }
        return "";
    }, [description, t]);

    const reasonError = useMemo(() => {
        if (!reason) {
            return t("report.reason.required");
        }
        if (!REASONS.includes(reason)) {
            return t("report.reason.invalid");
        }
        return "";
    }, [reason, t]);

    const fallback = useMemo(() => {
        if (reportType === "JOURNEY" && Number.isFinite(targetId)) {
            return `/journeys/${targetId}`;
        }
        if (reportType === "EVENT" && Number.isFinite(targetId)) {
            return `/events/${targetId}`;
        }
        return "/explore";
    }, [reportType, targetId]);

    const handleBack = () => {
        goBack(fallback);
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched(true);
        if (!Number.isFinite(targetId) || reasonError || descriptionError) {
            focusFirstInvalidField(formRef.current ?? event.currentTarget);
            return;
        }

        setSubmitting(true);
        setServerError("");

        try {
            await createReport({
                reportType,
                targetId,
                reason,
                description: description.trim(),
            });
            goBack(fallback, { replace: true });
        } catch (error) {
            console.error("Failed to submit report", error);
            setServerError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        } finally {
            setSubmitting(false);
        }
    };

    if (!Number.isFinite(targetId)) {
        return (
            <div className="page-shell">
                <div className="card">
                    <h1>{t("report.modal.title")}</h1>
                    <p>{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    <Button type="button" variant="secondary" onClick={() => navigate("/explore")}>
                        {t("journey.edit.back", { defaultValue: "Back" })}
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="page-shell report-create-page journey-create-page">
            <div className="card journey-create-card">
                <header className="journey-create-header">
                    <div>
                        <p className="eyebrow">{t("report.modal.title")}</p>
                        <h1>{t("report.modal.title")}</h1>
                        <p className="journey-create-header__lead">{t("report.modal.description")}</p>
                    </div>
                    <div className="journey-create-meta">{t("form.requiredHint")}</div>
                </header>

                {serverError && <p className="form-field__text form-field__text--error">{serverError}</p>}

                <form ref={formRef} className="journey-form" onSubmit={handleSubmit} noValidate>
                    <div className="form-field">
                        <label className="input-label" htmlFor="report-reason">
                            {t("report.reason.label")}
                            <span className="required-indicator" aria-hidden="true">
                                *
                            </span>
                        </label>
                        <select
                            id="report-reason"
                            className={touched && reasonError ? "input-control input-control--error" : "input-control"}
                            aria-invalid={touched && reasonError ? true : undefined}
                            value={reason}
                            onChange={(event) => {
                                setServerError("");
                                setReason(event.target.value as ReportReason);
                            }}
                            onBlur={() => setTouched(true)}
                        >
                            <option value="">{t("report.reason.placeholder")}</option>
                            <option value="SPAM">{t("report.reason.spam")}</option>
                            <option value="HARASSMENT">{t("report.reason.harassment")}</option>
                            <option value="INAPPROPRIATE_CONTENT">{t("report.reason.inappropriate")}</option>
                            <option value="MISINFORMATION">{t("report.reason.misinformation")}</option>
                            <option value="HATE_SPEECH">{t("report.reason.hate_speech")}</option>
                            <option value="VIOLENCE">{t("report.reason.violence")}</option>
                            <option value="OTHER">{t("report.reason.other")}</option>
                        </select>
                        {touched && reasonError && (
                            <p className="form-field__text form-field__text--error">{reasonError}</p>
                        )}
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="report-description">
                            {t("report.description.label")}
                            <span className="required-indicator" aria-hidden="true">
                                *
                            </span>
                        </label>
                        <textarea
                            id="report-description"
                            className={touched && descriptionError ? "input-control input-control--error" : "input-control"}
                            rows={6}
                            aria-invalid={touched && descriptionError ? true : undefined}
                            value={description}
                            onChange={(event) => {
                                setServerError("");
                                setDescription(event.target.value);
                            }}
                            onBlur={() => setTouched(true)}
                            placeholder={t("report.description.placeholder")}
                        />
                        <div className="form-field__text report-description-meta">
                            <span>{t("report.description.help")}</span>
                            <span className="report-description-meta__count" aria-live="polite">
                                {description.length}/{MAX_DESCRIPTION_LENGTH}
                            </span>
                        </div>
                        {touched && descriptionError && (
                            <p className="form-field__text form-field__text--error">{descriptionError}</p>
                        )}
                    </div>

                    <div className="journey-form__actions">
                        <Button type="button" variant="ghost" onClick={handleBack} disabled={submitting}>
                            {t("report.modal.cancel")}
                        </Button>
                        <Button type="submit" variant="danger" disabled={submitting}>
                            {submitting
                                ? t("report.submitting", { defaultValue: "Submitting..." })
                                : t("report.modal.submit")}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}

import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminCareerDetailData } from "@/hooks/useAdminDetailData";
import { updateCareer } from "@/lib/api/careers";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";

interface CareerFormState {
    name: string;
}

interface CareerEditFormProps {
    id: string;
    career: { name: string };
}

const API_FIELD_TO_FORM_FIELD: Record<string, keyof CareerFormState> = {
    name: "name",
};

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function CareerEditPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data: career, isLoading, isError } = useAdminCareerDetailData({ id });

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <PageStatus className="entity-create-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !career || !id) {
        return <PageStatus className="entity-create-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    // El form se monta recién con la carrera cargada, así arranca precargado sin
    // tener que sincronizar el estado con un efecto.
    return <CareerEditForm key={id} id={id} career={career} />;
}

function CareerEditForm({ id, career }: CareerEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const [form, setForm] = useState<CareerFormState>({ name: career.name ?? "" });
    const [touched, setTouched] = useState({ name: false });
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<Partial<Record<keyof CareerFormState, string>>>({});
    const [submitting, setSubmitting] = useState(false);

    const clientErrors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createCareer.name", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const errors = useMemo(
        () => ({
            name: clientErrors.name || serverErrors.name || "",
        }),
        [clientErrors, serverErrors]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const formElement = event.currentTarget;
        setTouched({ name: true });
        setServerErrors({});
        if (clientErrors.name) {
            focusFirstInvalidField(formElement);
            return;
        }
        setSubmitting(true);
        setSubmitError(null);
        updateCareer(id, { name: form.name.trim() })
            .then(async () => {
                await invalidateAdminEntityQueries(queryClient, "career", id);
                showToast(t("admin.toast.updated"), { variant: "success" });
                navigate(`/careers/${id}`);
            })
            .catch((error) => {
                console.error("Failed to update career", error);
                const nextServerErrors = mapApiFieldErrors(error, API_FIELD_TO_FORM_FIELD);
                if (Object.keys(nextServerErrors).length > 0) {
                    setServerErrors(nextServerErrors);
                    focusFirstInvalidField(formElement);
                } else {
                    setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                }
            })
            .finally(() => setSubmitting(false));
    };

    return (
        <div className="entity-create-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("editCareer.title", { defaultValue: "Edit Career" })}</h1>
                        <p className="auth-subtitle">
                            {t("editCareer.subtitle", { defaultValue: "Update career information" })}
                        </p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-group">
                            <label htmlFor="career-name" className="form-label required-field">
                                {t("createCareer.name", { defaultValue: "Career Name" })}
                            </label>
                            <input
                                id="career-name"
                                type="text"
                                className={`form-input ${touched.name && errors.name ? "error" : ""}`}
                                value={form.name}
                                onChange={(event) => {
                                    setServerErrors({ name: undefined });
                                    setSubmitError(null);
                                    setForm({ name: event.target.value });
                                }}
                                onBlur={() => {
                                    setTouched({ name: true });
                                    setForm((prev) => ({ name: formatTitleCase(prev.name) }));
                                }}
                                required
                            />
                            {touched.name && errors.name && <p className="error-message">{errors.name}</p>}
                        </div>

                        <button type="submit" className="form-button" disabled={submitting}>
                            {t("editCareer.submit", { defaultValue: "Update Career" })}
                        </button>
                    </form>
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to={`/careers/${id}`} className="auth-link">
                            {t("career.back", { defaultValue: "Back to careers" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

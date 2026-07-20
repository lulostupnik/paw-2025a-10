import { apiErrorMessage } from "@/lib/api/client";
import { useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { createInterest, type InterestPayload } from "@/lib/api/interests";
import { useToast } from "@/components/ui/ToastProvider";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";

interface InterestFormState {
    name: string;
}

const initialForm: InterestFormState = {
    name: "",
};

const API_FIELD_TO_FORM_FIELD: Record<string, keyof InterestFormState> = {
    name: "name",
};

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function InterestCreatePage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState({ name: false });
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<Partial<Record<keyof InterestFormState, string>>>({});

    const clientErrors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createInterest.name", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const errors = useMemo(
        () => ({
            name: clientErrors.name || serverErrors.name || "",
        }),
        [clientErrors, serverErrors]
    );

    const createInterestMutation = useMutation({
        mutationFn: (payload: InterestPayload) => createInterest(payload),
        onSuccess: async (created) => {
            await invalidateAdminEntityQueries(queryClient, "interest");
            showToast(t("admin.toast.created"), { variant: "success" });
            navigate(`/interests/${created.id}`, { replace: true });
        },
        onError: (error) => {
            console.error("Failed to create interest", error);
            const nextServerErrors = mapApiFieldErrors(error, API_FIELD_TO_FORM_FIELD);
            if (Object.keys(nextServerErrors).length > 0) {
                setServerErrors(nextServerErrors);
                focusFirstInvalidField(document.querySelector(".auth-form"));
            } else {
                setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
            }
        },
    });

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true });
        setServerErrors({});
        if (clientErrors.name) {
            focusFirstInvalidField(event.currentTarget);
            return;
        }
        setSubmitError(null);
        createInterestMutation.mutate({ name: form.name.trim() });
    };

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    return (
        <div className="entity-create-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("createInterest.title", { defaultValue: "Create Interest" })}</h1>
                        <p className="auth-subtitle">
                            {t("createInterest.subtitle", { defaultValue: "Add a new interest to the system" })}
                        </p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-group">
                            <label htmlFor="interest-name" className="form-label required-field">
                                {t("createInterest.name", { defaultValue: "Name" })}
                            </label>
                            <input
                                id="interest-name"
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

                        <button type="submit" className="form-button" disabled={createInterestMutation.isPending}>
                            {t("createInterest.submit", { defaultValue: "Create Interest" })}
                        </button>
                    </form>
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to="/admin/interests" className="auth-link">
                            {t("city.back", { defaultValue: "Back" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

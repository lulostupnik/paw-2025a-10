import { apiErrorMessage } from "@/lib/api/client";
import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminInterestDetailData } from "@/hooks/useAdminDetailData";
import { updateInterest, type InterestPayload } from "@/lib/api/interests";

interface InterestFormState {
    name: string;
}

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function InterestEditPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();
    const { data: interest, isLoading, isError } = useAdminInterestDetailData({ id });
    const queryClient = useQueryClient();
    const [form, setForm] = useState<InterestFormState>({ name: "" });
    const [touched, setTouched] = useState({ name: false });
    const [submitError, setSubmitError] = useState<string | null>(null);

    useEffect(() => {
        if (!interest) {
            return;
        }
        setForm({ name: interest.name ?? "" });
    }, [interest]);

    const errors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createInterest.name", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true });
        if (errors.name) {
            return;
        }
        if (!id) {
            setSubmitError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
            return;
        }
        setSubmitError(null);
        updateInterestMutation.mutate({ id, payload: { name: form.name.trim() } });
    };

    const updateInterestMutation = useMutation({
        mutationFn: ({ id: interestId, payload }: { id: string; payload: InterestPayload }) =>
            updateInterest(interestId, payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["adminInterests"] });
            if (id) {
                queryClient.invalidateQueries({ queryKey: ["adminInterestDetail", id] });
                navigate(`/interests/${id}`);
            } else {
                navigate("/admin/interests");
            }
        },
        onError: (error) => {
            console.error("Failed to update interest", error);
            setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        },
    });

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <div className="entity-create-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="entity-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }
    if (!interest) {
        return <div className="entity-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
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
                        <h1 className="auth-title">{t("editInterest.title", { defaultValue: "Edit Interest" })}</h1>
                        <p className="auth-subtitle">
                            {t("editInterest.subtitle", { defaultValue: "Update interest information" })}
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
                                onChange={(event) => setForm({ name: event.target.value })}
                                onBlur={() => {
                                    setTouched({ name: true });
                                    setForm((prev) => ({ name: formatTitleCase(prev.name) }));
                                }}
                                required
                            />
                            {touched.name && errors.name && <p className="error-message">{errors.name}</p>}
                        </div>

                        <button type="submit" className="form-button" disabled={updateInterestMutation.isPending}>
                            {t("editInterest.submit", { defaultValue: "Update Interest" })}
                        </button>
                    </form>
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to={`/interests/${id}`} className="auth-link">
                            {t("city.back", { defaultValue: "Back" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

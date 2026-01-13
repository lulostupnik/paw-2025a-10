import { useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";

interface CareerFormState {
    name: string;
}

const initialForm: CareerFormState = {
    name: "",
};

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function CareerCreatePage() {
    const { t } = useI18n();
    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState({ name: false });

    const errors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createCareer.name", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true });
        if (errors.name) {
            return;
        }
        // TODO: replace with API call to create career, handle success/error state.
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
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("createCareer.title", { defaultValue: "Create Career" })}</h1>
                        <p className="auth-subtitle">
                            {t("createCareer.subtitle", { defaultValue: "Add a new career to the system" })}
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
                                onChange={(event) => setForm({ name: event.target.value })}
                                onBlur={() => {
                                    setTouched({ name: true });
                                    setForm((prev) => ({ name: formatTitleCase(prev.name) }));
                                }}
                                required
                            />
                            {touched.name && errors.name && <p className="error-message">{errors.name}</p>}
                        </div>

                        <button type="submit" className="form-button">
                            {t("createCareer.submit", { defaultValue: "Create Career" })}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <Link to="/admin?tab=careers" className="auth-link">
                            {t("career.back", { defaultValue: "Back to careers" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

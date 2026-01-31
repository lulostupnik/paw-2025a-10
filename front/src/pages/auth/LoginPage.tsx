import { useMemo, useState } from "react";
import type { FormEvent } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { isAxiosError } from "axios";
import Button from "@/components/ui/Button";
import { login } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";

function getNext(search: string) {
    const params = new URLSearchParams(search);
    return params.get("next") || "/explore";
}

interface LoginFormData {
    email: string;
    password: string;
    remember: boolean;
}

const initialForm: LoginFormData = {
    email: "",
    password: "",
    remember: false,
};

export default function LoginPage() {
    const { t } = useI18n();
    const nav = useNavigate();
    const location = useLocation();
    const next = getNext(location.search);

    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState({ email: false, password: false });
    const [showPassword, setShowPassword] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [authError, setAuthError] = useState("");

    const errors = useMemo(() => {
        const result: Record<string, string> = {};
        if (!form.email.trim()) {
            result.email = t("login.email.required");
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
            result.email = t("login.email.invalid");
        }

        if (!form.password) {
            result.password = t("login.password.required");
        }

        return result;
    }, [form, t]);

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ email: true, password: true });
        if (Object.keys(errors).length > 0) {
            return;
        }
        setSubmitting(true);
        setAuthError("");
        try {
            await login({ email: form.email.trim(), password: form.password, remember: form.remember });
            nav(next, { replace: true });
        } catch (error) {
            if (isAxiosError(error) && error.response?.status === 423) {
                const normalizedEmail = form.email.trim();
                nav("/blocked", { replace: true, state: { blockedEmail: normalizedEmail } });
                return;
            }
            if (isAxiosError(error) && error.response?.status === 403) {
                nav("/not-verified", { replace: true });
                return;
            }
            setAuthError(t("login.error.description"));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="page-shell auth-page">
            <header className="register-header">
                <p className="eyebrow">{t("login.subtitle")}</p>
                <h1>{t("login.title")}</h1>
                <p className="register-header__lead">{t("login.description")}</p>
            </header>

            <form className="auth-card card" onSubmit={handleSubmit} noValidate>
                {authError && <p className="form-field__text form-field__text--error">{authError}</p>}
                <div className="form-field">
                    <label className="input-label" htmlFor="login-email">
                        {t("login.email")}
                        <span className="required-indicator" aria-hidden="true">*</span>
                    </label>
                    <input
                        id="login-email"
                        type="email"
                        className={classNames("input-control", touched.email && errors.email && "input-control--error")}
                        value={form.email}
                        onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
                        onBlur={() => setTouched((prev) => ({ ...prev, email: true }))}
                        placeholder={t("login.email.placeholder")}
                    />
                    {touched.email && errors.email && (
                        <p className="form-field__text form-field__text--error">{errors.email}</p>
                    )}
                </div>

                <div className="form-field">
                    <label className="input-label" htmlFor="login-password">
                        {t("login.password")}
                        <span className="required-indicator" aria-hidden="true">*</span>
                    </label>
                    <div className="input-with-addon">
                        <input
                            id="login-password"
                            type={showPassword ? "text" : "password"}
                            className={classNames(
                                "input-control",
                                touched.password && errors.password && "input-control--error"
                            )}
                            value={form.password}
                            onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
                            onBlur={() => setTouched((prev) => ({ ...prev, password: true }))}
                            placeholder={t("login.passwordPlaceholder")}
                        />
                        <button
                            type="button"
                            className="input-addon"
                            onClick={() => setShowPassword((prev) => !prev)}
                            aria-label={showPassword ? t("password.hide") : t("password.show")}
                        >
                            {showPassword ? t("password.hide") : t("password.show")}
                        </button>
                    </div>
                    {touched.password && errors.password && (
                        <p className="form-field__text form-field__text--error">{errors.password}</p>
                    )}
                </div>

                <div className="login-actions">
                    <label className="checkbox-field">
                        <input
                            type="checkbox"
                            className="checkbox-field__input"
                            checked={form.remember}
                            onChange={(event) =>
                                setForm((prev) => ({ ...prev, remember: event.target.checked }))
                            }
                        />
                        <span className="checkbox-field__box" aria-hidden="true" />
                        <span className="checkbox-field__label-text">{t("remember_me")}</span>
                    </label>
                    <Button type="button" variant="ghost" size="sm" onClick={() => nav("/forgot-password")}>
                        {t("login.forgot_password")}
                    </Button>
                </div>

                <div className="form-actions">
                    <Button type="submit" size="lg" fullWidth disabled={submitting}>
                        {t("login.submit")}
                    </Button>
                    <div className="form-footnote">
                        <span>{t("login.no.account")}</span>
                        <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            onClick={() => nav("/register")}
                            className="form-footnote__link"
                        >
                            {t("login.register")}
                        </Button>
                    </div>
                </div>
            </form>
        </div>
    );
}

import { useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { isAxiosError } from "axios";
import Button from "@/components/ui/Button";
import StatusCard from "@/components/ui/StatusCard";
import { requestPasswordReset } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const RESET_EMAIL_STORAGE_KEY = "forgot_password_email";

type SubmitState = "idle" | "success";

type ForgotPasswordError = "notFound" | "notVerified" | "blocked" | "general";

function mapForgotPasswordError(error: unknown): ForgotPasswordError {
    if (!isAxiosError(error)) {
        return "general";
    }
    const status = error.response?.status;
    const message = String(error.response?.data?.message ?? "").toLowerCase();

    if (status === 404) {
        return "notFound";
    }
    if (status === 403) {
        return "blocked";
    }
    if (status === 409 || message.includes("validat")) {
        return "notVerified";
    }
    return "general";
}

export default function ForgotPasswordPage() {
    const { t } = useI18n();
    const [email, setEmail] = useState("");
    const [touched, setTouched] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [status, setStatus] = useState<SubmitState>("idle");
    const [serverError, setServerError] = useState("");
    const [submittedEmail, setSubmittedEmail] = useState("");

    const emailError = useMemo(() => {
        const value = email.trim();
        if (!value) {
            return t("forgotpassword.error.email.required");
        }
        if (!emailPattern.test(value)) {
            return t("forgotpassword.error.email.invalid");
        }
        return "";
    }, [email, t]);

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched(true);
        if (emailError) {
            return;
        }

        setSubmitting(true);
        setServerError("");
        setStatus("idle");

        try {
            const normalizedEmail = email.trim();
            await requestPasswordReset({ email: normalizedEmail });
            setSubmittedEmail(normalizedEmail);
            if (typeof window !== "undefined") {
                try {
                    window.sessionStorage.setItem(RESET_EMAIL_STORAGE_KEY, normalizedEmail);
                } catch {
                    // Ignore storage errors.
                }
            }
            setStatus("success");
        } catch (error) {
            switch (mapForgotPasswordError(error)) {
                case "notFound":
                    setServerError(t("forgotpassword.error.email.not.found"));
                    break;
                case "notVerified":
                    setServerError(t("forgotpassword.error.account.not.verified"));
                    break;
                case "blocked":
                    setServerError(t("forgotpassword.error.account.blocked"));
                    break;
                default:
                    setServerError(t("forgotpassword.error.general"));
                    break;
            }
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="page-shell auth-page forgot-password-page">
            <header className="register-header">
                <p className="eyebrow">{t("forgotpassword.title")}</p>
                <h1>{t("forgotpassword.title")}</h1>
                <p className="register-header__lead">{t("forgotpassword.subtitle")}</p>
            </header>

            {status === "success" && (
                <StatusCard
                    variant="success"
                    title={t("forgotpassword.check.inbox")}
                    description={t("forgotpassword.email.instructions")}
                >
                    <ul className="status-card__list">
                        <li>
                            {t("forgotpassword.email.sent")} <strong>{submittedEmail}</strong>
                        </li>
                        <li>{t("forgotpassword.email.expiry")}</li>
                        <li>{t("forgotpassword.check.spam")}</li>
                    </ul>
                </StatusCard>
            )}

            <div className="forgot-password-grid">
                <form className="auth-card card" onSubmit={handleSubmit} noValidate>
                    {serverError && (
                        <p className="form-field__text form-field__text--error">{serverError}</p>
                    )}
                    <div className="form-field">
                        <label className="input-label" htmlFor="forgot-password-email">
                            {t("forgotpassword.email")}
                            <span className="required-indicator" aria-hidden="true">
                                *
                            </span>
                        </label>
                        <input
                            id="forgot-password-email"
                            type="email"
                            className={classNames(
                                "input-control",
                                touched && emailError && "input-control--error"
                            )}
                            value={email}
                            onChange={(event) => {
                                setEmail(event.target.value);
                                if (serverError) {
                                    setServerError("");
                                }
                            }}
                            onBlur={() => setTouched(true)}
                            placeholder={t("forgotpassword.email.placeholder")}
                            autoComplete="email"
                        />
                        {touched && emailError && (
                            <p className="form-field__text form-field__text--error">{emailError}</p>
                        )}
                    </div>
                    <Button type="submit" size="lg" fullWidth disabled={submitting}>
                        {t("forgotpassword.submit")}
                    </Button>
                    <div className="form-footnote">
                        <span>{t("forgotpassword.remember")}</span>
                        <Link to="/login" className="form-footnote__link">
                            {t("forgotpassword.login")}
                        </Link>
                    </div>
                </form>

                <div className="card forgot-password-help">
                    <h3>{t("forgotpassword.check.inbox")}</h3>
                    <p>{t("forgotpassword.check.spam")}</p>
                    <p>{t("forgotpassword.email.expiry")}</p>
                </div>
            </div>
        </div>
    );
}

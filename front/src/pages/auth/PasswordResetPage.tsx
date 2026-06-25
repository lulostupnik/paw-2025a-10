import { useMemo, useState, type FormEvent } from "react";
import { isAxiosError } from "axios";
import { useNavigate, useSearchParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import StatusCard from "@/components/ui/StatusCard";
import { resetPasswordWithToken } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

type ResetStatus = "form" | "submitting" | "expired" | "invalid" | "blocked" | "error";
type PasswordStrengthStatus = "empty" | "very-weak" | "weak" | "medium" | "strong";

interface PasswordStrength {
    level: number;
    status: PasswordStrengthStatus;
    labelKey: string;
    class: string;
}

function evaluatePassword(value: string): PasswordStrength {
    if (!value) {
        return { level: 0, status: "empty", labelKey: "register.password.strength.empty", class: 'password-strength--very-weak'};
    }

    let score = 0;
    if (value.length >= 8) score += 1;
    if (/[A-Z]/.test(value)) score += 1;
    if (/[0-9]/.test(value)) score += 1;
    if (/[^A-Za-z0-9]/.test(value) || value.length >= 12) score += 1;

    if (score <= 1) {
        return { level: 1, status: "very-weak", labelKey: "register.password.strength.veryWeak", class: 'password-strength--very-weak' };
    }
    if (score === 2) {
        return { level: 2, status: "weak", labelKey: "register.password.strength.weak", class: 'password-strength--weak'};
    }
    if (score === 3) {
        return { level: 3, status: "medium", labelKey: "register.password.strength.medium", class: 'password-strength--medium'};
    }
    return { level: 4, status: "strong", labelKey: "register.password.strength.strong", class: 'password-strength--strong' };
}


const mapResetError = (error: unknown): ResetStatus => {
    if (!isAxiosError(error)) {
        return "error";
    }
    const status = error.response?.status;
    const message = String(error.response?.data?.message ?? "").toLowerCase();
    if (status === 410 || message.includes("expir")) {
        return "expired";
    }
    if (status === 423 || message.includes("block")) {
        return "blocked";
    }
    if (status === 401 || status === 400 || status === 404 || message.includes("invalid")) {
        return "invalid";
    }
    return "error";
};

export default function PasswordResetPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [params] = useSearchParams();
    const token = (params.get("token") ?? params.get("amp;token") ?? "").trim();
    const userId = (params.get("userId") ?? params.get("amp;userId") ?? "").trim();
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [touched, setTouched] = useState({ password: false, confirmPassword: false });
    const [status, setStatus] = useState<ResetStatus>(() => (token && userId ? "form" : "invalid"));
    const [serverError, setServerError] = useState("");
    const passwordStrength = useMemo(() => evaluatePassword(password), [password]);
    const passwordError = useMemo(() => {
        if (!password) {
            return t("register.validation.password.required");
        }
        if (password.length < 8) {
            return t("register.password.requirements");
        }
        return "";
    }, [password, t]);
    const confirmPasswordError = useMemo(() => {
        if (!confirmPassword) {
            return t("register.validation.confirmPassword.required");
        }
        if (password !== confirmPassword) {
            return t("register.validation.confirmPassword.match");
        }
        return "";
    }, [confirmPassword, password, t]);

    const passwordsMatch = password && confirmPassword ? password === confirmPassword : true;

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ password: true, confirmPassword: true });
        if (!token || !userId || passwordError || confirmPasswordError || !passwordsMatch) {
            return;
        }
        setStatus("submitting");
        setServerError("");
        try {
            await resetPasswordWithToken({ token, userId, password, confirmPassword });
            navigate("/password/reset/confirmation?status=success");
        } catch (error) {
            const nextStatus = mapResetError(error);
            setStatus(nextStatus);
            if (nextStatus === "error") {
                setServerError(t("password.confirmation.error.title", { defaultValue: "There was an error." }));
            }
        }
    };

    const renderStatusCard = () => {
        switch (status) {
            case "expired":
                return (
                    <StatusCard
                        variant="warning"
                        title={t("expiredtoken.title")}
                        description={t("expiredtoken.explanation")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/forgot-password")}>
                                    {t("forgotpassword.submit")}
                                </Button>
                                <a className="btn btn--ghost" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("expiredtoken.contact.us")}
                                </a>
                            </>
                        }
                    >
                        <p>{t("expiredtoken.instructions")}</p>
                    </StatusCard>
                );
            case "invalid":
                return (
                    <StatusCard
                        variant="error"
                        title={t("invalidtoken.title")}
                        description={t("invalidtoken.explanation")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/forgot-password")}>
                                    {t("forgotpassword.submit")}
                                </Button>
                                <a className="btn btn--ghost" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("invalidtoken.contact.us")}
                                </a>
                            </>
                        }
                    >
                        <p>{t("invalidtoken.instructions")}</p>
                    </StatusCard>
                );
            case "blocked":
                return (
                    <StatusCard
                        variant="error"
                        title={t("blocked.title")}
                        description={t("blocked.explanation")}
                        actions={
                            <>
                                <a className="btn btn--primary" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("blocked.contact.us")}
                                </a>
                                <Button variant="ghost" onClick={() => navigate("/")}>
                                    {t("blocked.back.to.home")}
                                </Button>
                            </>
                        }
                    >
                        <p>{t("blocked.instructions")}</p>
                    </StatusCard>
                );
            case "error":
                return (
                    <StatusCard
                        variant="error"
                        title={t("password.confirmation.error.title")}
                        description={t("password.confirmation.help")}
                        actions={
                            <>
                                <a className="btn btn--primary" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("password.confirmation.actions.support")}
                                </a>
                                <Button variant="ghost" onClick={() => navigate("/login")}>
                                    {t("password.confirmation.actions.login")}
                                </Button>
                            </>
                        }
                    />
                );
            default:
                return null;
        }
    };

    if (status !== "form" && status !== "submitting") {
        return (
            <div className="page-shell auth-page reset-password-page">
                <header className="register-header">
                    <p className="eyebrow">{t("profile.edit.password")}</p>
                    <h1>{t("profile.edit.password")}</h1>
                    <p className="register-header__lead">{t("profile.change.password")}</p>
                </header>
                {renderStatusCard()}
            </div>
        );
    }

    return (
        <div className="page-shell auth-page reset-password-page">
            <header className="register-header">
                <p className="eyebrow">{t("profile.edit.password")}</p>
                <h1>{t("profile.edit.password")}</h1>
                <p className="register-header__lead">{t("profile.change.password")}</p>
            </header>

            <form className="auth-card card" onSubmit={handleSubmit} noValidate>
                {serverError && <p className="form-field__text form-field__text--error">{serverError}</p>}
                <div className="form-group">
                    <label htmlFor="password" className="form-label required-field">
                        {t("profile.new.password")}
                    </label>
                    <div className="password-field-container">
                        <input
                            id="password"
                            type="password"
                            className={classNames("form-input input-control required", touched.password && passwordError && "error")}
                            placeholder="••••••••"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            onBlur={() => setTouched((prev) => ({ ...prev, password: true }))}
                        />
                    </div>
                    {touched.password && passwordError && (
                        <p className="form-field__text form-field__text--error">{passwordError}</p>
                    )}
                    <div className={classNames("password-strength", passwordStrength.class)}>
                        <div className="password-strength__header">
                            <span>{t("register.password.strength.label")}</span>
                            {passwordStrength.level > 0 && (
                                <span>{t(passwordStrength.labelKey)}</span>
                            )}
                        </div>
                        <div className="password-strength__bars">
                            {[0, 1, 2, 3].map((index) => (
                                <span
                                    key={index}
                                    className={classNames(
                                        "password-strength__bar",
                                        passwordStrength.level > index && "is-filled"
                                    )}
                                />
                            ))}
                        </div>
                    </div>
                </div>

                <div className="form-group">
                    <label htmlFor="confirmPassword" className="form-label required-field">
                        {t("profile.confirm.password")}
                    </label>
                    <div className="password-field-container">
                        <input
                            id="confirmPassword"
                            type="password"
                            className={classNames(
                                "form-input required input-control",
                                touched.confirmPassword && confirmPasswordError && "error"
                            )}
                            placeholder="••••••••"
                            value={confirmPassword}
                            onChange={(event) => setConfirmPassword(event.target.value)}
                            onBlur={() => setTouched((prev) => ({ ...prev, confirmPassword: true }))}
                        />
                    </div>
                    {touched.confirmPassword && confirmPasswordError && (
                        <p className="form-field__text form-field__text--error">{confirmPasswordError}</p>
                    )}
                    <div className={classNames("password-match-message", passwordsMatch ? "match" : "mismatch")}>
                        {confirmPassword &&
                            (passwordsMatch
                                ? t("password.match", { defaultValue: "Las contraseñas coinciden" })
                                : t("password.mismatch", { defaultValue: "Las contraseñas no coinciden" }))}
                    </div>
                </div>

                <button type="submit" className="btn btn--primary btn--lg btn--full" disabled={status === "submitting"}>
                    {t("profile.edit.password.save")}
                </button>
            </form>
        </div>
    );
}

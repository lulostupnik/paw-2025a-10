import { useMemo, useState, type FormEvent } from "react";
import { isAxiosError } from "axios";
import { useNavigate, useSearchParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import StatusCard from "@/components/ui/StatusCard";
import { resetPassword } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

type ResetStatus = "form" | "submitting" | "expired" | "invalid" | "blocked" | "error";

const getStrengthScore = (password: string) => {
    if (!password) {
        return 0;
    }
    let score = 0;
    if (password.length >= 8) score += 25;
    if (/[A-Z]/.test(password)) score += 20;
    if (/[a-z]/.test(password)) score += 20;
    if (/[0-9]/.test(password)) score += 20;
    if (/[^A-Za-z0-9]/.test(password)) score += 15;
    return Math.min(score, 100);
};

const getStrengthClass = (score: number) => {
    if (score === 0) return "";
    if (score < 25) return "strength-very-weak";
    if (score < 50) return "strength-weak";
    if (score < 75) return "strength-medium";
    if (score < 90) return "strength-strong";
    return "strength-very-strong";
};

const getStrengthLabel = (score: number, t: (key: string, options?: { defaultValue?: string }) => string) => {
    if (score === 0) return "";
    if (score < 25) return t("password.strength.very-weak", { defaultValue: "Muy debil" });
    if (score < 50) return t("password.strength.weak", { defaultValue: "Debil" });
    if (score < 75) return t("password.strength.medium", { defaultValue: "Media" });
    if (score < 90) return t("password.strength.strong", { defaultValue: "Fuerte" });
    return t("password.strength.very-strong", { defaultValue: "Muy fuerte" });
};

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
    if (status === 400 || status === 404 || message.includes("invalid")) {
        return "invalid";
    }
    return "error";
};

export default function PasswordResetPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [params] = useSearchParams();
    const token = params.get("token") ?? "";
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [touched, setTouched] = useState({ password: false, confirmPassword: false });
    const [status, setStatus] = useState<ResetStatus>(() => (token ? "form" : "invalid"));
    const [serverError, setServerError] = useState("");

    const strengthScore = useMemo(() => getStrengthScore(password), [password]);
    const strengthLabel = useMemo(() => getStrengthLabel(strengthScore, t), [strengthScore, t]);
    const passwordsMatch = password && confirmPassword ? password === confirmPassword : true;

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ password: true, confirmPassword: true });
        if (!token || !password || !confirmPassword || !passwordsMatch) {
            return;
        }
        setStatus("submitting");
        setServerError("");
        try {
            await resetPassword({ token, password, confirmPassword });
            navigate("/password/reset/confirmation?status=success");
        } catch (error) {
            const nextStatus = mapResetError(error);
            if (nextStatus === "error" && error instanceof Error && error.message === "reset-password-not-implemented") {
                setStatus("form");
                setServerError(t("admin.dashboard.error", { defaultValue: "TODO: reset password endpoint pendiente." }));
                return;
            }
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
                            className={classNames("form-input required", touched.password && !password && "error")}
                            placeholder="••••••••"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            onBlur={() => setTouched((prev) => ({ ...prev, password: true }))}
                        />
                    </div>
                    <div className="password-strength">
                        <div className="password-meter">
                            <div className={classNames("password-bar", getStrengthClass(strengthScore))} style={{ width: `${strengthScore}%` }}></div>
                        </div>
                        <div className="password-feedback">
                            <div className="password-status">
                                <span className={classNames("password-label", getStrengthClass(strengthScore))}>{strengthLabel}</span>
                            </div>
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
                                "form-input required",
                                touched.confirmPassword && (!confirmPassword || !passwordsMatch) && "error"
                            )}
                            placeholder="••••••••"
                            value={confirmPassword}
                            onChange={(event) => setConfirmPassword(event.target.value)}
                            onBlur={() => setTouched((prev) => ({ ...prev, confirmPassword: true }))}
                        />
                    </div>
                    <div className={classNames("password-match-message", passwordsMatch ? "match" : "mismatch")}>
                        {confirmPassword &&
                            (passwordsMatch
                                ? t("password.match", { defaultValue: "Las contraseñas coinciden" })
                                : t("password.mismatch", { defaultValue: "Las contraseñas no coinciden" }))}
                    </div>
                </div>

                <button type="submit" className="form-button" disabled={status === "submitting"}>
                    {t("profile.edit.password.save")}
                </button>
            </form>
        </div>
    );
}

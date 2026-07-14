import { useMemo, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import { classNames } from "@/lib/utils/classNames";
import { useToast } from "@/components/ui/ToastProvider";
import { apiErrorMessage } from "@/lib/api/client";
import { sanitizeInternalPath } from "@/lib/utils/internalPath";

type PasswordStrengthStatus = "empty" | "very-weak" | "weak" | "medium" | "strong";

interface PasswordStrength {
    level: number;
    status: PasswordStrengthStatus;
    labelKey: string;
}

const evaluatePassword = (value: string): PasswordStrength => {
    if (!value) {
        return { level: 0, status: "empty", labelKey: "register.password.strength.empty" };
    }

    let score = 0;
    if (value.length >= 8) score += 1;
    if (/[A-Z]/.test(value)) score += 1;
    if (/[0-9]/.test(value)) score += 1;
    if (/[^A-Za-z0-9]/.test(value) || value.length >= 12) score += 1;

    if (score <= 1) {
        return { level: 1, status: "very-weak", labelKey: "register.password.strength.veryWeak" };
    }
    if (score === 2) {
        return { level: 2, status: "weak", labelKey: "register.password.strength.weak" };
    }
    if (score === 3) {
        return { level: 3, status: "medium", labelKey: "register.password.strength.medium" };
    }
    return { level: 4, status: "strong", labelKey: "register.password.strength.strong" };
};

export default function ProfilePasswordForm() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const { showToast } = useToast();
    const { updatePassword, isLoading } = useProfileUpsert();
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [touched, setTouched] = useState({ password: false, confirmPassword: false });
    const [submitError, setSubmitError] = useState<string | null>(null);
    const returnPath = sanitizeInternalPath((location.state as { from?: string } | null)?.from);

    const passwordStrength = useMemo(() => evaluatePassword(password), [password]);
    const passwordsMatch = password && confirmPassword ? password === confirmPassword : true;

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ password: true, confirmPassword: true });
        setSubmitError(null);
        if (!password || !confirmPassword || !passwordsMatch) {
            return;
        }
        try {
            await updatePassword({ password, confirmPassword });
            showToast(t("profile.toast.passwordUpdated"), { variant: "success" });
            navigate(returnPath ?? "/profiles/me/info", { replace: true });
        } catch (error) {
            setSubmitError(
                apiErrorMessage(
                    error,
                    t("admin.dashboard.error", { defaultValue: "Error guardando los cambios." })
                )
            );
        }
    };

    return (
        <div className="profile-password-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("profile.edit.password")}</h1>
                        <p className="auth-subtitle">{t("profile.change.password")}</p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-group">
                            <label htmlFor="password" className="form-label required-field">
                                {t("profile.new.password")}
                            </label>
                            <div className="password-field-container">
                                <input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    className={`form-input required ${touched.password && !password ? "error" : ""}`}
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={(event) => setPassword(event.target.value)}
                                    onBlur={() => setTouched((prev) => ({ ...prev, password: true }))}
                                />
                                <button
                                    type="button"
                                    className="password-toggle-button"
                                    onClick={() => setShowPassword((prev) => !prev)}
                                    aria-label={showPassword ? t("password.hide", { defaultValue: "Ocultar contraseña" }) : t("password.show", { defaultValue: "Mostrar contraseña" })}
                                >
                                    {showPassword ? (
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                            <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                            <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                        </svg>
                                    ) : (
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                            <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                        </svg>
                                    )}
                                </button>
                            </div>

                            <div className={classNames("password-strength", `password-strength--${passwordStrength.status}`)}>
                                <div className="password-strength__header">
                                    <span>{t("register.password.strength.label")}</span>
                                    <span className="password-strength__value">
                                        {passwordStrength.level > 0 ? t(passwordStrength.labelKey) : ""}
                                    </span>
                                </div>
                                <div className="password-strength__bars">
                                    {[1, 2, 3, 4].map((level) => (
                                        <span
                                            key={level}
                                            className={classNames(
                                                "password-strength__bar",
                                                level <= passwordStrength.level && "is-filled"
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
                                    type={showConfirmPassword ? "text" : "password"}
                                    className={`form-input required ${touched.confirmPassword && (!confirmPassword || !passwordsMatch) ? "error" : ""}`}
                                    placeholder="••••••••"
                                    value={confirmPassword}
                                    onChange={(event) => setConfirmPassword(event.target.value)}
                                    onBlur={() => setTouched((prev) => ({ ...prev, confirmPassword: true }))}
                                />
                                <button
                                    type="button"
                                    className="password-toggle-button"
                                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                                    aria-label={showConfirmPassword ? t("password.hide", { defaultValue: "Ocultar contraseña" }) : t("password.show", { defaultValue: "Mostrar contraseña" })}
                                >
                                    {showConfirmPassword ? (
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                            <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                            <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                        </svg>
                                    ) : (
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                            <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                        </svg>
                                    )}
                                </button>
                            </div>
                            <div className={classNames("password-match-message", passwordsMatch ? "match" : "mismatch")}>
                                {confirmPassword && (
                                    passwordsMatch
                                        ? t("password.match", { defaultValue: "Las contraseñas coinciden" })
                                        : t("password.mismatch", { defaultValue: "Las contraseñas no coinciden" })
                                )}
                            </div>
                        </div>

                        {submitError && <p className="error-message">{submitError}</p>}

                        <button type="submit" className="form-button" disabled={isLoading}>
                            {t("profile.save.changes")}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <Link to={returnPath ?? "/profiles/me/info"} className="auth-link">
                            {t("profile.back.to.profile")}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

import { useEffect, useMemo, useRef, useState } from "react";
import type { FormEvent, ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import {
    searchCareers,
    searchUniversities,
    searchInterests,
    type CatalogOption,
    type CatalogSearchFn,
} from "@/lib/api/catalog";
import { useRegister } from "@/hooks/useRegister";

type RegisterField =
    | "email"
    | "username"
    | "password"
    | "confirmPassword"
    | "firstName"
    | "lastName"
    | "career"
    | "university"
    | "interests";

interface RegisterFormData {
    email: string;
    username: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    career: CatalogOption | null;
    university: CatalogOption | null;
    interests: CatalogOption[];
}

interface ValidationResult {
    [key: string]: string | undefined;
}

type PasswordStrengthStatus = "empty" | "very-weak" | "weak" | "medium" | "strong";

interface PasswordStrength {
    level: number;
    status: PasswordStrengthStatus;
    labelKey: string;
}

const initialForm: RegisterFormData = {
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    career: null,
    university: null,
    interests: [],
};

const initialTouchedState: Record<RegisterField, boolean> = {
    email: false,
    username: false,
    password: false,
    confirmPassword: false,
    firstName: false,
    lastName: false,
    career: false,
    university: false,
    interests: false,
};

function evaluatePassword(value: string): PasswordStrength {
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
}

function useAsyncOptions(fetcher: CatalogSearchFn, enabled: boolean, query: string) {
    const [options, setOptions] = useState<CatalogOption[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!enabled) {
            return;
        }
        const controller = new AbortController();
        setLoading(true);
        fetcher(query, controller.signal)
            .then((result) => setOptions(result))
            .catch(() => setOptions([]))
            .finally(() => setLoading(false));

        return () => controller.abort();
    }, [fetcher, enabled, query]);

    return { options, loading };
}

function useDropdownState() {
    const [open, setOpen] = useState(false);
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!open) return;
        const handleClick = (event: MouseEvent) => {
            if (ref.current && !ref.current.contains(event.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClick);
        return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    return { open, setOpen, ref } as const;
}

interface SingleSelectFieldProps {
    label: string;
    name: RegisterField;
    placeholder: string;
    value: CatalogOption | null;
    onChange: (option: CatalogOption | null) => void;
    fetcher: CatalogSearchFn;
    error?: string;
    touched?: boolean;
    required?: boolean;
}

function SingleSelectField({ label, name, placeholder, value, onChange, fetcher, error, touched, required = true }: SingleSelectFieldProps) {
    const { t } = useI18n();
    const [query, setQuery] = useState("");
    const { open, setOpen, ref } = useDropdownState();
    const { options, loading } = useAsyncOptions(fetcher, open, query);

    useEffect(() => {
        if (!value) {
            setQuery("");
        }
    }, [value]);

    const showError = Boolean(error && touched);

    return (
        <div className={classNames("form-field", "autocomplete-field", showError && "has-error")} ref={ref}>
            <label className="input-label" htmlFor={`field-${name}`}>
                {label}
                {required && (
                    <span className="required-indicator" aria-hidden="true">
                        *
                    </span>
                )}
            </label>

            {value ? (
                <div className="selected-option">
                    <div>
                        <p className="selected-option__value">{value.name}</p>
                    </div>
                    <button
                        type="button"
                        className="selected-option__action"
                        onClick={() => {
                            onChange(null);
                            setOpen(true);
                        }}
                    >
                        {t("register.autocomplete.change")}
                    </button>
                </div>
            ) : (
                <>
                    <div className="input-with-addon">
                        <input
                            id={`field-${name}`}
                            className={classNames("input-control", showError && "input-control--error")}
                            value={query}
                            onFocus={() => setOpen(true)}
                            onChange={(event) => setQuery(event.target.value)}
                            placeholder={placeholder}
                            autoComplete="off"
                        />
                        <button type="button" className="input-addon" onClick={() => setOpen((prev) => !prev)} aria-label={t("register.autocomplete.toggle")}>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                            </svg>
                        </button>
                    </div>
                    <div className={classNames("autocomplete-panel", open && "is-open")}>
                        {loading && <p className="autocomplete-status">{t("register.autocomplete.loading")}</p>}
                        {!loading && options.length === 0 && (
                            <p className="autocomplete-status">{t("register.autocomplete.noResults")}</p>
                        )}
                        {!loading &&
                            options.map((option) => (
                                <button
                                    type="button"
                                    key={option.id}
                                    className="autocomplete-option"
                                    onMouseDown={(event) => event.preventDefault()}
                                    onClick={() => {
                                        onChange(option);
                                        setOpen(false);
                                    }}
                                >
                                    {option.name}
                                </button>
                            ))}
                    </div>
                </>
            )}

            {showError && <p className="form-field__text form-field__text--error">{error}</p>}
        </div>
    );
}

interface MultiSelectFieldProps {
    label: string;
    name: RegisterField;
    placeholder: string;
    selected: CatalogOption[];
    onChange: (next: CatalogOption[]) => void;
    fetcher: CatalogSearchFn;
    helper?: string;
    error?: string;
    touched?: boolean;
    required?: boolean;
}

function MultiSelectField({
    label,
    name,
    placeholder,
    selected,
    onChange,
    fetcher,
    helper,
    error,
    touched,
    required = true,
}: MultiSelectFieldProps) {
    const { t } = useI18n();
    const [query, setQuery] = useState("");
    const { open, setOpen, ref } = useDropdownState();
    const { options, loading } = useAsyncOptions(fetcher, open, query);

    const available = options.filter((option) => !selected.some((item) => item.id === option.id));
    const showError = Boolean(error && touched);

    return (
        <div className={classNames("form-field", "autocomplete-field", "autocomplete-field--multiple", showError && "has-error")} ref={ref}>
            <label className="input-label" htmlFor={`field-${name}`}>
                {label}
                {required && (
                    <span className="required-indicator" aria-hidden="true">
                        *
                    </span>
                )}
            </label>
            <div className="input-with-addon">
                <input
                    id={`field-${name}`}
                    className={classNames("input-control", showError && "input-control--error")}
                    value={query}
                    onFocus={() => setOpen(true)}
                    onChange={(event) => setQuery(event.target.value)}
                    placeholder={placeholder}
                    autoComplete="off"
                />
                <button type="button" className="input-addon" onClick={() => setOpen((prev) => !prev)} aria-label={t("register.autocomplete.toggle")}>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
                </button>
            </div>

            <div className="chip-list" aria-live="polite">
                {selected.map((interest) => (
                    <span className="chip" key={interest.id}>
                        {interest.name}
                        <button
                            type="button"
                            onClick={() => onChange(selected.filter((item) => item.id !== interest.id))}
                            aria-label={t("register.interests.remove", { values: { name: interest.name } })}
                        >
                            ×
                        </button>
                    </span>
                ))}
            </div>

            {selected.length === 0 && helper && <p className="form-field__text">{helper}</p>}
            {showError && <p className="form-field__text form-field__text--error">{error}</p>}

            <div className={classNames("autocomplete-panel", open && "is-open")}
                role="listbox"
                aria-multiselectable="true"
            >
                {loading && <p className="autocomplete-status">{t("register.autocomplete.loading")}</p>}
                {!loading && available.length === 0 && (
                    <p className="autocomplete-status">{t("register.autocomplete.noResults")}</p>
                )}
                {!loading &&
                    available.map((option) => (
                        <button
                            type="button"
                            key={option.id}
                            className="autocomplete-option"
                            onMouseDown={(event) => event.preventDefault()}
                            onClick={() => {
                                onChange([...selected, option]);
                                setQuery("");
                                setOpen(false);
                            }}
                        >
                            {option.name}
                        </button>
                    ))}
            </div>
        </div>
    );
}

function validateForm(data: RegisterFormData, t: (key: string, options?: Record<string, unknown>) => string): ValidationResult {
    const errors: ValidationResult = {};

    if (!data.email.trim()) {
        errors.email = t("register.validation.email.required");
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email.trim())) {
        errors.email = t("register.validation.email.invalid");
    }

    if (!data.username.trim()) {
        errors.username = t("register.validation.username.required");
    } else if (data.username.trim().length < 3) {
        errors.username = t("register.validation.username.min");
    } else if (/\s/.test(data.username)) {
        errors.username = t("register.validation.username.spaces");
    }

    if (!data.password) {
        errors.password = t("register.validation.password.required");
    } else if (!/(?=.*[A-Za-z])(?=.*\d).{8,}/.test(data.password)) {
        errors.password = t("register.validation.password.format");
    }

    if (!data.confirmPassword) {
        errors.confirmPassword = t("register.validation.confirmPassword.required");
    } else if (data.confirmPassword !== data.password) {
        errors.confirmPassword = t("register.validation.confirmPassword.match");
    }

    if (!data.firstName.trim()) {
        errors.firstName = t("register.validation.firstName.required");
    }

    if (!data.lastName.trim()) {
        errors.lastName = t("register.validation.lastName.required");
    }

    if (!data.career) {
        errors.career = t("register.validation.career.required");
    }

    if (!data.university) {
        errors.university = t("register.validation.university.required");
    }

    if (data.interests.length === 0) {
        errors.interests = t("register.validation.interests.required");
    }

    return errors;
}

export default function RegisterPage() {
    const { t } = useI18n();
    const nav = useNavigate();
    const { register: submitRegister, loading: registering, error: registerErrorKey, success, nextPath, reset } = useRegister();

    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState<Record<RegisterField, boolean>>(initialTouchedState);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const passwordStrength = useMemo(() => evaluatePassword(form.password), [form.password]);
    const errors = useMemo(() => validateForm(form, t), [form, t]);
    const loginTarget = useMemo(() => {
        const query = nextPath ? `?next=${encodeURIComponent(nextPath)}` : "";
        return `/login${query}`;
    }, [nextPath]);

    const markTouched = (field: RegisterField) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
    };

    const handleTextChange = (field: keyof RegisterFormData) => (event: ChangeEvent<HTMLInputElement>) => {
        const { value } = event.target;
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        reset();
        const touchedState: Record<RegisterField, boolean> = {
            email: true,
            username: true,
            password: true,
            confirmPassword: true,
            firstName: true,
            lastName: true,
            career: true,
            university: true,
            interests: true,
        };
        setTouched(touchedState);

        if (Object.keys(errors).length > 0) {
            return;
        }

        try {
            await submitRegister({
                email: form.email.trim(),
                username: form.username.trim(),
                password: form.password,
                confirmPassword: form.confirmPassword,
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                career: form.career?.name ?? "",
                originUniversity: form.university?.name ?? "",
                interests: form.interests.map((interest) => interest.name),
            });
            setForm(initialForm);
            setTouched(initialTouchedState);
        } catch {
            // Error is handled by the hook
        }
    };

    const header = (
        <header className="register-header">
            <div>
                <p className="eyebrow">{t("register.subtitle")}</p>
                <h1>{t("register.title")}</h1>
                <p className="register-header__lead">{t("register.description")}</p>
            </div>
        </header>
    );

    if (success) {
        return (
            <div className="page-shell register-page">
                {header}
                <div className="card register-success-card" role="status">
                    <span className="register-success-card__icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                        </svg>
                    </span>
                    <h2>{t("register.success.title")}</h2>
                    <p>{t("register.success.message")}</p>
                    <Button type="button" size="lg" onClick={() => nav(loginTarget)}>
                        {t("register.success.button")}
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="page-shell register-page">
            {header}

            <form className="register-form card" onSubmit={onSubmit} noValidate>
                <div className="register-grid">
                    <div className="register-column">
                        <div className="form-field">
                            <label className="input-label" htmlFor="field-email">
                                {t("register.email")}
                                <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input
                                id="field-email"
                                type="email"
                                className={classNames("input-control", touched.email && errors.email && "input-control--error")}
                                value={form.email}
                                onChange={handleTextChange("email")}
                                onBlur={() => markTouched("email")}
                                placeholder="correo@ejemplo.com"
                            />
                            {touched.email && errors.email && (
                                <p className="form-field__text form-field__text--error">{errors.email}</p>
                            )}
                        </div>

                        <div className="form-field">
                            <label className="input-label" htmlFor="field-username">
                                {t("register.username")}
                                <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input
                                id="field-username"
                                type="text"
                                className={classNames("input-control", touched.username && errors.username && "input-control--error")}
                                value={form.username}
                                onChange={handleTextChange("username")}
                                onBlur={() => markTouched("username")}
                                placeholder="gogether_user"
                            />
                            {touched.username && errors.username && (
                                <p className="form-field__text form-field__text--error">{errors.username}</p>
                            )}
                        </div>

                        <div className="form-field">
                            <label className="input-label" htmlFor="field-password">
                                {t("register.password")}
                                <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <div className="input-with-addon">
                                <input
                                    id="field-password"
                                    type={showPassword ? "text" : "password"}
                                className={classNames("input-control", touched.password && errors.password && "input-control--error")}
                                value={form.password}
                                onChange={handleTextChange("password")}
                                onBlur={() => markTouched("password")}
                                placeholder={t("register.passwordPlaceholder")}
                            />
                                <button
                                    type="button"
                                    className="input-addon"
                                    onClick={() => setShowPassword((prev) => !prev)}
                                    aria-label={showPassword ? t("register.password.hide") : t("register.password.show")}
                                >
                                    {showPassword ? t("register.password.hide") : t("register.password.show")}
                                </button>
                            </div>
                            {touched.password && errors.password && (
                                <p className="form-field__text form-field__text--error">{errors.password}</p>
                            )}

                            <div className={classNames("password-strength", `password-strength--${passwordStrength.status}`)}>
                                <div className="password-strength__header">
                                    <span>{t("register.password.strength.label")}</span>
                                    {passwordStrength.status !== "empty" && (
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

                        <div className="form-field">
                            <label className="input-label" htmlFor="field-confirm">
                                {t("register.confirmPassword")}
                                <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <div className="input-with-addon">
                                <input
                                    id="field-confirm"
                                    type={showConfirmPassword ? "text" : "password"}
                                    className={classNames(
                                        "input-control",
                                        touched.confirmPassword && errors.confirmPassword && "input-control--error"
                                    )}
                                    value={form.confirmPassword}
                                    onChange={handleTextChange("confirmPassword")}
                                    onBlur={() => markTouched("confirmPassword")}
                                    placeholder={t("register.confirmPasswordPlaceholder")}
                                />
                                <button
                                    type="button"
                                    className="input-addon"
                                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                                    aria-label={
                                        showConfirmPassword
                                            ? t("register.password.hide")
                                            : t("register.password.show")
                                    }
                                >
                                    {showConfirmPassword ? t("register.password.hide") : t("register.password.show")}
                                </button>
                            </div>
                            {touched.confirmPassword && errors.confirmPassword && (
                                <p className="form-field__text form-field__text--error">{errors.confirmPassword}</p>
                            )}
                        </div>

                    </div>

                    <div className="register-column">
                        <div className="register-row">
                            <div className="form-field">
                                <label className="input-label" htmlFor="field-firstName">
                                    {t("register.firstName")}
                                    <span className="required-indicator" aria-hidden="true">*</span>
                                </label>
                                <input
                                    id="field-firstName"
                                    type="text"
                                    className={classNames(
                                        "input-control",
                                        touched.firstName && errors.firstName && "input-control--error"
                                    )}
                                    value={form.firstName}
                                    onChange={handleTextChange("firstName")}
                                    onBlur={() => markTouched("firstName")}
                                    placeholder={t("register.firstNamePlaceholder")}
                                />
                                {touched.firstName && errors.firstName && (
                                    <p className="form-field__text form-field__text--error">{errors.firstName}</p>
                                )}
                            </div>

                            <div className="form-field">
                                <label className="input-label" htmlFor="field-lastName">
                                    {t("register.lastName")}
                                    <span className="required-indicator" aria-hidden="true">*</span>
                                </label>
                                <input
                                    id="field-lastName"
                                    type="text"
                                    className={classNames(
                                        "input-control",
                                        touched.lastName && errors.lastName && "input-control--error"
                                    )}
                                    value={form.lastName}
                                    onChange={handleTextChange("lastName")}
                                    onBlur={() => markTouched("lastName")}
                                    placeholder={t("register.lastNamePlaceholder")}
                                />
                                {touched.lastName && errors.lastName && (
                                    <p className="form-field__text form-field__text--error">{errors.lastName}</p>
                                )}
                            </div>
                        </div>

                        <SingleSelectField
                            label={t("register.career")}
                            name="career"
                            placeholder={t("register.searchPlaceholder")}
                            value={form.career}
                            onChange={(option) => {
                                setForm((prev) => ({ ...prev, career: option }));
                                markTouched("career");
                            }}
                            fetcher={searchCareers}
                            error={errors.career}
                            touched={touched.career}
                        />

                        <SingleSelectField
                            label={t("register.university")}
                            name="university"
                            placeholder={t("register.searchPlaceholder")}
                            value={form.university}
                            onChange={(option) => {
                                setForm((prev) => ({ ...prev, university: option }));
                                markTouched("university");
                            }}
                            fetcher={searchUniversities}
                            error={errors.university}
                            touched={touched.university}
                        />

                        <MultiSelectField
                            label={t("register.interests")}
                            name="interests"
                            placeholder={t("register.interestsPlaceholder")}
                            selected={form.interests}
                            onChange={(options) => {
                                setForm((prev) => ({ ...prev, interests: options }));
                                markTouched("interests");
                            }}
                            fetcher={searchInterests}
                            helper={t("register.interests.helper")}
                            error={errors.interests}
                            touched={touched.interests}
                        />

                    </div>
                </div>

                <div className="form-actions">
                    <Button type="submit" size="lg" fullWidth disabled={registering}>
                        {t("register.submit")}
                    </Button>
                    {registerErrorKey && (
                        <p className="form-field__text form-field__text--error" role="alert">
                            {t(registerErrorKey)}
                        </p>
                    )}
                    <div className="form-footnote">
                        <span>{t("register.have.account")}</span>
                        <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            onClick={() => nav(loginTarget)}
                            className="form-footnote__link"
                        >
                            {t("register.login")}
                        </Button>
                    </div>
                </div>
            </form>
        </div>
    );
}

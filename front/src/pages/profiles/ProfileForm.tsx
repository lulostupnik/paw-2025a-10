import { useEffect, useMemo, useRef, useState, type FormEvent } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import type { ProfileDetail } from "@/types/profile";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import SingleSelectAutocomplete from "@/components/profiles/SingleSelectAutocomplete";
import { useQuery } from "@tanstack/react-query";
import { listCareers } from "@/lib/api/careers";
import { listUniversities } from "@/lib/api/universities";
import { emptyPage } from "@/types/pagination";
import { useToast } from "@/components/ui/ToastProvider";
import { sanitizeInternalPath } from "@/lib/utils/internalPath";
import { apiErrorMessage } from "@/lib/api/client";
import PageStatus from "@/components/ui/PageStatus";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";

interface ProfileFormState {
    firstName: string;
    lastName: string;
    username: string;
}

interface Option {
    id: number;
    name: string;
}

interface ProfileEditFormProps {
    profileId: string;
    profile: ProfileDetail;
    universities: Option[];
    careers: Option[];
    hasOptionsError: boolean;
}

const PROFILE_LIMITS = {
    username: { min: 2, max: 50 },
    firstName: { min: 2, max: 100 },
    lastName: { min: 2, max: 100 },
} as const;

export default function ProfileForm() {
    const { t } = useI18n();
    const { profileId = "me" } = useParams();
    const { data: profile, isLoading, isError, infoLoading } = useProfileDetail(profileId);

    const universitiesQuery = useQuery({
        queryKey: ["profileUniversities"],
        queryFn: async ({ signal }) => listUniversities({ page: 1, size: 200 }, signal),
    });
    const careersQuery = useQuery({
        queryKey: ["profileCareers"],
        queryFn: async ({ signal }) => listCareers({ page: 1, size: 200 }, signal),
    });

    const universities = useMemo(
        () => (universitiesQuery.data ?? emptyPage()).content.map((item) => ({ id: item.id, name: item.name })),
        [universitiesQuery.data]
    );
    const careers = useMemo(
        () => (careersQuery.data ?? emptyPage()).content.map((item) => ({ id: item.id, name: item.name })),
        [careersQuery.data]
    );

    // Se espera al perfil y a las opciones antes de montar el form: así arranca
    // precargado sin sincronizar el estado con un efecto.
    if (isLoading || infoLoading || universitiesQuery.isLoading || careersQuery.isLoading) {
        return <PageStatus className="profile-form-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !profile) {
        return (
            <div className="profile-form-page">
                <div className="error-container">{t("profile.not.found")}</div>
            </div>
        );
    }

    return (
        <ProfileEditForm
            key={profile.id}
            profileId={profileId}
            profile={profile}
            universities={universities}
            careers={careers}
            hasOptionsError={Boolean(universitiesQuery.isError || careersQuery.isError)}
        />
    );
}

function ProfileEditForm({ profileId, profile, universities, careers, hasOptionsError }: ProfileEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const { showToast } = useToast();
    const { updateProfile, isLoading: isSaving } = useProfileUpsert();
    const [form, setForm] = useState<ProfileFormState>({
        firstName: profile.firstname,
        lastName: profile.lastname,
        username: profile.username,
    });
    const [touched, setTouched] = useState({ firstName: false, lastName: false, username: false });
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [selectedUniversity, setSelectedUniversity] = useState<Option | null>(
        () => universities.find((item) => item.name === profile.university?.name) ?? null
    );
    const [selectedCareer, setSelectedCareer] = useState<Option | null>(
        () => careers.find((item) => item.name === profile.career?.name) ?? null
    );
    const formRef = useRef<HTMLFormElement>(null);
    const returnPathKey = `profile:return:${profileId}`;
    const fromState = sanitizeInternalPath((location.state as { from?: string } | null)?.from);
    const returnPath = fromState ?? sanitizeInternalPath(sessionStorage.getItem(returnPathKey));

    useEffect(() => {
        if (fromState) {
            sessionStorage.setItem(returnPathKey, fromState);
        }
    }, [fromState, returnPathKey]);

    const errors = useMemo(
        () => ({
            firstName: !form.firstName.trim()
                ? t("NotNull.createUserForm.firstName", { defaultValue: "Campo obligatorio." })
                : form.firstName.trim().length < PROFILE_LIMITS.firstName.min || form.firstName.trim().length > PROFILE_LIMITS.firstName.max
                  ? t("profile.validation.firstName.length")
                  : "",
            lastName: !form.lastName.trim()
                ? t("NotNull.createUserForm.lastName", { defaultValue: "Campo obligatorio." })
                : form.lastName.trim().length < PROFILE_LIMITS.lastName.min || form.lastName.trim().length > PROFILE_LIMITS.lastName.max
                  ? t("profile.validation.lastName.length")
                  : "",
            username: !form.username.trim()
                ? t("NotNull.createUserForm.username", { defaultValue: "Campo obligatorio." })
                : form.username.trim().length < PROFILE_LIMITS.username.min || form.username.trim().length > PROFILE_LIMITS.username.max
                  ? t("profile.validation.username.length")
                  : "",
        }),
        [form, t]
    );

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ firstName: true, lastName: true, username: true });
        setSubmitError(null);
        if (errors.firstName || errors.lastName || errors.username) {
            focusFirstInvalidField(formRef.current ?? event.currentTarget);
            return;
        }
        try {
            await updateProfile({
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                username: form.username.trim(),
                universityId: selectedUniversity?.id,
                careerId: selectedCareer?.id,
            });
            showToast(t("profile.toast.updated"), { variant: "success" });
            navigate(returnPath ?? `/profiles/${profile.id}/info`, { replace: true });
        } catch (err) {
            setSubmitError(
                apiErrorMessage(
                    err,
                    err instanceof Error
                        ? err.message
                        : t("admin.dashboard.error", { defaultValue: "Error guardando los cambios." })
                )
            );
        }
    };

    const handleBack = () => {
        navigate(returnPath ?? `/profiles/${profile.id}/info`, { replace: true });
    };

    return (
        <div className="profile-form-page">
            <div className="auth-container">
                <div className="auth-card profile-edit-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("profile.edit.title")}</h1>
                        <p className="auth-subtitle">{t("profile.edit.subtitle")}</p>
                    </div>

                    <form ref={formRef} className="auth-form profile-edit-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-section">
                            <h3 className="section-title">{t("profile.edit.personal.info")}</h3>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="firstName" className="form-label required-field">
                                        {t("profile.firstname")}
                                    </label>
                                    <input
                                        id="firstName"
                                        className={`form-input required ${touched.firstName && errors.firstName ? "error" : ""}`}
                                        aria-invalid={touched.firstName && errors.firstName ? true : undefined}
                                        value={form.firstName}
                                        onChange={(event) => setForm((prev) => ({ ...prev, firstName: event.target.value }))}
                                        onBlur={() => setTouched((prev) => ({ ...prev, firstName: true }))}
                                    />
                                    <p className={`character-counter ${form.firstName.trim().length > PROFILE_LIMITS.firstName.max ? "is-error" : ""}`}>
                                        {form.firstName.trim().length}/{PROFILE_LIMITS.firstName.max}
                                    </p>
                                    {touched.firstName && errors.firstName && <p className="error-message">{errors.firstName}</p>}
                                </div>

                                <div className="form-group">
                                    <label htmlFor="lastName" className="form-label required-field">
                                        {t("profile.lastname")}
                                    </label>
                                    <input
                                        id="lastName"
                                        className={`form-input required ${touched.lastName && errors.lastName ? "error" : ""}`}
                                        aria-invalid={touched.lastName && errors.lastName ? true : undefined}
                                        value={form.lastName}
                                        onChange={(event) => setForm((prev) => ({ ...prev, lastName: event.target.value }))}
                                        onBlur={() => setTouched((prev) => ({ ...prev, lastName: true }))}
                                    />
                                    <p className={`character-counter ${form.lastName.trim().length > PROFILE_LIMITS.lastName.max ? "is-error" : ""}`}>
                                        {form.lastName.trim().length}/{PROFILE_LIMITS.lastName.max}
                                    </p>
                                    {touched.lastName && errors.lastName && <p className="error-message">{errors.lastName}</p>}
                                </div>
                            </div>

                            <div className="form-group">
                                <label htmlFor="username" className="form-label required-field">
                                    {t("profile.username")}
                                </label>
                                <input
                                    id="username"
                                    className={`form-input required ${touched.username && errors.username ? "error" : ""}`}
                                    aria-invalid={touched.username && errors.username ? true : undefined}
                                    value={form.username}
                                    onChange={(event) => setForm((prev) => ({ ...prev, username: event.target.value }))}
                                    onBlur={() => setTouched((prev) => ({ ...prev, username: true }))}
                                />
                                <p className={`character-counter ${form.username.trim().length > PROFILE_LIMITS.username.max ? "is-error" : ""}`}>
                                    {form.username.trim().length}/{PROFILE_LIMITS.username.max}
                                </p>
                                {touched.username && errors.username && <p className="error-message">{errors.username}</p>}
                            </div>
                        </div>

                        <div className="form-section">
                            <h3 className="section-title">{t("profile.edit.academic.info")}</h3>

                            <div className="form-group">
                                <label htmlFor="originUniversity" className="form-label">
                                    {t("university.detail.title")}
                                </label>
                                <SingleSelectAutocomplete
                                    id="originUniversity"
                                    options={universities}
                                    value={selectedUniversity}
                                    placeholder={t("createJourney.originUniversity.search", { defaultValue: "Type to search..." })}
                                    onChange={setSelectedUniversity}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="career" className="form-label">
                                    {t("profile.career")}
                                </label>
                                <SingleSelectAutocomplete
                                    id="career"
                                    options={careers}
                                    value={selectedCareer}
                                    placeholder={t("event.career.search", { defaultValue: "Type to search..." })}
                                    onChange={setSelectedCareer}
                                />
                            </div>
                        </div>
                        {hasOptionsError && (
                            <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                        )}
                        {submitError && (
                            <p className="error-message">{submitError}</p>
                        )}

                        <div className="form-actions">
                            <button type="submit" className="form-button" disabled={isSaving}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                                    <polyline points="17,21 17,13 7,13 7,21"></polyline>
                                    <polyline points="7,3 7,8 15,8"></polyline>
                                </svg>
                                {t("profile.save.changes")}
                            </button>
                        </div>
                    </form>

                    <div className="auth-footer">
                        <button type="button" className="auth-link auth-link-button" onClick={handleBack}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M19 12H5"></path>
                                <path d="M12 19l-7-7 7-7"></path>
                            </svg>
                            {t("profile.back.to.profile")}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

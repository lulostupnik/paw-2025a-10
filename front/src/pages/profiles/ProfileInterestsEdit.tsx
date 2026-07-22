import { apiErrorMessage } from "@/lib/api/client";
import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import { useProfileInterests } from "@/hooks/profiles/useProfileInterests";
import { searchInterests, type CatalogOption } from "@/lib/api/catalog";
import { useToast } from "@/components/ui/ToastProvider";
import { useBackNavigation } from "@/lib/navigation";
import PageStatus from "@/components/ui/PageStatus";
import InterestMultiSelectField from "@/components/form/InterestMultiSelectField";

export default function ProfileInterestsEdit() {
    const { t } = useI18n();
    const { data: profile, isLoading, isError } = useProfileDetail("me");
    const userInterestsQuery = useProfileInterests({ profileId: "me", page: 1, size: 200, enabled: Boolean(profile) });

    if (isLoading || userInterestsQuery.isLoading) {
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
        <InterestsForm
            initialSelected={userInterestsQuery.data.content.map((interest) => ({ id: interest.id, name: interest.name }))}
            hasLoadError={userInterestsQuery.isError}
        />
    );
}

interface InterestsFormProps {
    initialSelected: CatalogOption[];
    hasLoadError: boolean;
}

function InterestsForm({ initialSelected, hasLoadError }: InterestsFormProps) {
    const { t } = useI18n();
    const { from, goBack } = useBackNavigation();
    const { showToast } = useToast();
    const { updateInterests, isLoading: isSaving } = useProfileUpsert();
    const [selected, setSelected] = useState<CatalogOption[]>(initialSelected);
    const [submitError, setSubmitError] = useState<string | null>(null);

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            setSubmitError(null);
            await updateInterests(selected.map((interest) => interest.id));
            showToast(t("profile.toast.interestsUpdated"), { variant: "success" });
            goBack("/profiles/me/interests", { replace: true });
        } catch (error) {
            console.error("Failed to update interests", error);
            setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        }
    };

    return (
        <div className="profile-form-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("editInterests.title")}</h1>
                        <p className="auth-subtitle">{t("editInterests.subtitle")}</p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <InterestMultiSelectField
                            label={t("event.interest")}
                            name="interests"
                            placeholder={t("event.interest.search", { defaultValue: "Search interests..." })}
                            selected={selected}
                            onChange={setSelected}
                            fetcher={searchInterests}
                            helper={t("register.interests.helper")}
                        />

                        <button type="submit" className="form-button" disabled={isSaving}>
                            {t("editInterests.submit")}
                        </button>
                    </form>
                    {submitError && <p className="error-message">{submitError}</p>}
                    {hasLoadError && (
                        <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}

                    <div className="auth-footer">
                        <Link className="auth-link" to={from ?? "/profiles/me/interests"}>
                            {t("interests.back", { defaultValue: "Back to Interests" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

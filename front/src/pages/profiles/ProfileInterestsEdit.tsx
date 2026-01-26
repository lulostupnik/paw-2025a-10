import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileUpsert } from "@/hooks/profiles/useProfileUpsert";
import type { ProfileInterest } from "@/types/profile";
import { classNames } from "@/lib/utils/classNames";
import { useQuery } from "@tanstack/react-query";
import { listInterests } from "@/lib/api/interests";
import { emptyPage } from "@/types/pagination";

export default function ProfileInterestsEdit() {
    const { t } = useI18n();
    const { data: profile, isLoading, isError } = useProfileDetail("me");
    const { updateInterests, isLoading: isSaving } = useProfileUpsert();
    const [query, setQuery] = useState("");
    const [selected, setSelected] = useState<ProfileInterest[]>([]);
    const [open, setOpen] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    useEffect(() => {
        if (profile) {
            setSelected(profile.interests);
        }
    }, [profile]);

    const interestsQuery = useQuery({
        queryKey: ["profileInterestsOptions"],
        queryFn: async ({ signal }) => listInterests({ page: 1, size: 200 }, signal),
    });
    const options = useMemo(
        () => (interestsQuery.data ?? emptyPage()).content.map((item) => ({ id: item.id, name: item.name })),
        [interestsQuery.data]
    );
    const filtered = useMemo(() => {
        const normalized = query.trim().toLowerCase();
        if (!normalized) {
            return options;
        }
        return options.filter((item) => item.name.toLowerCase().includes(normalized));
    }, [options, query]);

    const toggleInterest = (interest: ProfileInterest) => {
        setSelected((prev) => {
            const exists = prev.some((item) => item.id === interest.id);
            if (exists) {
                return prev.filter((item) => item.id !== interest.id);
            }
            return [...prev, interest];
        });
    };

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            setSubmitError(null);
            await updateInterests(selected.map((interest) => interest.id));
        } catch (error) {
            console.error("Failed to update interests", error);
            setSubmitError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
        }
    };

    if (isLoading) {
        return <div className="profile-form-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError || !profile) {
        return (
            <div className="profile-form-page">
                <div className="error-container">{t("profile.not.found")}</div>
            </div>
        );
    }

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
                        <div className="form-group">
                            <label htmlFor="interestSearch" className="form-label required-field">
                                {t("event.interest")}
                            </label>

                            <div className="autocomplete-wrapper">
                                <input
                                    id="interestSearch"
                                    type="text"
                                    className="autocomplete-input"
                                    placeholder={t("event.interest.search", { defaultValue: "Search interests..." })}
                                    value={query}
                                    onFocus={() => setOpen(true)}
                                    onBlur={() => setOpen(false)}
                                    onChange={(event) => setQuery(event.target.value)}
                                />

                                <div className="autocomplete-dropdown" style={{ display: open ? "block" : "none" }}>
                                    {filtered.map((interest) => {
                                        const isSelected = selected.some((item) => item.id === interest.id);
                                        return (
                                            <div
                                                key={interest.id}
                                                className={classNames("autocomplete-item", isSelected && "selected")}
                                                onMouseDown={(event) => event.preventDefault()}
                                                onClick={() => toggleInterest(interest)}
                                            >
                                                {interest.name}
                                            </div>
                                        );
                                    })}
                                </div>

                                <div className="selected-tags required-selected-tags">
                                    {selected.map((interest) => (
                                        <span key={interest.id} className="selected-tag">
                                            {interest.name}
                                            <button
                                                type="button"
                                                className="tag-remove"
                                                aria-label="Remove"
                                                onClick={() => toggleInterest(interest)}
                                            >
                                                ×
                                            </button>
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>

                        <button type="submit" className="form-button" disabled={isSaving}>
                            {t("editInterests.submit")}
                        </button>
                    </form>
                    {submitError && <p className="error-message">{submitError}</p>}
                    {interestsQuery.isError && (
                        <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}

                    <div className="auth-footer">
                        <Link className="auth-link" to="/profiles/me/interests">
                            {t("interests.back", { defaultValue: "Back to Interests" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

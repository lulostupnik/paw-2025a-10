import { apiErrorMessage } from "@/lib/api/client";
import { useCallback, useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminUniversityDetailData } from "@/hooks/useAdminDetailData";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { updateUniversity } from "@/lib/api/universities";
import { listCities } from "@/lib/api/cities";
import { emptyPage } from "@/types/pagination";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";

interface UniversityFormState {
    name: string;
    abbreviation: string;
    city: string;
}

interface UniversityEditFormProps {
    id: string;
    university: { name: string; abbreviation: string; city: { name: string } };
}

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

const API_FIELD_TO_FORM_FIELD: Record<string, keyof UniversityFormState> = {
    name: "name",
    abbreviation: "abbreviation",
    cityId: "city",
};

export default function UniversityEditPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data: university, isLoading, isError } = useAdminUniversityDetailData({ id });

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <PageStatus className="entity-create-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !university || !id) {
        return <PageStatus className="entity-create-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    // El form se monta recién con la universidad cargada, así arranca precargado
    // sin tener que sincronizar el estado con un efecto.
    return <UniversityEditForm key={id} id={id} university={university} />;
}

function UniversityEditForm({ id, university }: UniversityEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const cityName = university.city?.name ?? "";
    const [form, setForm] = useState<UniversityFormState>({
        name: university.name ?? "",
        abbreviation: university.abbreviation ?? "",
        city: cityName,
    });
    const [touched, setTouched] = useState({ name: false, abbreviation: false, city: false });
    const [cityQuery, setCityQuery] = useState(cityName);
    const [selectedCity, setSelectedCity] = useState<string | null>(cityName || null);
    const [cityOpen, setCityOpen] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<Partial<Record<keyof UniversityFormState, string>>>({});
    const [submitting, setSubmitting] = useState(false);

    const citiesQuery = useQuery({
        queryKey: ["citiesOptions"],
        queryFn: async ({ signal }) => listCities({ page: 1, size: 200 }, signal),
    });
    const cities = useMemo(
        () => (citiesQuery.data ?? emptyPage()).content.map((city) => ({ id: city.id, name: city.name })),
        [citiesQuery.data]
    );

    const filteredCities = useMemo(() => {
        const query = cityQuery.trim().toLowerCase();
        if (!query) {
            return cities;
        }
        return cities.filter((city) => city.name.toLowerCase().includes(query));
    }, [cities, cityQuery]);

    const resolveCityId = useCallback(
        (name: string) =>
            cities.find((c) => c.name.trim().toLowerCase() === name.trim().toLowerCase())?.id ?? null,
        [cities]
    );

    const clientErrors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createUniversity.name", { defaultValue: "Campo obligatorio." }),
            abbreviation: form.abbreviation.trim()
                ? ""
                : t("NotNull.createUniversity.abbreviation", { defaultValue: "Campo obligatorio." }),
            city: !form.city.trim()
                ? t("NotNull.createUniversity.city", { defaultValue: "Campo obligatorio." })
                : resolveCityId(form.city) != null
                    ? ""
                    : t("ExistingCity.createUniversityForm.city", { defaultValue: "Esta ciudad no está entre nuestras opciones" }),
        }),
        [form, t, resolveCityId]
    );

    const errors = useMemo(
        () => ({
            name: clientErrors.name || serverErrors.name || "",
            abbreviation: clientErrors.abbreviation || serverErrors.abbreviation || "",
            city: clientErrors.city || serverErrors.city || "",
        }),
        [clientErrors, serverErrors]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const formElement = event.currentTarget;
        setTouched({ name: true, abbreviation: true, city: true });
        setServerErrors({});
        if (clientErrors.name || clientErrors.abbreviation || clientErrors.city) {
            focusFirstInvalidField(formElement);
            return;
        }
        const cityId = resolveCityId(form.city);
        if (cityId == null) {
            focusFirstInvalidField(formElement);
            return;
        }
        setSubmitting(true);
        setSubmitError(null);
        updateUniversity(id, {
            name: form.name.trim(),
            abbreviation: form.abbreviation.trim(),
            cityId,
        })
            .then(async () => {
                await invalidateAdminEntityQueries(queryClient, "university", id);
                showToast(t("admin.toast.updated"), { variant: "success" });
                navigate(`/universities/${id}`);
            })
            .catch((error) => {
                console.error("Failed to update university", error);
                const nextServerErrors = mapApiFieldErrors(error, API_FIELD_TO_FORM_FIELD);
                if (Object.keys(nextServerErrors).length > 0) {
                    setServerErrors(nextServerErrors);
                    focusFirstInvalidField(formElement);
                } else {
                    setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                }
            })
            .finally(() => setSubmitting(false));
    };

    const handleCitySelect = (value: string) => {
        setServerErrors((prev) => ({ ...prev, city: undefined }));
        setSubmitError(null);
        setSelectedCity(value);
        setCityQuery(value);
        setForm((prev) => ({ ...prev, city: value }));
        setCityOpen(false);
    };

    const clearCity = () => {
        setServerErrors((prev) => ({ ...prev, city: undefined }));
        setSubmitError(null);
        setSelectedCity(null);
        setCityQuery("");
        setForm((prev) => ({ ...prev, city: "" }));
        setCityOpen(false);
    };

    return (
        <div className="entity-create-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <div className="auth-logo">
                            <svg xmlns="http://www.w3.org/2000/svg" className="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                            </svg>
                        </div>
                        <h1 className="auth-title">{t("updateUniversity.title")}</h1>
                        <p className="auth-subtitle">
                            {t("updateUniversity.subtitle", { defaultValue: "Update university information" })}
                        </p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-group">
                            <label htmlFor="university-name" className="form-label required-field">
                                {t("createUniversity.name")}
                            </label>
                            <input
                                id="university-name"
                                type="text"
                                className={`form-input ${touched.name && errors.name ? "error" : ""}`}
                                value={form.name}
                                onChange={(event) => {
                                    setServerErrors((prev) => ({ ...prev, name: undefined }));
                                    setSubmitError(null);
                                    setForm((prev) => ({ ...prev, name: event.target.value }));
                                }}
                                onBlur={() => {
                                    setTouched((prev) => ({ ...prev, name: true }));
                                    setForm((prev) => ({ ...prev, name: formatTitleCase(prev.name) }));
                                }}
                                required
                            />
                            {touched.name && errors.name && <p className="error-message">{errors.name}</p>}
                        </div>

                        <div className="form-group">
                            <label htmlFor="university-abbreviation" className="form-label required-field">
                                {t("createUniversity.abbreviation")}
                            </label>
                            <input
                                id="university-abbreviation"
                                type="text"
                                className={`form-input ${touched.abbreviation && errors.abbreviation ? "error" : ""}`}
                                value={form.abbreviation}
                                onChange={(event) => {
                                    setServerErrors((prev) => ({ ...prev, abbreviation: undefined }));
                                    setSubmitError(null);
                                    setForm((prev) => ({ ...prev, abbreviation: event.target.value }));
                                }}
                                onBlur={() => setTouched((prev) => ({ ...prev, abbreviation: true }))}
                                required
                            />
                            {touched.abbreviation && errors.abbreviation && (
                                <p className="error-message">{errors.abbreviation}</p>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="university-city" className="form-label required-field">
                                {t("createUniversity.city")}
                            </label>
                            <div className="autocomplete-wrapper">
                                <input
                                    id="university-city"
                                    type="text"
                                    className={`form-input autocomplete-input ${touched.city && errors.city ? "error" : ""}`}
                                    value={cityQuery}
                                    placeholder={t("createUniversity.city.search")}
                                    onChange={(event) => {
                                        const value = event.target.value;
                                        setServerErrors((prev) => ({ ...prev, city: undefined }));
                                        setSubmitError(null);
                                        setCityQuery(value);
                                        setForm((prev) => ({ ...prev, city: value }));
                                        setSelectedCity(null);
                                        setCityOpen(true);
                                    }}
                                    onFocus={() => setCityOpen(true)}
                                    onBlur={() => {
                                        setTouched((prev) => ({ ...prev, city: true }));
                                        window.setTimeout(() => setCityOpen(false), 100);
                                    }}
                                    required
                                    autoComplete="off"
                                />
                                <div className={`autocomplete-dropdown ${cityOpen ? "is-open" : ""}`} role="listbox">
                                    {filteredCities.length === 0 && (
                                        <div className="autocomplete-item no-results">
                                            {t("autocomplete.empty", { defaultValue: "No encontramos coincidencias." })}
                                        </div>
                                    )}
                                    {filteredCities.map((city) => (
                                        <div
                                            key={city.id}
                                            role="option"
                                            className={`autocomplete-item ${selectedCity === city.name ? "selected" : ""}`}
                                            onMouseDown={(event) => event.preventDefault()}
                                            onClick={() => handleCitySelect(city.name)}
                                        >
                                            {city.name}
                                        </div>
                                    ))}
                                </div>
                                {selectedCity && (
                                    <div className="selected-tags">
                                        <span className="selected-tag">
                                            {selectedCity}
                                            <button
                                                type="button"
                                                className="tag-remove"
                                                onClick={clearCity}
                                                aria-label={t("autocomplete.clear", { defaultValue: "Limpiar seleccion" })}
                                            >
                                                ×
                                            </button>
                                        </span>
                                    </div>
                                )}
                            </div>
                            {touched.city && errors.city && <p className="error-message">{errors.city}</p>}
                        </div>

                        <button type="submit" className="form-button" disabled={submitting}>
                            {t("updateUniversity.submit", { defaultValue: "Update University" })}
                        </button>
                    </form>
                    {citiesQuery.isError && (
                        <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to={`/universities/${id}`} className="auth-link">
                            {t("university.back", { defaultValue: "Back to universities" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

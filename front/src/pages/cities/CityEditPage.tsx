import { apiErrorMessage } from "@/lib/api/client";
import { useCallback, useMemo, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminCityDetailData } from "@/hooks/useAdminDetailData";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { updateCity } from "@/lib/api/cities";
import { listCountries, type CountryDto } from "@/lib/api/countries";
import { useNavigate } from "react-router-dom";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";

interface CityFormState {
    name: string;
    country: string;
}

interface CityEditFormProps {
    id: string;
    city: { name: string; country: string };
}

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

// Campo del ErrorDto de la API → campo del formulario.
const API_FIELD_TO_FORM_FIELD: Record<string, keyof CityFormState> = {
    name: "name",
    countryId: "country",
};

export default function CityEditPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data: city, isLoading, isError } = useAdminCityDetailData({ id });

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <PageStatus className="entity-create-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !city || !id) {
        return <PageStatus className="entity-create-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    // El form se monta recién con la ciudad cargada, así arranca precargado sin
    // tener que sincronizar el estado con un efecto.
    return <CityEditForm key={id} id={id} city={city} />;
}

function CityEditForm({ id, city }: CityEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const [form, setForm] = useState<CityFormState>({ name: city.name ?? "", country: city.country ?? "" });
    const [touched, setTouched] = useState({ name: false, country: false });
    const [countryQuery, setCountryQuery] = useState(city.country ?? "");
    const [selectedCountry, setSelectedCountry] = useState<string | null>(city.country || null);
    const [countryOpen, setCountryOpen] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<Partial<Record<keyof CityFormState, string>>>({});
    const [submitting, setSubmitting] = useState(false);

    const countriesQuery = useQuery({
        queryKey: ["countries"],
        queryFn: ({ signal }) => listCountries(signal),
    });
    const countries = useMemo(() => countriesQuery.data ?? [], [countriesQuery.data]);

    const filteredCountries = useMemo(() => {
        const query = countryQuery.trim().toLowerCase();
        if (!query) {
            return countries;
        }
        return countries.filter((country) => country.name.toLowerCase().includes(query));
    }, [countries, countryQuery]);

    // The body references the country by its id, so the typed/selected name is
    // resolved to the country's id at submit time using the loaded list.
    const resolveCountryId = useCallback(
        (name: string) =>
            countries.find((c) => c.name.trim().toLowerCase() === name.trim().toLowerCase())?.id ?? null,
        [countries]
    );

    const clientErrors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createCity.name", { defaultValue: "Campo obligatorio." }),
            country: !form.country.trim()
                ? t("NotNull.createCity.country", { defaultValue: "Campo obligatorio." })
                : resolveCountryId(form.country) != null
                    ? ""
                    : t("ExistingCountry.createCityForm.country", { defaultValue: "Este país no está entre nuestras opciones" }),
        }),
        [form, t, resolveCountryId]
    );

    const errors = useMemo(
        () => ({
            name: clientErrors.name || serverErrors.name || "",
            country: clientErrors.country || serverErrors.country || "",
        }),
        [clientErrors, serverErrors]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true, country: true });
        setServerErrors({});
        if (clientErrors.name || clientErrors.country) {
            return;
        }
        const countryId = resolveCountryId(form.country);
        if (countryId == null) {
            return;
        }
        setSubmitting(true);
        setSubmitError(null);
        updateCity(id, { name: form.name.trim(), countryId })
            .then(async () => {
                await invalidateAdminEntityQueries(queryClient, "city", id);
                showToast(t("admin.toast.updated"), { variant: "success" });
                navigate(`/cities/${id}`);
            })
            .catch((error) => {
                console.error("Failed to update city", error);
                const nextServerErrors = mapApiFieldErrors(error, API_FIELD_TO_FORM_FIELD);
                if (Object.keys(nextServerErrors).length > 0) {
                    setServerErrors(nextServerErrors);
                } else {
                    setSubmitError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                }
            })
            .finally(() => setSubmitting(false));
    };

    const handleCountrySelect = (country: CountryDto) => {
        setServerErrors((prev) => ({ ...prev, country: undefined }));
        setSubmitError(null);
        setSelectedCountry(country.name);
        setCountryQuery(country.name);
        setForm((prev) => ({ ...prev, country: country.name }));
        setCountryOpen(false);
    };

    const clearCountry = () => {
        setServerErrors((prev) => ({ ...prev, country: undefined }));
        setSubmitError(null);
        setSelectedCountry(null);
        setCountryQuery("");
        setForm((prev) => ({ ...prev, country: "" }));
        setCountryOpen(false);
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
                        <h1 className="auth-title">{t("editCity.title", { defaultValue: "Edit City" })}</h1>
                        <p className="auth-subtitle">
                            {t("editCity.subtitle", { defaultValue: "Update city information" })}
                        </p>
                    </div>

                    <form className="auth-form" onSubmit={handleSubmit} noValidate>
                        <div className="form-group">
                            <label htmlFor="city-name" className="form-label required-field">
                                {t("createCity.name", { defaultValue: "City Name" })}
                            </label>
                            <input
                                id="city-name"
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
                            <label htmlFor="city-country" className="form-label required-field">
                                {t("createCity.country", { defaultValue: "Country" })}
                            </label>
                            <div className="autocomplete-wrapper">
                                <input
                                    id="city-country"
                                    type="text"
                                    className={`form-input autocomplete-input ${touched.country && errors.country ? "error" : ""}`}
                                    value={countryQuery}
                                    placeholder={t("createCity.country.search")}
                                    onChange={(event) => {
                                        const value = event.target.value;
                                        setServerErrors((prev) => ({ ...prev, country: undefined }));
                                        setSubmitError(null);
                                        setCountryQuery(value);
                                        setForm((prev) => ({ ...prev, country: value }));
                                        setSelectedCountry(null);
                                        setCountryOpen(true);
                                    }}
                                    onFocus={() => setCountryOpen(true)}
                                    onBlur={() => {
                                        setTouched((prev) => ({ ...prev, country: true }));
                                        window.setTimeout(() => setCountryOpen(false), 100);
                                    }}
                                    required
                                    autoComplete="off"
                                />
                                <div className={`autocomplete-dropdown ${countryOpen ? "is-open" : ""}`} role="listbox">
                                    {filteredCountries.length === 0 && (
                                        <div className="autocomplete-item no-results">
                                            {t("autocomplete.empty", { defaultValue: "No encontramos coincidencias." })}
                                        </div>
                                    )}
                                    {filteredCountries.map((country) => (
                                        <div
                                            key={country.id}
                                            role="option"
                                            className={`autocomplete-item ${selectedCountry === country.name ? "selected" : ""}`}
                                            onMouseDown={(event) => event.preventDefault()}
                                            onClick={() => handleCountrySelect(country)}
                                        >
                                            {country.name}
                                        </div>
                                    ))}
                                </div>
                                {selectedCountry && (
                                    <div className="selected-tags">
                                        <span className="selected-tag">
                                            {selectedCountry}
                                            <button
                                                type="button"
                                                className="tag-remove"
                                                onClick={clearCountry}
                                                aria-label={t("autocomplete.clear", { defaultValue: "Limpiar seleccion" })}
                                            >
                                                ×
                                            </button>
                                        </span>
                                    </div>
                                )}
                            </div>
                            {touched.country && errors.country && <p className="error-message">{errors.country}</p>}
                        </div>

                        <button type="submit" className="form-button" disabled={submitting}>
                            {t("editCity.submit", { defaultValue: "Update City" })}
                        </button>
                    </form>
                    {countriesQuery.isError && (
                        <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to={`/cities/${id}`} className="auth-link">
                            {t("city.back", { defaultValue: "Back to cities" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

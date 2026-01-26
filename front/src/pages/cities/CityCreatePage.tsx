import { useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useQuery } from "@tanstack/react-query";
import { createCity, listCities } from "@/lib/api/cities";
import { useNavigate } from "react-router-dom";

interface CityFormState {
    name: string;
    country: string;
}

const initialForm: CityFormState = {
    name: "",
    country: "",
};

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function CityCreatePage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState({ name: false, country: false });
    const [countryQuery, setCountryQuery] = useState("");
    const [selectedCountry, setSelectedCountry] = useState<string | null>(null);
    const [countryOpen, setCountryOpen] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);

    const countriesQuery = useQuery({
        queryKey: ["cityCountries"],
        queryFn: async ({ signal }) => {
            const cities = await listCities({ page: 1, size: 200 }, signal);    //TODO Shouln't we have a GET countries endpoint?
            const unique = Array.from(new Set(cities.content.map((item) => item.country).filter(Boolean)));
            return unique.map((name, index) => ({ id: index + 1, name: name ?? "" }));
        },
    });
    const countries = useMemo(() => countriesQuery.data ?? [], [countriesQuery.data]);

    const filteredCountries = useMemo(() => {
        const query = countryQuery.trim().toLowerCase();
        if (!query) {
            return countries;
        }
        return countries.filter((country) => country.name.toLowerCase().includes(query));
    }, [countries, countryQuery]);

    const errors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createCity.name", { defaultValue: "Campo obligatorio." }),
            country: form.country.trim()
                ? ""
                : t("NotNull.createCity.country", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true, country: true });
        if (errors.name || errors.country) {
            return;
        }
        setSubmitting(true);
        setSubmitError(null);
        createCity({ name: form.name.trim(), country: form.country.trim() })
            .then((created) => navigate(`/cities/${created.id}`))
            .catch((error) => {
                console.error("Failed to create city", error);
                setSubmitError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
            })
            .finally(() => setSubmitting(false));
    };

    const handleCountrySelect = (value: string) => {
        setSelectedCountry(value);
        setCountryQuery(value);
        setForm((prev) => ({ ...prev, country: value }));
        setCountryOpen(false);
    };

    const clearCountry = () => {
        setSelectedCountry(null);
        setCountryQuery("");
        setForm((prev) => ({ ...prev, country: "" }));
        setCountryOpen(false);
    };

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

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
                        <h1 className="auth-title">{t("createCity.title")}</h1>
                        <p className="auth-subtitle">{t("createCity.subtitle", { defaultValue: "Add a new city to the system" })}</p>
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
                                onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
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
                                            onClick={() => handleCountrySelect(country.name)}
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
                            {t("createCity.submit")}
                        </button>
                    </form>
                    {countriesQuery.isError && (
                        <p className="error-message">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}
                    {submitError && <p className="error-message">{submitError}</p>}

                    <div className="auth-footer">
                        <Link to="/admin/cities" className="auth-link">
                            {t("city.back", { defaultValue: "Back to cities" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

import { useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { getCityOptionsMock, type AdminCreateScenario } from "@/mocks/adminCreate.mock";

const SCENARIO: AdminCreateScenario = "normal";

interface UniversityFormState {
    name: string;
    abbreviation: string;
    city: string;
}

const initialForm: UniversityFormState = {
    name: "",
    abbreviation: "",
    city: "",
};

const formatTitleCase = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");

export default function UniversityCreatePage() {
    const { t } = useI18n();
    const [form, setForm] = useState(initialForm);
    const [touched, setTouched] = useState({ name: false, abbreviation: false, city: false });
    const [cityQuery, setCityQuery] = useState("");
    const [selectedCity, setSelectedCity] = useState<string | null>(null);
    const [cityOpen, setCityOpen] = useState(false);

    const cities = useMemo(() => getCityOptionsMock(SCENARIO), [SCENARIO]);

    const filteredCities = useMemo(() => {
        const query = cityQuery.trim().toLowerCase();
        if (!query) {
            return cities;
        }
        return cities.filter((city) => city.name.toLowerCase().includes(query));
    }, [cities, cityQuery]);

    const errors = useMemo(
        () => ({
            name: form.name.trim() ? "" : t("NotNull.createUniversity.name", { defaultValue: "Campo obligatorio." }),
            abbreviation: form.abbreviation.trim()
                ? ""
                : t("NotNull.createUniversity.abbreviation", { defaultValue: "Campo obligatorio." }),
            city: form.city.trim() ? "" : t("NotNull.createUniversity.city", { defaultValue: "Campo obligatorio." }),
        }),
        [form, t]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setTouched({ name: true, abbreviation: true, city: true });
        if (errors.name || errors.abbreviation || errors.city) {
            return;
        }
        // TODO: replace with API call to create university, handle success/error state.
    };

    const handleCitySelect = (value: string) => {
        setSelectedCity(value);
        setCityQuery(value);
        setForm((prev) => ({ ...prev, city: value }));
        setCityOpen(false);
    };

    const clearCity = () => {
        setSelectedCity(null);
        setCityQuery("");
        setForm((prev) => ({ ...prev, city: "" }));
        setCityOpen(false);
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
                        <h1 className="auth-title">{t("createUniversity.title")}</h1>
                        <p className="auth-subtitle">
                            {t("createUniversity.subtitle", { defaultValue: "Add a new university to the system" })}
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
                            <label htmlFor="university-abbreviation" className="form-label required-field">
                                {t("createUniversity.abbreviation")}
                            </label>
                            <input
                                id="university-abbreviation"
                                type="text"
                                className={`form-input ${touched.abbreviation && errors.abbreviation ? "error" : ""}`}
                                value={form.abbreviation}
                                onChange={(event) => setForm((prev) => ({ ...prev, abbreviation: event.target.value }))}
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

                        <button type="submit" className="form-button">
                            {t("createUniversity.submit")}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <Link to="/admin?tab=universities" className="auth-link">
                            {t("university.back", { defaultValue: "Back to universities" })}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

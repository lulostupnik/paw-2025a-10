import { useCallback, useEffect, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import { classNames } from "@/lib/utils/classNames";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { searchUniversities, type CatalogOption } from "@/lib/api/catalog";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import { updateJourney } from "@/lib/api/journeys";
import { useI18n } from "@/lib/i18n";
import { getTodayIsoDate } from "@/lib/utils/date";

interface JourneyFormState {
    startDate: string;
    endDate: string;
    destination: CatalogOption | null;
    description: string;
}

type JourneyField = keyof JourneyFormState;
type JourneyErrors = Partial<Record<JourneyField, string>>;
type JourneyTouched = Partial<Record<JourneyField, boolean>>;

const INITIAL_FORM: JourneyFormState = {
    startDate: "",
    endDate: "",
    destination: null,
    description: "",
};

const addDays = (dateValue: string, days: number) => {
    const [year, month, day] = dateValue.split("-").map(Number);
    if (!year || !month || !day) {
        return "";
    }
    const nextDate = new Date(Date.UTC(year, month - 1, day + days));
    return nextDate.toISOString().slice(0, 10);
};

export default function JourneyEditPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();
    const { data, isLoading, isError, isNotFound } = useJourneyDetailData({ journeyId: id });
    const [form, setForm] = useState<JourneyFormState>({ ...INITIAL_FORM });
    const [errors, setErrors] = useState<JourneyErrors>({});
    const [touched, setTouched] = useState<JourneyTouched>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [destinationQuery, setDestinationQuery] = useState(form.destination?.name ?? "");
    const [seeded, setSeeded] = useState(false);
    const descriptionRef = useRef<HTMLTextAreaElement>(null);

    useEffect(() => {
        if (!data || seeded) {
            return;
        }
        const destinationName = data.destinationUniversity?.name ?? "";
        setForm({
            startDate: data.startDate ?? "",
            endDate: data.endDate ?? "",
            destination: destinationName ? { id: 0, name: destinationName } : null,
            description: data.description ?? "",
        });
        setDestinationQuery(destinationName);
        setSeeded(true);
    }, [data, seeded]);

    const markTouched = useCallback((field: JourneyField) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
    }, []);

    const handleDateChange = (field: "startDate" | "endDate") => (event: ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleDescriptionChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setForm((prev) => ({ ...prev, description: event.target.value }));
    };

    const validate = useCallback(
        (state: JourneyFormState): JourneyErrors => {
            const nextErrors: JourneyErrors = {};
            const today = getTodayIsoDate();
            if (!state.startDate) {
                nextErrors.startDate = t("journey.create.validation.startDate");
            } else if (state.startDate < today) {
                nextErrors.startDate = t("FutureDate.createJourneyForm.startDate");
            }
            if (!state.endDate) {
                nextErrors.endDate = t("journey.create.validation.endDate");
            } else if (state.endDate < today) {
                nextErrors.endDate = t("FutureDate.createJourneyForm.endDate");
            }
            if (!nextErrors.endDate && state.startDate && state.endDate && state.startDate >= state.endDate) {
                nextErrors.endDate = t("journey.create.validation.range");
            }
            if (!state.destination) {
                nextErrors.destination = t("journey.create.validation.destination");
            }
            if (!state.description.trim()) {
                nextErrors.description = t("journey.create.validation.description");
            }
            return nextErrors;
        },
        [t]
    );

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const nextErrors = validate(form);
        setTouched((prev) => ({
            ...prev,
            startDate: true,
            endDate: true,
            destination: true,
            description: true,
        }));
        setErrors(nextErrors);
        setSubmitError(null);
        if (Object.keys(nextErrors).length > 0) {
            if (nextErrors.description) {
                descriptionRef.current?.focus();
            }
            return;
        }
        if (!id) {
            setSubmitError(t("journey.edit.error", { defaultValue: "No journey id provided." }));
            return;
        }

        try {
            setSubmitting(true);
            await updateJourney(
                id,
                {
                    destinationUniversity: form.destination?.name ?? "",
                    startDate: form.startDate,
                    endDate: form.endDate,
                    description: form.description.trim(),
                },
                undefined
            );
            navigate(`/journeys/${id}`);
        } catch (err) {
            console.error("Failed to update journey", err);
            setSubmitError(t("journey.edit.error", { defaultValue: "Error al actualizar el viaje." }));
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        if (id) {
            navigate(`/journeys/${id}`);
        } else {
            navigate("/journeys");
        }
    };
    const today = getTodayIsoDate();
    const endDateMin = form.startDate
        ? (addDays(form.startDate, 1) > today ? addDays(form.startDate, 1) : today)
        : today;

    if (isLoading) {
        return <div className="journey-create-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isNotFound) {
        return <div className="journey-create-page">{t("journey.not.found.title", { defaultValue: "Journey not found." })}</div>;
    }

    if (isError) {
        return <div className="journey-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }
    if (!data) {
        return <div className="journey-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }

    return (
        <div className="page-shell journey-create-page">
            <div className="journey-create-card card">
                <header className="journey-create-header">
                    <div>
                        <p className="eyebrow">{t("journey.edit.header")}</p>
                        <h1>{t("journey.edit.title")}</h1>
                        <p className="journey-create-header__lead">{t("journey.edit.subtitle")}</p>
                    </div>
                    <div className="journey-create-meta">{t("form.requiredHint")}</div>
                </header>

                <form className="journey-form" onSubmit={handleSubmit} noValidate>
                    <div className="journey-form__grid journey-form__grid--two">
                        <div className="form-field">
                            <label className="input-label" htmlFor="journey-start-date">
                                {t("journey.create.startDate")} <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input
                                id="journey-start-date"
                                type="date"
                                className={classNames("input-control", touched.startDate && errors.startDate && "input-control--error")}
                                value={form.startDate}
                                onChange={handleDateChange("startDate")}
                                onBlur={() => markTouched("startDate")}
                                placeholder={t("common.date.placeholder")}
                                min={today}
                            />
                            {touched.startDate && errors.startDate && (
                                <p className="form-field__text form-field__text--error">{errors.startDate}</p>
                            )}
                        </div>
                        <div className="form-field">
                            <label className="input-label" htmlFor="journey-end-date">
                                {t("journey.create.endDate")} <span className="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input
                                id="journey-end-date"
                                type="date"
                                className={classNames("input-control", touched.endDate && errors.endDate && "input-control--error")}
                                value={form.endDate}
                                onChange={handleDateChange("endDate")}
                                onBlur={() => markTouched("endDate")}
                                placeholder={t("common.date.placeholder")}
                                min={endDateMin}
                            />
                            {touched.endDate && errors.endDate && (
                                <p className="form-field__text form-field__text--error">{errors.endDate}</p>
                            )}
                        </div>
                    </div>

                    <CatalogAutocompleteField
                        label={t("journey.create.destination.label")}
                        placeholder={t("journey.create.destination.placeholder")}
                        value={form.destination}
                        query={destinationQuery}
                        onQueryChange={setDestinationQuery}
                        onChange={(option) => setForm((prev) => ({ ...prev, destination: option }))}
                        fetcher={searchUniversities}
                        error={touched.destination ? errors.destination : undefined}
                        required
                        onBlur={() => markTouched("destination")}
                    />

                    <div className="form-field">
                        <label className="input-label" htmlFor="journey-description">
                            {t("journey.create.description.label")} <span className="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <textarea
                            id="journey-description"
                            ref={descriptionRef}
                            className={classNames("input-control", touched.description && errors.description && "input-control--error")}
                            placeholder={t("journey.description.hint")}
                            value={form.description}
                            onChange={handleDescriptionChange}
                            onBlur={() => markTouched("description")}
                            rows={6}
                        />
                        {!errors.description && <p className="form-field__text">{t("journey.create.description.helper")}</p>}
                        {touched.description && errors.description && (
                            <p className="form-field__text form-field__text--error">{errors.description}</p>
                        )}
                    </div>

                    {submitError && <p className="form-field__text form-field__text--error">{submitError}</p>}

                    <div className="journey-form__actions">
                        <Button type="button" variant="ghost" onClick={handleCancel} disabled={submitting}>
                            {t("common.cancel")}
                        </Button>
                        <Button type="submit" variant="primary" disabled={submitting}>
                            {submitting ? t("journey.edit.submit") : t("journey.edit.submit")}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}

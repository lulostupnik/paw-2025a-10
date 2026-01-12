import { useCallback, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import { classNames } from "@/lib/utils/classNames";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { searchUniversities, type CatalogOption } from "@/lib/api/catalog";
import { useI18n } from "@/lib/i18n";

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

export default function JourneyCreatePage() {
    const navigate = useNavigate();
    const { t } = useI18n();
    const [form, setForm] = useState<JourneyFormState>({ ...INITIAL_FORM });
    const [errors, setErrors] = useState<JourneyErrors>({});
    const [touched, setTouched] = useState<JourneyTouched>({});
    const [submitting, setSubmitting] = useState(false);
    const [destinationQuery, setDestinationQuery] = useState(form.destination?.name ?? "");
    const descriptionRef = useRef<HTMLTextAreaElement>(null);

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

    const validate = useCallback((state: JourneyFormState): JourneyErrors => {
        const nextErrors: JourneyErrors = {};
        if (!state.startDate) {
            nextErrors.startDate = t("journey.create.validation.startDate");
        }
        if (!state.endDate) {
            nextErrors.endDate = t("journey.create.validation.endDate");
        }
        if (state.startDate && state.endDate && state.startDate > state.endDate) {
            nextErrors.endDate = t("journey.create.validation.range");
        }
        if (!state.destination) {
            nextErrors.destination = t("journey.create.validation.destination");
        }
        if (!state.description.trim()) {
            nextErrors.description = t("journey.create.validation.description");
        }
        return nextErrors;
    }, [t]);

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
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
        if (Object.keys(nextErrors).length > 0) {
            if (nextErrors.description) {
                descriptionRef.current?.focus();
            }
            return;
        }

        setSubmitting(true);
        window.setTimeout(() => {
            console.info("Submitting journey", {
                ...form,
                destinationId: form.destination?.id,
            });
            setSubmitting(false);
            setForm({ ...INITIAL_FORM });
            setDestinationQuery("");
            setTouched({});
            setErrors({});
            navigate("/journeys");
        }, 500);
    };

    const handleCancel = () => {
        navigate(-1);
    };

    const descriptionHelper = t("journey.create.description.helper");

    return (
        <div className="page-shell journey-create-page">
            <div className="journey-create-card card">
                <header className="journey-create-header">
                    <div>
                        <p className="eyebrow">{t("journey.create.eyebrow")}</p>
                        <h1>{t("journey.create.title")}</h1>
                        <p className="journey-create-header__lead">{t("journey.create.subtitle")}</p>
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
                                min={form.startDate || undefined}
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
                            placeholder={t("journey.create.description.placeholder")}
                            value={form.description}
                            onChange={handleDescriptionChange}
                            onBlur={() => markTouched("description")}
                            rows={6}
                        />
                        {!errors.description && <p className="form-field__text">{descriptionHelper}</p>}
                        {touched.description && errors.description && (
                            <p className="form-field__text form-field__text--error">{errors.description}</p>
                        )}
                    </div>

                    <div className="journey-form__actions">
                        <Button type="button" variant="ghost" onClick={handleCancel} disabled={submitting}>
                            {t("common.cancel")}
                        </Button>
                        <Button type="submit" variant="primary" disabled={submitting}>
                            {submitting ? t("journey.create.submitting") : t("journey.create.submit")}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}

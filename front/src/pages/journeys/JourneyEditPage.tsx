import { apiErrorMessage } from "@/lib/api/client";
import { useCallback, useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import Button from "@/components/ui/Button";
import { classNames } from "@/lib/utils/classNames";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { searchUniversities, type CatalogOption } from "@/lib/api/catalog";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { getUserId } from "@/lib/auth/auth";
import { updateJourney } from "@/lib/api/journeys";
import { useI18n } from "@/lib/i18n";
import { getTodayIsoDate } from "@/lib/utils/date";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateJourneyDetailQueries, invalidateJourneyListQueries } from "@/lib/api/queryInvalidation";
import type { JourneyDetail } from "@/types/journey";
import { focusFirstInvalidField } from "@/lib/forms/focusFirstInvalidField";
import NotFoundPage from "@/pages/errors/NotFoundPage";

interface JourneyFormState {
    startDate: string;
    endDate: string;
    destination: CatalogOption | null;
    description: string;
}

type JourneyField = keyof JourneyFormState;
type JourneyErrors = Partial<Record<JourneyField, string>>;
type JourneyTouched = Partial<Record<JourneyField, boolean>>;

const API_FIELD_TO_FORM_FIELD: Record<string, JourneyField> = {
    startDate: "startDate",
    endDate: "endDate",
    destinationUniversityId: "destination",
    description: "description",
};
const JOURNEY_DESCRIPTION_MAX_LENGTH = 2047;

const addDays = (dateValue: string, days: number) => {
    const [year, month, day] = dateValue.split("-").map(Number);
    if (!year || !month || !day) {
        return "";
    }
    const nextDate = new Date(Date.UTC(year, month - 1, day + days));
    return nextDate.toISOString().slice(0, 10);
};

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

const buildInitialForm = (journey: JourneyDetail): JourneyFormState => {
    const destinationName = journey.destinationUniversity?.name ?? "";
    const destinationId = parseIdFromUrl(journey.links?.destinationUniversityUrl) ?? 0;
    return {
        startDate: journey.startDate ?? "",
        endDate: journey.endDate ?? "",
        destination: destinationName ? { id: destinationId, name: destinationName } : null,
        description: journey.description ?? "",
    };
};

export default function JourneyEditPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data, isLoading, isError, isNotFound } = useJourneyDetailData({ journeyId: id });

    if (isLoading) {
        return <PageStatus className="journey-create-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isNotFound) {
        return <NotFoundPage/>
    }

    if (isError || !data) {
        return <PageStatus className="journey-create-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (data.user?.id !== getUserId()) {
        return <ForbiddenPage />;
    }

    return <JourneyEditForm key={data.id} journey={data} />;
}

interface JourneyEditFormProps {
    journey: JourneyDetail;
}

function JourneyEditForm({ journey }: JourneyEditFormProps) {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const [form, setForm] = useState<JourneyFormState>(() => buildInitialForm(journey));
    const [errors, setErrors] = useState<JourneyErrors>({});
    const [touched, setTouched] = useState<JourneyTouched>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [destinationQuery, setDestinationQuery] = useState(() => journey.destinationUniversity?.name ?? "");

    const markTouched = useCallback((field: JourneyField) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
    }, []);

    const handleDateChange = (field: "startDate" | "endDate") => (event: ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        setErrors((prev) => ({ ...prev, [field]: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleDescriptionChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setErrors((prev) => ({ ...prev, description: undefined }));
        setSubmitError(null);
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
            } else if (state.description.trim().length < 2 || state.description.trim().length > JOURNEY_DESCRIPTION_MAX_LENGTH) {
                nextErrors.description = t("event.create.validation.descriptionLength");
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
            focusFirstInvalidField(event.currentTarget);
            return;
        }

        try {
            setSubmitting(true);
            await updateJourney(
                journey.id,
                {
                    destinationUniversityId: form.destination?.id ?? 0,
                    startDate: form.startDate,
                    endDate: form.endDate,
                    description: form.description.trim(),
                },
                undefined
            );
            await Promise.all([
                invalidateJourneyDetailQueries(queryClient, journey.id),
                invalidateJourneyListQueries(queryClient),
            ]);
            showToast(t("journey.toast.updated"), { variant: "success" });
            navigate(`/journeys/${journey.id}`);
        } catch (err) {
            console.error("Failed to update journey", err);
            const serverErrors = mapApiFieldErrors(err, API_FIELD_TO_FORM_FIELD);
            if (Object.keys(serverErrors).length > 0) {
                setErrors((prev) => ({ ...prev, ...serverErrors }));
                focusFirstInvalidField(event.currentTarget);
            } else {
                setSubmitError(apiErrorMessage(err, t("journey.edit.error", { defaultValue: "Error al actualizar el viaje." })));
            }
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        navigate(`/journeys/${journey.id}`);
    };
    const today = getTodayIsoDate();
    const endDateMin = form.startDate
        ? (addDays(form.startDate, 1) > today ? addDays(form.startDate, 1) : today)
        : today;

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
                        onChange={(option) => {
                            setErrors((prev) => ({ ...prev, destination: undefined }));
                            setSubmitError(null);
                            setForm((prev) => ({ ...prev, destination: option }));
                        }}
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
                            className={classNames("input-control", touched.description && errors.description && "input-control--error")}
                            placeholder={t("journey.description.hint")}
                            value={form.description}
                            onChange={handleDescriptionChange}
                            onBlur={() => markTouched("description")}
                            rows={6}
                        />
                        <p className={`character-counter ${form.description.trim().length > JOURNEY_DESCRIPTION_MAX_LENGTH ? "is-error" : ""}`}>
                            {form.description.trim().length}/{JOURNEY_DESCRIPTION_MAX_LENGTH}
                        </p>
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
                            {t("journey.edit.submit")}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}

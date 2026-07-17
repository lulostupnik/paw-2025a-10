import { useRef, useState, useCallback, useEffect, useMemo } from "react";
import type { ChangeEvent, FormEvent } from "react";
import Button from "@/components/ui/Button";
import TextField from "@/components/ui/TextField";
import Checkbox from "@/components/ui/Checkbox";
import { classNames } from "@/lib/utils/classNames";
import { searchCities, type CatalogOption } from "@/lib/api/catalog";
import { useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { useI18n } from "@/lib/i18n";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import PageStatus from "@/components/ui/PageStatus";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useToast } from "@/components/ui/ToastProvider";
import { getUserId } from "@/lib/auth/auth";
import { updateEvent, updateEventFlyer } from "@/lib/api/events";
import { apiErrorMessage, apiErrorStatus } from "@/lib/api/client";
import { mapApiFieldErrors } from "@/lib/api/formErrors";
import { invalidateEventDetailQueries, invalidateEventListQueries } from "@/lib/api/queryInvalidation";
import type { EventDetail } from "@/types/event";

const ACCEPTED_EXTENSIONS = [".jpg", ".jpeg", ".png"];
const ACCEPTED_MIME_TYPES = ["image/jpeg", "image/png"];
const MAX_FILE_SIZE = 2 * 1024 * 1024;

interface FormState {
    name: string;
    city: CatalogOption | null;
    date: string;
    time: string;
    allDay: boolean;
    description: string;
    address: string;
    participantLimit: string;
    unlimited: boolean;
    flyer: File | null;
}

type FormField = "name" | "city" | "date" | "time" | "description" | "address" | "participantLimit" | "flyer";
type FormErrors = Partial<Record<FormField, string>>;

const API_FIELD_TO_FORM_FIELD: Record<string, FormField> = {
    title: "name",
    cityId: "city",
    date: "date",
    time: "time",
    description: "description",
    address: "address",
    attendeesLimit: "participantLimit",
};
type TouchedState = Partial<Record<FormField, boolean>>;

const buildInitialForm = (event: EventDetail): FormState => {
    const unlimited = !event.attendeesLimit || event.attendeesLimit <= 0;
    return {
        name: event.title ?? "",
        city: event.city?.name ? { id: event.city.id ?? 0, name: event.city.name } : null,
        date: event.date ?? "",
        time: event.time ?? "",
        allDay: !event.time,
        description: event.description ?? "",
        address: event.address ?? "",
        participantLimit: unlimited ? "" : String(event.attendeesLimit ?? ""),
        unlimited,
        flyer: null,
    };
};

export default function EventEditPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data, isLoading, isError } = useEventDetailData({ eventId: id });

    if (isLoading) {
        return <PageStatus className="event-create-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !data) {
        return <PageStatus className="event-create-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (data.user?.id !== getUserId()) {
        return <ForbiddenPage />;
    }

    return <EventEditForm key={data.id} event={data} />;
}

interface EventEditFormProps {
    event: EventDetail;
}

function EventEditForm({ event }: EventEditFormProps) {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { t } = useI18n();
    const { showToast } = useToast();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [form, setForm] = useState<FormState>(() => buildInitialForm(event));
    const [errors, setErrors] = useState<FormErrors>({});
    const [touched, setTouched] = useState<TouchedState>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [dragging, setDragging] = useState(false);
    const [cityQuery, setCityQuery] = useState(() => event.city?.name ?? "");

    const flyerPreviewUrl = useMemo(() => (form.flyer ? URL.createObjectURL(form.flyer) : null), [form.flyer]);

    useEffect(() => {
        if (!flyerPreviewUrl) {
            return;
        }
        return () => URL.revokeObjectURL(flyerPreviewUrl);
    }, [flyerPreviewUrl]);

    const markTouched = useCallback((field: FormField) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
    }, []);

    const handleTextChange = useCallback(
        (field: keyof Pick<FormState, "name" | "description" | "address">) =>
            (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
                const value = event.target.value;
                setErrors((prev) => ({ ...prev, [field]: undefined }));
                setSubmitError(null);
                setForm((prev) => ({ ...prev, [field]: value }));
            },
        []
    );

    const handleDateChange = (event: ChangeEvent<HTMLInputElement>) => {
        setErrors((prev) => ({ ...prev, date: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, date: event.target.value }));
    };

    const handleTimeChange = (event: ChangeEvent<HTMLInputElement>) => {
        setErrors((prev) => ({ ...prev, time: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, time: event.target.value }));
    };

    const handleLimitChange = (event: ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        setErrors((prev) => ({ ...prev, participantLimit: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, participantLimit: value }));
    };

    const handleAllDayToggle = (checked: boolean) => {
        setErrors((prev) => ({ ...prev, time: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, allDay: checked, time: checked ? "" : prev.time }));
    };

    const handleUnlimitedToggle = (checked: boolean) => {
        setErrors((prev) => ({ ...prev, participantLimit: undefined }));
        setSubmitError(null);
        setForm((prev) => ({ ...prev, unlimited: checked, participantLimit: checked ? "" : prev.participantLimit }));
    };

    const validate = useCallback(
        (state: FormState): FormErrors => {
            const nextErrors: FormErrors = {};
            if (!state.name.trim()) {
                nextErrors.name = t("event.create.validation.name");
            }
            if (!state.city) {
                nextErrors.city = t("event.create.validation.city");
            }
            if (!state.date) {
                nextErrors.date = t("event.create.validation.date");
            }
            if (!state.allDay && !state.time.trim()) {
                nextErrors.time = t("event.create.validation.time");
            }
            if (!state.description.trim()) {
                nextErrors.description = t("event.create.validation.description");
            }
            if (!state.unlimited && state.participantLimit.trim()) {
                const parsed = Number(state.participantLimit);
                if (Number.isNaN(parsed) || parsed <= 0) {
                    nextErrors.participantLimit = t("event.create.validation.limitInvalid");
                }
            }
            if (!state.unlimited && !state.participantLimit.trim()) {
                nextErrors.participantLimit = t("event.create.validation.limitRequired");
            }
            if (state.flyer) {
                const extension = `.${state.flyer.name.split(".").pop()?.toLowerCase() ?? ""}`;
                const matchesExtension = ACCEPTED_EXTENSIONS.includes(extension);
                const matchesMime = ACCEPTED_MIME_TYPES.includes(state.flyer.type);
                const validSize = state.flyer.size <= MAX_FILE_SIZE;
                if (!matchesExtension && !matchesMime) {
                    nextErrors.flyer = t("event.create.validation.flyerType");
                }
                if (!validSize) {
                    nextErrors.flyer = t("event.create.validation.flyerSize");
                }
            }
            return nextErrors;
        },
        [t]
    );

    const handleFileSelection = useCallback(
        (files: FileList | null) => {
            if (!files || files.length === 0) {
                return;
            }
            const file = files[0];
            const extension = `.${file.name.split(".").pop()?.toLowerCase() ?? ""}`;
            const matchesExtension = ACCEPTED_EXTENSIONS.includes(extension);
            const matchesMime = ACCEPTED_MIME_TYPES.includes(file.type);
            const validSize = file.size <= MAX_FILE_SIZE;

            if (!matchesExtension && !matchesMime) {
                setSubmitError(null);
                setErrors((prev) => ({ ...prev, flyer: t("event.create.validation.flyerType") }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            if (!validSize) {
                setSubmitError(null);
                setErrors((prev) => ({ ...prev, flyer: t("event.create.validation.flyerSize") }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            setSubmitError(null);
            setForm((prev) => ({ ...prev, flyer: file }));
            setErrors((prev) => ({ ...prev, flyer: undefined }));
            markTouched("flyer");
        },
        [markTouched, t]
    );

    const handleSubmit = async (submitEvent: FormEvent<HTMLFormElement>) => {
        submitEvent.preventDefault();
        const nextErrors = validate(form);
        const touchedAll: TouchedState = {
            name: true,
            city: true,
            date: true,
            time: true,
            description: true,
            address: true,
            participantLimit: true,
            flyer: true,
        };
        setTouched((prev) => ({ ...prev, ...touchedAll }));
        setErrors(nextErrors);
        setSubmitError(null);
        if (Object.keys(nextErrors).length > 0) {
            return;
        }

        const cityId = form.city?.id;
        if (!cityId) {
            setErrors((prev) => ({ ...prev, city: t("event.create.validation.city") }));
            setTouched((prev) => ({ ...prev, city: true }));
            return;
        }

        try {
            setSubmitting(true);
            await updateEvent(event.id, {
                cityId,
                date: form.date,
                description: form.description.trim(),
                title: form.name.trim(),
                time: form.allDay ? null : form.time,
                address: form.address.trim() || null,
                attendeesLimit: form.unlimited ? null : Number(form.participantLimit),
            });
            let flyerFailed = false;
            if (form.flyer) {
                try {
                    await updateEventFlyer(event.id, form.flyer);
                } catch (flyerError) {
                    console.error("Failed to upload event flyer", flyerError);
                    flyerFailed = true;
                }
            }
            await Promise.all([
                invalidateEventDetailQueries(queryClient, event.id),
                invalidateEventListQueries(queryClient),
            ]);
            showToast(flyerFailed ? t("event.toast.updatedWithoutFlyer") : t("event.toast.updated"), {
                variant: flyerFailed ? "info" : "success",
            });
            navigate(`/events/${event.id}`);
        } catch (err) {
            console.error("Failed to update event", err);
            const serverErrors: FormErrors = mapApiFieldErrors(err, API_FIELD_TO_FORM_FIELD);
            if (apiErrorStatus(err) === 409) {
                serverErrors.participantLimit = apiErrorMessage(err, t("event.edit.error", { defaultValue: "Error al actualizar el evento." }));
            }
            if (Object.keys(serverErrors).length > 0) {
                setErrors((prev) => ({ ...prev, ...serverErrors }));
            } else {
                setSubmitError(apiErrorMessage(err, t("event.edit.error", { defaultValue: "Error al actualizar el evento." })));
            }
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        navigate(`/events/${event.id}`);
    };

    const timeFieldDisabled = form.allDay;
    const limitFieldDisabled = form.unlimited;

    return (
        <div className="page-shell event-create-page">
            <div className="event-create-card card">
                <header className="event-create-header">
                    <div>
                        <p className="eyebrow">{t("event.edit.header")}</p>
                        <h1>{t("event.edit.title")}</h1>
                        <p className="event-create-header__lead">{t("event.edit.subtitle")}</p>
                    </div>
                    <div className="event-create-meta">
                        <span>{t("form.requiredHint")}</span>
                    </div>
                </header>

                <form className="event-form" onSubmit={handleSubmit} noValidate>
                    <TextField
                        label={
                            <span className="input-label">
                                {t("event.create.name.label")} <span className="required-indicator" aria-hidden="true">*</span>
                            </span>
                        }
                        placeholder={t("event.create.name.placeholder")}
                        value={form.name}
                        onChange={handleTextChange("name")}
                        onBlur={() => markTouched("name")}
                        errorText={touched.name ? errors.name : undefined}
                    />

                    <CatalogAutocompleteField
                        label={t("event.create.city.label")}
                        placeholder={t("event.create.city.placeholder")}
                        value={form.city}
                        query={cityQuery}
                        onQueryChange={setCityQuery}
                        onChange={(option) => {
                            setErrors((prev) => ({ ...prev, city: undefined }));
                            setSubmitError(null);
                            setForm((prev) => ({ ...prev, city: option }));
                        }}
                        fetcher={searchCities}
                        error={touched.city ? errors.city : undefined}
                        onBlur={() => markTouched("city")}
                        required
                    />

                    <div className="event-form__row event-form__row--datetime">
                        <div className="event-form__grid event-form__grid--two">
                            <div className="form-field">
                                <label className="input-label" htmlFor="field-date">
                                    {t("event.create.date.label")} <span className="required-indicator" aria-hidden="true">*</span>
                                </label>
                                <input
                                    id="field-date"
                                    type="date"
                                    className={classNames("input-control", touched.date && errors.date && "input-control--error")}
                                    value={form.date}
                                    onChange={handleDateChange}
                                    onBlur={() => markTouched("date")}
                                    placeholder={t("common.date.placeholder")}
                                />
                                {touched.date && errors.date && (
                                    <p className="form-field__text form-field__text--error">{errors.date}</p>
                                )}
                            </div>

                            <div className="form-field">
                                <label className="input-label" htmlFor="field-time">
                                    {t("event.create.time.label")}
                                    {!timeFieldDisabled && <span className="required-indicator" aria-hidden="true">*</span>}
                                </label>
                                <input
                                    id="field-time"
                                    type="time"
                                    className={classNames("input-control", touched.time && errors.time && "input-control--error")}
                                    value={form.time}
                                    onChange={handleTimeChange}
                                    onBlur={() => markTouched("time")}
                                    placeholder={t("common.time.placeholder")}
                                    disabled={timeFieldDisabled}
                                />
                                <p className="form-field__text">
                                    {timeFieldDisabled
                                        ? t("event.create.time.optional.allDay")
                                        : t("event.create.time.required.unlessAllDay")}
                                </p>
                                {touched.time && errors.time && (
                                    <p className="form-field__text form-field__text--error">{errors.time}</p>
                                )}
                            </div>
                        </div>

                        <Checkbox
                            label={t("event.create.allDay")}
                            checked={form.allDay}
                            onChange={(changeEvent) => handleAllDayToggle(changeEvent.target.checked)}
                        />
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="field-description">
                            {t("event.create.description.label")} <span className="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <textarea
                            id="field-description"
                            className={classNames("input-control", touched.description && errors.description && "input-control--error")}
                            placeholder={t("event.create.description.placeholder")}
                            value={form.description}
                            onChange={handleTextChange("description")}
                            onBlur={() => markTouched("description")}
                            rows={5}
                        />
                        {touched.description && errors.description && (
                            <p className="form-field__text form-field__text--error">{errors.description}</p>
                        )}
                    </div>

                    <TextField
                        label={<span className="input-label">{t("event.create.address.label")}</span>}
                        placeholder={t("event.create.address.placeholder")}
                        value={form.address}
                        onChange={handleTextChange("address")}
                        onBlur={() => markTouched("address")}
                        errorText={touched.address ? errors.address : undefined}
                    />

                    <div className="event-form__row event-form__row--capacity">
                        <TextField
                            label={
                                <span className="input-label">
                                    {t("event.create.limit.label")}
                                    {!limitFieldDisabled && <span className="required-indicator" aria-hidden="true">*</span>}
                                </span>
                            }
                            placeholder={t("event.create.limit.placeholder", { defaultValue: "Enter attendee limit" })}
                            value={form.participantLimit}
                            onChange={handleLimitChange}
                            onBlur={() => markTouched("participantLimit")}
                            disabled={limitFieldDisabled}
                            helperText={t(
                                limitFieldDisabled
                                    ? "event.create.limit.optional.unlimited"
                                    : "event.create.limit.required.unlessUnlimited"
                            )}
                            errorText={touched.participantLimit ? errors.participantLimit : undefined}
                        />

                        <Checkbox
                            label={t("event.create.unlimited")}
                            checked={form.unlimited}
                            onChange={(changeEvent) => handleUnlimitedToggle(changeEvent.target.checked)}
                        />
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="field-flyer">
                            {t("event.edit.flyer.label", { defaultValue: "Upload Flyer" })}
                        </label>
                        <div
                            className={classNames(
                                "upload-dropzone",
                                dragging && "is-dragging",
                                touched.flyer && errors.flyer && "has-error"
                            )}
                            onDragOver={(dragEvent) => {
                                dragEvent.preventDefault();
                                setDragging(true);
                            }}
                            onDragLeave={(dragEvent) => {
                                dragEvent.preventDefault();
                                setDragging(false);
                            }}
                            onDrop={(dropEvent) => {
                                dropEvent.preventDefault();
                                setDragging(false);
                                handleFileSelection(dropEvent.dataTransfer.files);
                            }}
                            onClick={() => fileInputRef.current?.click()}
                            role="button"
                            tabIndex={0}
                            onKeyDown={(keyEvent) => {
                                if (keyEvent.key === "Enter" || keyEvent.key === " ") {
                                    keyEvent.preventDefault();
                                    fileInputRef.current?.click();
                                }
                            }}
                        >
                            {form.flyer ? (
                                <div className="upload-preview">
                                    <img src={flyerPreviewUrl ?? ""} alt={t("event.flyer.alt")} />
                                    <div>
                                        <p>{form.flyer.name}</p>
                                        <p className="upload-hint">{t("event.create.flyer.change")}</p>
                                    </div>
                                </div>
                            ) : (
                                <div className="upload-placeholder">
                                    <UploadIcon />
                                    <p>{t("event.create.flyer.placeholder")}</p>
                                    <p className="upload-hint">{t("event.create.flyer.helper")}</p>
                                </div>
                            )}
                        </div>
                        <input
                            id="field-flyer"
                            ref={fileInputRef}
                            type="file"
                            accept={ACCEPTED_EXTENSIONS.join(",")}
                            style={{ display: "none" }}
                            onChange={(changeEvent) => handleFileSelection(changeEvent.target.files)}
                            onBlur={() => markTouched("flyer")}
                        />
                        <p className="upload-hint">{t("event.flyer.edit.hint")}</p>
                        {touched.flyer && errors.flyer && (
                            <p className="form-field__text form-field__text--error">{errors.flyer}</p>
                        )}
                    </div>

                    {submitError && <p className="form-field__text form-field__text--error">{submitError}</p>}

                    <div className="event-form__actions">
                        <Button type="button" variant="ghost" onClick={handleCancel} disabled={submitting}>
                            {t("common.cancel")}
                        </Button>
                        <Button type="submit" variant="primary" disabled={submitting}>
                            {t("event.edit")}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}

const UploadIcon = () => (
    <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" strokeWidth={1.8} aria-hidden="true">
        <path
            d="M16 31v6a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-6"
            strokeLinecap="round"
            strokeLinejoin="round"
        />
        <path d="M24 29V8" strokeLinecap="round" strokeLinejoin="round" />
        <path d="m15 18 9-9 9 9" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
);

import { useRef, useState, useCallback } from "react";
import type { ChangeEvent, FormEvent } from "react";
import Button from "@/components/ui/Button";
import TextField from "@/components/ui/TextField";
import Checkbox from "@/components/ui/Checkbox";
import { classNames } from "@/lib/utils/classNames";
import { searchCities, type CatalogOption } from "@/lib/api/catalog";
import { useNavigate } from "react-router-dom";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { useI18n } from "@/lib/i18n";
import { createEvent, updateEventFlyer } from "@/lib/api/events";
import { apiFieldErrors } from "@/lib/api/client";

const ACCEPTED_EXTENSIONS = [".jpg", ".jpeg", ".png"];
const ACCEPTED_MIME_TYPES = ["image/jpeg", "image/png"];
const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

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
type TouchedState = Partial<Record<FormField, boolean>>;

// Campo del ErrorDto de la API → campo del formulario.
const API_FIELD_TO_FORM_FIELD: Record<string, FormField> = {
    title: "name",
    cityId: "city",
    date: "date",
    time: "time",
    description: "description",
    address: "address",
    attendeesLimit: "participantLimit",
};

const INITIAL_FORM: FormState = {
    name: "",
    city: null,
    date: "",
    time: "",
    allDay: false,
    description: "",
    address: "",
    participantLimit: "",
    unlimited: false,
    flyer: null,
};

export default function EventCreatePage() {
    const navigate = useNavigate();
    const { t } = useI18n();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [form, setForm] = useState<FormState>({ ...INITIAL_FORM });
    const [errors, setErrors] = useState<FormErrors>({});
    const [touched, setTouched] = useState<TouchedState>({});
    const [submitting, setSubmitting] = useState(false);
    const [dragging, setDragging] = useState(false);
    const [cityQuery, setCityQuery] = useState("");
    const [submitError, setSubmitError] = useState<string | null>(null);

    const resolveSubmitError = useCallback(
        (error: unknown) => {
            const response = (error as { response?: { status?: number; data?: { message?: string; errors?: { field?: string; message?: string }[] } } | null })?.response;
            const data = response?.data;
            const firstFieldError = data?.errors?.find((item) => item?.field || item?.message);
            if (firstFieldError) {
                const field = firstFieldError.field ?? "";
                const message = firstFieldError.message?.toLowerCase() ?? "";
                if (field === "city" || field === "cityId") {
                    if (message.includes("must not be null") || message.includes("must not be empty")) {
                        return t("event.create.validation.city");
                    }
                    if (message.includes("between") || message.includes("size")) {
                        return t("event.create.validation.cityLength");
                    }
                    return t("event.create.validation.cityInvalid");
                }
                if (field === "date") {
                    if (message.includes("must not be null") || message.includes("must not be empty")) {
                        return t("event.create.validation.date");
                    }
                    if (message.includes("future")) {
                        return t("event.create.validation.dateFuture");
                    }
                    return t("event.create.validation.dateInvalid");
                }
                if (field === "title") {
                    if (message.includes("must not be null") || message.includes("must not be empty")) {
                        return t("event.create.validation.name");
                    }
                    if (message.includes("between") || message.includes("size")) {
                        return t("event.create.validation.nameLength");
                    }
                    return t("event.create.validation.nameInvalid");
                }
                if (field === "description") {
                    if (message.includes("between")) {
                        return t("event.create.validation.descriptionLength");
                    }
                    if (message.includes("must not be null") || message.includes("must not be empty")) {
                        return t("event.create.validation.description");
                    }
                    return t("event.create.validation.descriptionInvalid");
                }
                if (field === "address") {
                    if (message.includes("between") || message.includes("size")) {
                        return t("event.create.validation.addressLength");
                    }
                    return t("event.create.validation.addressInvalid");
                }
                if (field === "time") {
                    return t("event.create.validation.timeInvalid");
                }
                if (field === "attendeesLimit") {
                    if (message.includes("less than or equal to") || message.includes("max")) {
                        return t("event.create.validation.limitMax");
                    }
                    if (message.includes("greater than or equal to") || message.includes("min")) {
                        return t("event.create.validation.limitMin");
                    }
                    return t("event.create.validation.limitInvalid");
                }
            }

            const serverMessage = data?.message;
            if (serverMessage) {
                const normalized = serverMessage.toLowerCase();
                if (normalized.includes("city does not exist")) {
                    return t("event.create.validation.cityInvalid");
                }
                if (normalized.includes("date must be in the future")) {
                    return t("event.create.validation.dateFuture");
                }
                if (normalized.includes("invalid date format")) {
                    return t("event.create.validation.dateInvalid");
                }
                if (normalized.includes("size must be between 0 and 50")) {
                    return t("event.create.validation.nameLength");
                }
                if (normalized.includes("size must be between 2 and 2047")) {
                    return t("event.create.validation.descriptionLength");
                }
                if (normalized.includes("size must be between 0 and 255")) {
                    return t("event.create.validation.addressLength");
                }
                if (normalized.includes("size must be between 0 and 100")) {
                    return t("event.create.validation.cityLength");
                }
                if (normalized.includes("must be less than or equal to 1000")) {
                    return t("event.create.validation.limitMax");
                }
                if (normalized.includes("must be greater than or equal to 1")) {
                    return t("event.create.validation.limitMin");
                }
                return t("event.create.error.withReason", { values: { 0: serverMessage } });
            }

            switch (response?.status) {
                case 401:
                    return t("event.create.error.unauthorized");
                case 403:
                    return t("event.create.error.forbidden");
                case 409:
                    return t("event.create.error.conflict");
                default:
                    return t("event.create.error.generic");
            }
        },
        [t]
    );

    const markTouched = useCallback((field: FormField) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
    }, []);

    const handleTextChange = useCallback(
        (field: keyof Pick<FormState, "name" | "description" | "address">) =>
            (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
                const value = event.target.value;
                setForm((prev) => ({ ...prev, [field]: value }));
            },
        []
    );

    const handleDateChange = (event: ChangeEvent<HTMLInputElement>) => {
        setForm((prev) => ({ ...prev, date: event.target.value }));
    };

    const handleTimeChange = (event: ChangeEvent<HTMLInputElement>) => {
        setForm((prev) => ({ ...prev, time: event.target.value }));
    };

    const handleLimitChange = (event: ChangeEvent<HTMLInputElement>) => {
        const value = event.target.value;
        setForm((prev) => ({ ...prev, participantLimit: value }));
    };

    const handleAllDayToggle = (checked: boolean) => {
        setForm((prev) => ({ ...prev, allDay: checked, time: checked ? "" : prev.time }));
    };

    const handleUnlimitedToggle = (checked: boolean) => {
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
            if (!state.flyer) {
                nextErrors.flyer = t("event.create.validation.flyerMissing");
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
                setErrors((prev) => ({ ...prev, flyer: t("event.create.validation.flyerType") }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            if (!validSize) {
                setErrors((prev) => ({ ...prev, flyer: t("event.create.validation.flyerSize") }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            setForm((prev) => ({ ...prev, flyer: file }));
            setErrors((prev) => ({ ...prev, flyer: undefined }));
            markTouched("flyer");
        },
        [markTouched, t]
    );

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
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
        if (Object.keys(nextErrors).length > 0) {
            return;
        }
        setSubmitError(null);

        const cityId = form.city?.id;
        if (cityId == null) {
            return;
        }

        try {
            setSubmitting(true);
            const eventResponse = await createEvent({
                cityId,
                date: form.date,
                description: form.description.trim(),
                title: form.name.trim(),
                time: form.allDay ? null : form.time,
                address: form.address.trim() || null,
                attendeesLimit: form.unlimited ? null : Number(form.participantLimit),
            });
            if (form.flyer) {
                await updateEventFlyer(eventResponse.id, form.flyer);
            }
            setForm({ ...INITIAL_FORM });
            setCityQuery("");
            setTouched({});
            setErrors({});
            navigate(`/events/${eventResponse.id}`);
        } catch (error) {
            console.error("Failed to create event", error);
            const serverErrors: FormErrors = {};
            for (const [apiField, message] of Object.entries(apiFieldErrors(error))) {
                const formField = API_FIELD_TO_FORM_FIELD[apiField];
                if (formField) {
                    serverErrors[formField] = message;
                }
            }
            if (Object.keys(serverErrors).length > 0) {
                setErrors((prev) => ({ ...prev, ...serverErrors }));
            } else {
                setSubmitError(resolveSubmitError(error));
            }
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        navigate(-1);
    };

    const timeFieldDisabled = form.allDay;
    const limitFieldDisabled = form.unlimited;

    return (
        <div className="page-shell event-create-page">
            <div className="event-create-card card">
                <header className="event-create-header">
                    <div>
                        <p className="eyebrow">{t("event.create.eyebrow")}</p>
                        <h1>{t("event.create.title")}</h1>
                        <p className="event-create-header__lead">{t("event.create.subtitle")}</p>
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
                        onChange={(option) => setForm((prev) => ({ ...prev, city: option }))}
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
                                <label className="form-field__label" htmlFor="field-time">
                                    {t("event.create.time.label")}
                                </label>
                                <input
                                    id="field-time"
                                    type="time"
                                    placeholder={t("common.time.placeholder")}
                                    className={classNames(
                                        "input-control",
                                        timeFieldDisabled && "input-control--disabled",
                                        touched.time && errors.time && "input-control--error"
                                    )}
                                    value={form.time}
                                    onChange={handleTimeChange}
                                    onBlur={() => markTouched("time")}
                                    disabled={timeFieldDisabled}
                                />
                                {touched.time && errors.time && (
                                    <p className="form-field__text form-field__text--error">{errors.time}</p>
                                )}
                            </div>
                        </div>
                        <Checkbox
                            label={t("event.create.allDay")}
                            checked={form.allDay}
                            onChange={(event) => handleAllDayToggle(event.target.checked)}
                            containerClassName="event-form__checkbox"
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
                        />
                        {touched.description && errors.description && (
                            <p className="form-field__text form-field__text--error">{errors.description}</p>
                        )}
                    </div>

                    <TextField
                        label={t("event.create.address.label")}
                        placeholder={t("event.create.address.placeholder")}
                        value={form.address}
                        onChange={handleTextChange("address")}
                        onBlur={() => markTouched("address")}
                    />

                    <div className="event-form__row event-form__row--limit">
                        <div className="form-field">
                            <label className="form-field__label" htmlFor="field-limit">
                                {t("event.create.limit.label")}
                            </label>
                            <input
                                id="field-limit"
                                type="number"
                                min="1"
                                className={classNames(
                                    "input-control",
                                    limitFieldDisabled && "input-control--disabled",
                                    touched.participantLimit && errors.participantLimit && "input-control--error"
                                )}
                                value={form.participantLimit}
                                onChange={handleLimitChange}
                                onBlur={() => markTouched("participantLimit")}
                                disabled={limitFieldDisabled}
                            />
                            {touched.participantLimit && errors.participantLimit && (
                                <p className="form-field__text form-field__text--error">{errors.participantLimit}</p>
                            )}
                        </div>
                        <Checkbox
                            label={t("event.create.unlimited")}
                            checked={form.unlimited}
                            onChange={(event) => handleUnlimitedToggle(event.target.checked)}
                            containerClassName="event-form__checkbox"
                        />
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="field-flyer">
                            {t("event.create.flyer.label")}
                            <span className="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <div
                            className={classNames(
                                "upload-dropzone",
                                dragging && "is-dragging",
                                touched.flyer && errors.flyer && "has-error"
                            )}
                            onDragOver={(event) => {
                                event.preventDefault();
                                setDragging(true);
                            }}
                            onDragLeave={(event) => {
                                event.preventDefault();
                                setDragging(false);
                            }}
                            onDrop={(event) => {
                                event.preventDefault();
                                setDragging(false);
                                handleFileSelection(event.dataTransfer.files);
                            }}
                            onClick={() => fileInputRef.current?.click()}
                            role="button"
                            tabIndex={0}
                            onKeyDown={(event) => {
                                if (event.key === "Enter" || event.key === " ") {
                                    event.preventDefault();
                                    fileInputRef.current?.click();
                                }
                            }}
                        >
                            {form.flyer ? (
                                <div className="event-file-info">
                                    <UploadIcon />
                                    <div>
                                        <p className="event-file-info__name">{form.flyer.name}</p>
                                        <button
                                            type="button"
                                            className="event-file-info__change"
                                            onClick={(event) => {
                                                event.stopPropagation();
                                                fileInputRef.current?.click();
                                            }}
                                        >
                                            {t("event.create.flyer.change")}
                                        </button>
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
                            onChange={(event) => handleFileSelection(event.target.files)}
                            onBlur={() => markTouched("flyer")}
                        />
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
                            {submitting ? t("event.create.submitting") : t("event.create.submit")}
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

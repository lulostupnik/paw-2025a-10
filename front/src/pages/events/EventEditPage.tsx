import { useRef, useState, useCallback, useEffect } from "react";
import type { ChangeEvent, FormEvent } from "react";
import Button from "@/components/ui/Button";
import TextField from "@/components/ui/TextField";
import Checkbox from "@/components/ui/Checkbox";
import { classNames } from "@/lib/utils/classNames";
import { searchCities, type CatalogOption } from "@/lib/api/catalog";
import { useNavigate, useParams } from "react-router-dom";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";
import { useI18n } from "@/lib/i18n";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import { updateEvent, updateEventFlyer } from "@/lib/api/events";

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

export default function EventEditPage() {
    const navigate = useNavigate();
    const { t } = useI18n();
    const { id } = useParams();
    const { data, isLoading, isError } = useEventDetailData({ eventId: id });
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [form, setForm] = useState<FormState>({ ...INITIAL_FORM });
    const [errors, setErrors] = useState<FormErrors>({});
    const [touched, setTouched] = useState<TouchedState>({});
    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [dragging, setDragging] = useState(false);
    const [cityQuery, setCityQuery] = useState("");
    const [seeded, setSeeded] = useState(false);

    useEffect(() => {
        if (seeded || !data) {
            return;
        }
        const hasUnlimited = !data.attendeesLimit || data.attendeesLimit <= 0;
        setForm({
            name: data.title ?? "",
            city: data.city?.name ? { id: 0, name: data.city.name } : null,
            date: data.date ?? "",
            time: data.time ?? "",
            allDay: !data.time,
            description: data.description ?? "",
            address: data.address ?? "",
            participantLimit: hasUnlimited ? "" : String(data.attendeesLimit ?? ""),
            unlimited: hasUnlimited,
            flyer: null,
        });
        setCityQuery(data.city?.name ?? "");
        setSeeded(true);
    }, [data, seeded]);

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
        setSubmitError(null);
        if (Object.keys(nextErrors).length > 0) {
            return;
        }
        if (!id) {
            setSubmitError(t("event.edit.error", { defaultValue: "Missing event id." }));
            return;
        }

        try {
            setSubmitting(true);
            await updateEvent(Number(id), {
                city: form.city?.name ?? "",
                date: form.date,
                description: form.description.trim(),
                title: form.name.trim(),
                time: form.allDay ? null : form.time,
                address: form.address.trim() || null,
                attendeesLimit: form.unlimited ? null : Number(form.participantLimit),
            });
            if (form.flyer) {
                await updateEventFlyer(Number(id), form.flyer);
            }
            navigate(`/events/${id}`);
        } catch (err) {
            console.error("Failed to update event", err);
            setSubmitError(t("event.edit.error", { defaultValue: "Error al actualizar el evento." }));
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        if (id) {
            navigate(`/events/${id}`);
        } else {
            navigate("/events");
        }
    };

    const timeFieldDisabled = form.allDay;
    const limitFieldDisabled = form.unlimited;

    if (isLoading) {
        return <div className="event-create-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="event-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }
    if (!data) {
        return <div className="event-create-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }

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
                                <label className="input-label" htmlFor="field-time">
                                    {t("event.create.time.label")} <span className="required-indicator" aria-hidden="true">*</span>
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
                                {touched.time && errors.time && (
                                    <p className="form-field__text form-field__text--error">{errors.time}</p>
                                )}
                            </div>
                        </div>

                        <Checkbox
                            label={t("event.create.allday.label")}
                            checked={form.allDay}
                            onChange={(event) => handleAllDayToggle(event.target.checked)}
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
                                    {t("event.create.limit.label")} <span className="required-indicator" aria-hidden="true">*</span>
                                </span>
                            }
                            placeholder={t("event.create.limit.placeholder")}
                            value={form.participantLimit}
                            onChange={handleLimitChange}
                            onBlur={() => markTouched("participantLimit")}
                            disabled={limitFieldDisabled}
                            errorText={touched.participantLimit ? errors.participantLimit : undefined}
                        />

                        <Checkbox
                            label={t("event.create.limit.unlimited")}
                            checked={form.unlimited}
                            onChange={(event) => handleUnlimitedToggle(event.target.checked)}
                        />
                    </div>

                    <div
                        className={classNames("file-uploader", dragging && "file-uploader--dragging", touched.flyer && errors.flyer && "file-uploader--error")}
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
                    >
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept={ACCEPTED_EXTENSIONS.join(",")}
                            hidden
                            onChange={(event) => handleFileSelection(event.target.files)}
                        />
                        <div className="file-uploader__body">
                            <div className="file-uploader__icon" aria-hidden="true">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8}>
                                    <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2 1.586-1.586a2 2 0 012.828 0L20 14" />
                                    <path d="M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    <path d="M12 8h.01" />
                                </svg>
                            </div>
                            <div>
                                <p className="file-uploader__title">{t("event.create.flyer.title")}</p>
                                <p className="file-uploader__subtitle">{t("event.create.flyer.subtitle")}</p>
                            </div>
                        </div>
                        <div className="file-uploader__actions">
                            <Button type="button" variant="secondary" size="sm" onClick={() => fileInputRef.current?.click()}>
                                {t("event.create.flyer.button")}
                            </Button>
                            {data.flyerImageUrl && !form.flyer && (
                                <span className="file-uploader__filename">{t("event.edit.flyer.current", { defaultValue: "Flyer actual" })}</span>
                            )}
                            {form.flyer && <span className="file-uploader__filename">{form.flyer.name}</span>}
                        </div>
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

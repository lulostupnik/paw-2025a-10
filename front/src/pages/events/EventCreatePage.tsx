import { useRef, useState, useCallback } from "react";
import type { ChangeEvent, FormEvent } from "react";
import Button from "@/components/ui/Button";
import TextField from "@/components/ui/TextField";
import Checkbox from "@/components/ui/Checkbox";
import { classNames } from "@/lib/utils/classNames";
import { searchCities, type CatalogOption } from "@/lib/api/catalog";
import { useNavigate } from "react-router-dom";
import CatalogAutocompleteField from "@/components/form/CatalogAutocompleteField";

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

export default function EventCreatePage() {
    const navigate = useNavigate();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [form, setForm] = useState<FormState>({ ...INITIAL_FORM });
    const [errors, setErrors] = useState<FormErrors>({});
    const [touched, setTouched] = useState<TouchedState>({});
    const [submitting, setSubmitting] = useState(false);
    const [dragging, setDragging] = useState(false);
    const [cityQuery, setCityQuery] = useState("");

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
                nextErrors.name = "El nombre del evento es obligatorio.";
            }
            if (!state.city) {
                nextErrors.city = "Selecciona una ciudad.";
            }
            if (!state.date) {
                nextErrors.date = "Selecciona una fecha.";
            }
            if (!state.allDay && !state.time.trim()) {
                nextErrors.time = "Define un horario para el evento.";
            }
            if (!state.description.trim()) {
                nextErrors.description = "La descripción es obligatoria.";
            }
            if (!state.unlimited && state.participantLimit.trim()) {
                const parsed = Number(state.participantLimit);
                if (Number.isNaN(parsed) || parsed <= 0) {
                    nextErrors.participantLimit = "Ingresa un número válido mayor a 0.";
                }
            }
            if (!state.unlimited && !state.participantLimit.trim()) {
                nextErrors.participantLimit = "Define el límite o marca 'Sin límite'.";
            }
            if (!state.flyer) {
                nextErrors.flyer = "Debes subir el folleto del evento.";
            }
            return nextErrors;
        },
        []
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
                setErrors((prev) => ({ ...prev, flyer: "Solo se aceptan archivos JPG o PNG." }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            if (!validSize) {
                setErrors((prev) => ({ ...prev, flyer: "El archivo no puede superar los 2MB." }));
                setForm((prev) => ({ ...prev, flyer: null }));
                markTouched("flyer");
                return;
            }

            setForm((prev) => ({ ...prev, flyer: file }));
            setErrors((prev) => ({ ...prev, flyer: undefined }));
            markTouched("flyer");
        },
        [markTouched]
    );

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
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

        setSubmitting(true);
        window.setTimeout(() => {
            console.info("Submitting event", {
                ...form,
                participantLimit: form.unlimited ? null : Number(form.participantLimit),
                cityId: form.city?.id,
            });
            setSubmitting(false);
            setForm({ ...INITIAL_FORM });
            setCityQuery("");
            setTouched({});
            setErrors({});
            navigate("/events");
        }, 500);
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
                        <p className="eyebrow">Nuevo evento</p>
                        <h1>Crear evento</h1>
                        <p className="event-create-header__lead">
                            Completa los siguientes datos para publicar tu evento y llegar a más estudiantes.
                        </p>
                    </div>
                    <div className="event-create-meta">
                        <span>Campos marcados con * son obligatorios.</span>
                    </div>
                </header>

                <form className="event-form" onSubmit={handleSubmit} noValidate>
                    <TextField
                        label={
                            <span className="input-label">
                                Nombre del evento <span className="required-indicator" aria-hidden="true">*</span>
                            </span>
                        }
                        placeholder="Ingresa el nombre del evento"
                        value={form.name}
                        onChange={handleTextChange("name")}
                        onBlur={() => markTouched("name")}
                        errorText={touched.name ? errors.name : undefined}
                    />

                    <CatalogAutocompleteField
                        label="Ciudad"
                        placeholder="Escribe para buscar ciudad..."
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
                                    Fecha <span className="required-indicator" aria-hidden="true">*</span>
                                </label>
                                <input
                                    id="field-date"
                                    type="date"
                                    className={classNames("input-control", touched.date && errors.date && "input-control--error")}
                                    value={form.date}
                                    onChange={handleDateChange}
                                    onBlur={() => markTouched("date")}
                                    placeholder="dd/mm/yyyy"
                                />
                                {touched.date && errors.date && (
                                    <p className="form-field__text form-field__text--error">{errors.date}</p>
                                )}
                            </div>

                            <div className="form-field">
                                <label className="form-field__label" htmlFor="field-time">
                                    Hora
                                </label>
                                <input
                                    id="field-time"
                                    type="time"
                                    placeholder="--:--"
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
                            label="Todo el día"
                            checked={form.allDay}
                            onChange={(event) => handleAllDayToggle(event.target.checked)}
                            containerClassName="event-form__checkbox"
                        />
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="field-description">
                            Descripción <span className="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <textarea
                            id="field-description"
                            className={classNames("input-control", touched.description && errors.description && "input-control--error")}
                            placeholder="Ingresa la descripción del evento"
                            value={form.description}
                            onChange={handleTextChange("description")}
                            onBlur={() => markTouched("description")}
                        />
                        {touched.description && errors.description && (
                            <p className="form-field__text form-field__text--error">{errors.description}</p>
                        )}
                    </div>

                    <TextField
                        label="Dirección"
                        placeholder="Ingresa la dirección del evento (si es que tiene una):"
                        value={form.address}
                        onChange={handleTextChange("address")}
                        onBlur={() => markTouched("address")}
                    />

                    <div className="event-form__row event-form__row--limit">
                        <div className="form-field">
                            <label className="form-field__label" htmlFor="field-limit">
                                Límite de participantes
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
                            label="Sin límite"
                            checked={form.unlimited}
                            onChange={(event) => handleUnlimitedToggle(event.target.checked)}
                            containerClassName="event-form__checkbox"
                        />
                    </div>

                    <div className="form-field">
                        <label className="input-label" htmlFor="field-flyer">
                            Subir folleto para el evento
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
                                            Cambiar
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="upload-placeholder">
                                    <UploadIcon />
                                    <p>Subir folleto del evento ( PNG o JPG )</p>
                                    <p className="upload-hint">JPG o PNG, máximo 2MB</p>
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

                    <div className="event-form__actions">
                        <Button type="button" variant="ghost" onClick={handleCancel} disabled={submitting}>
                            Cancelar
                        </Button>
                        <Button type="submit" variant="primary" disabled={submitting}>
                            {submitting ? "Creando..." : "Crear evento"}
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

import { useRef, useState } from "react";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";
import type { CatalogOption, CatalogSearchFn } from "@/lib/api/catalog";
import { useAsyncCatalogOptions, useDropdownState } from "@/components/form/catalogAutocompleteHooks";

interface InterestMultiSelectFieldProps {
    label: string;
    name: string;
    placeholder: string;
    selected: CatalogOption[];
    onChange: (next: CatalogOption[]) => void;
    fetcher: CatalogSearchFn;
    helper?: string;
    error?: string;
    touched?: boolean;
    required?: boolean;
}

export default function InterestMultiSelectField({
    label,
    name,
    placeholder,
    selected,
    onChange,
    fetcher,
    helper,
    error,
    touched,
    required = true,
}: InterestMultiSelectFieldProps) {
    const { t } = useI18n();
    const [query, setQuery] = useState("");
    const { open, setOpen, ref } = useDropdownState();
    const inputRef = useRef<HTMLInputElement>(null);
    const { options, loading } = useAsyncCatalogOptions(fetcher, open, query);

    const available = options.filter((option) => !selected.some((item) => item.id === option.id));
    const showError = Boolean(error && touched);

    return (
        <div className={classNames("form-field", "autocomplete-field", "autocomplete-field--multiple", showError && "has-error")} ref={ref}>
            <label className="input-label" htmlFor={`field-${name}`}>
                {label}
                {required && (
                    <span className="required-indicator" aria-hidden="true">
                        *
                    </span>
                )}
            </label>
            <div className="input-with-addon">
                <input
                    id={`field-${name}`}
                    ref={inputRef}
                    className={classNames("input-control", showError && "input-control--error")}
                    value={query}
                    onFocus={() => setOpen(true)}
                    onChange={(event) => {
                        setQuery(event.target.value);
                        if (!open) {
                            setOpen(true);
                        }
                    }}
                    placeholder={placeholder}
                    autoComplete="off"
                />
                <button type="button" className="input-addon" onClick={() => setOpen((prev) => !prev)} aria-label={t("register.autocomplete.toggle")}>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
                </button>
            </div>

            <div
                className="chip-list"
                aria-live="polite"
                onMouseDown={(event) => {
                    const target = event.target as HTMLElement | null;
                    if (target?.closest("[data-chip-remove='true']")) {
                        return;
                    }
                    setOpen(false);
                }}
            >
                {selected.map((interest) => (
                    <span className="chip" key={interest.id}>
                        {interest.name}
                        <button
                            type="button"
                            data-chip-remove="true"
                            onClick={() => onChange(selected.filter((item) => item.id !== interest.id))}
                            aria-label={t("register.interests.remove", { values: { name: interest.name } })}
                        >
                            ×
                        </button>
                    </span>
                ))}
            </div>

            {selected.length === 0 && helper && <p className="form-field__text">{helper}</p>}
            {showError && <p className="form-field__text form-field__text--error">{error}</p>}

            <div className={classNames("autocomplete-panel", open && "is-open")} role="listbox" aria-multiselectable="true">
                {loading && <p className="autocomplete-status">{t("register.autocomplete.loading")}</p>}
                {!loading && available.length === 0 && (
                    <p className="autocomplete-status">{t("register.autocomplete.noResults")}</p>
                )}
                {!loading &&
                    available.map((option) => (
                        <button
                            type="button"
                            key={option.id}
                            className="autocomplete-option"
                            onMouseDown={(event) => event.preventDefault()}
                            onClick={() => {
                                onChange([...selected, option]);
                                setQuery("");
                                setOpen(true);
                                inputRef.current?.focus();
                            }}
                        >
                            {option.name}
                        </button>
                    ))}
            </div>
        </div>
    );
}

import { useId, useRef, type ChangeEvent } from "react";
import { classNames } from "@/lib/utils/classNames";
import type { CatalogOption, CatalogSearchFn } from "@/lib/api/catalog";
import { useI18n } from "@/lib/i18n";
import { useAsyncCatalogOptions, useDropdownState } from "@/components/form/catalogAutocompleteHooks";

interface CatalogAutocompleteFieldProps {
    label: string;
    placeholder: string;
    value: CatalogOption | null;
    query: string;
    onQueryChange: (next: string) => void;
    onChange: (option: CatalogOption | null) => void;
    fetcher: CatalogSearchFn;
    error?: string;
    required?: boolean;
    loadingLabel?: string;
    emptyLabel?: string;
    onBlur?: () => void;
}

export default function CatalogAutocompleteField({
    label,
    placeholder,
    value,
    query,
    onQueryChange,
    onChange,
    fetcher,
    error,
    required = false,
    loadingLabel,
    emptyLabel,
    onBlur,
}: CatalogAutocompleteFieldProps) {
    const { t } = useI18n();
    const { open, setOpen, ref: containerRef } = useDropdownState();
    const inputRef = useRef<HTMLInputElement>(null);
    const inputId = useId();
    const { options, loading } = useAsyncCatalogOptions(fetcher, open, query.trim(), 250);

    const showClear = Boolean(value);

    const handleSelect = (option: CatalogOption) => {
        onChange(option);
        onQueryChange(option.name);
        setOpen(false);
        inputRef.current?.blur();
    };

    const handleInput = (event: ChangeEvent<HTMLInputElement>) => {
        const nextValue = event.target.value;
        onQueryChange(nextValue);
        if (value) {
            onChange(null);
        }
        if (!open) {
            setOpen(true);
        }
    };

    const handleClear = () => {
        onQueryChange("");
        onChange(null);
        inputRef.current?.focus();
    };

    const handleInputBlur = () => {
        onBlur?.();
        setOpen(false);
    };

    const loadingText = loadingLabel ?? t("autocomplete.loading");
    const emptyText = emptyLabel ?? t("autocomplete.empty");
    const clearAria = t("autocomplete.clear");
    const toggleAria = t("autocomplete.toggle");

    return (
        <div className="form-field create-autocomplete" ref={containerRef}>
            <label className="input-label" htmlFor={inputId}>
                {label}
                {required && (
                    <span className="required-indicator" aria-hidden="true">
                        *
                    </span>
                )}
            </label>
            <div className="filters-field__control">
                <input
                    id={inputId}
                    className={classNames("input-control", error && "input-control--error")}
                    ref={inputRef}
                    type="text"
                    placeholder={placeholder}
                    value={query}
                    onChange={handleInput}
                    onFocus={() => setOpen(true)}
                    onBlur={handleInputBlur}
                    autoComplete="off"
                    spellCheck="false"
                    aria-autocomplete="list"
                    aria-expanded={open}
                    aria-haspopup="listbox"
                />
                {showClear && (
                    <button
                        type="button"
                        className="filters-field__icon-btn filters-field__icon-btn--clear"
                        onClick={handleClear}
                        aria-label={clearAria}
                    >
                        ×
                    </button>
                )}
                <button
                    type="button"
                    className={classNames(
                        "filters-field__icon-btn",
                        "filters-field__icon-btn--toggle",
                        open && "is-open"
                    )}
                    aria-label={toggleAria}
                    onClick={() => {
                        setOpen((prev) => !prev);
                        inputRef.current?.focus();
                    }}
                >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M6 9l6 6 6-6" />
                    </svg>
                </button>
            </div>
            <div className={classNames("autocomplete-panel", open && "is-open")} role="listbox" aria-label={label}>
                {loading && <p className="autocomplete-status">{loadingText}</p>}
                {!loading && open && options.length === 0 && (
                    <p className="autocomplete-status">{emptyText}</p>
                )}
                {!loading &&
                    options.map((option) => (
                        <button
                            key={option.id}
                            type="button"
                            className="autocomplete-option"
                            role="option"
                            aria-selected={value?.id === option.id}
                            onMouseDown={(event) => event.preventDefault()}
                            onClick={() => handleSelect(option)}
                        >
                            {option.name}
                        </button>
                    ))}
            </div>
            {error && <p className="form-field__text form-field__text--error">{error}</p>}
        </div>
    );
}

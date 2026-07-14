import { useEffect, useId, useLayoutEffect, useMemo, useRef, useState } from "react";
import type { FormEvent } from "react";
import { useQuery } from "@tanstack/react-query";
import Button from "@/components/ui/Button";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";
import { searchCities, searchInterests, searchUniversities, type CatalogOption, type CatalogSearchFn } from "@/lib/api/catalog";
import type { ListingFiltersState } from "@/hooks/useListingFilters";
import { EMPTY_LISTING_FILTERS } from "@/hooks/useListingFilters";

export type ListingFilterMode = "events" | "journeys";

interface ListingFiltersDialogProps {
    mode: ListingFilterMode;
    open: boolean;
    filters: ListingFiltersState;
    anchorRef?: { current: HTMLElement | null } | null;
    onClose: () => void;
    onApply: (filters: ListingFiltersState) => void;
    onReset: () => void;
}

interface CatalogSelectFieldProps {
    label: string;
    placeholder: string;
    value: CatalogOption | null;
    onChange: (option: CatalogOption | null) => void;
    fetcher: CatalogSearchFn;
    catalogKey: string;
    loadingLabel: string;
    emptyLabel: string;
    toggleLabel: string;
    changeLabel: string;
}

interface DateFieldProps {
    label: string;
    value: string;
    onChange: (value: string) => void;
    placeholder: string;
    max?: string;
    min?: string;
    helperText?: string;
    error?: string;
}

interface SelectFieldProps {
    label: string;
    value: string;
    onChange: (value: string) => void;
    options: Array<{ value: string; label: string }>;
}

interface CheckboxFieldProps {
    label: string;
    checked: boolean;
    onChange: (checked: boolean) => void;
}

const CalendarIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8} aria-hidden="true">
        <rect x="3" y="5" width="18" height="16" rx="2" ry="2" />
        <path d="M3 10h18" />
        <path d="M8 3v4" />
        <path d="M16 3v4" />
    </svg>
);

const FilterIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path d="M4 6h16" />
        <path d="M6 12h12" />
        <path d="M10 18h4" />
    </svg>
);

const ResetIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path d="m18 6-12 12" />
        <path d="m6 6 12 12" />
    </svg>
);

export default function ListingFiltersDialog({ open, ...props }: ListingFiltersDialogProps) {
    if (!open) {
        return null;
    }

    // El panel se monta recién al abrirse, así el borrador arranca desde los
    // filtros vigentes sin sincronizarlo con un efecto.
    return <ListingFiltersPanel {...props} />;
}

type ListingFiltersPanelProps = Omit<ListingFiltersDialogProps, "open">;

function ListingFiltersPanel({ mode, filters, anchorRef, onClose, onApply, onReset }: ListingFiltersPanelProps) {
    const { t } = useI18n();
    const [draft, setDraft] = useState<ListingFiltersState>(filters);
    const titleId = useId();
    const panelRef = useRef<HTMLDivElement>(null);
    const lastFocusedRef = useRef<HTMLElement | null>(null);
    const [position, setPosition] = useState<{ top: number; left: number }>({ top: 120, left: 16 });

    useEffect(() => {
        const anchor = anchorRef?.current;
        lastFocusedRef.current = document.activeElement as HTMLElement | null;
        const focusable = panelRef.current?.querySelectorAll<HTMLElement>(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        focusable?.[0]?.focus();
        const handleKey = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                onClose();
                return;
            }
            if (event.key !== "Tab") {
                return;
            }
            const items = panelRef.current?.querySelectorAll<HTMLElement>(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
            );
            if (!items || items.length === 0) {
                return;
            }
            const first = items[0];
            const last = items[items.length - 1];
            const active = document.activeElement as HTMLElement | null;
            if (event.shiftKey && active === first) {
                event.preventDefault();
                last.focus();
            } else if (!event.shiftKey && active === last) {
                event.preventDefault();
                first.focus();
            }
        };
        document.addEventListener("keydown", handleKey);
        return () => {
            document.removeEventListener("keydown", handleKey);
            if (anchor) {
                anchor.focus();
            } else {
                lastFocusedRef.current?.focus();
            }
        };
    }, [anchorRef, onClose]);

    useLayoutEffect(() => {
        const updatePosition = () => {
            const anchor = anchorRef?.current;
            const panel = panelRef.current;
            const spacing = 8;
            const fallbackWidth = Math.min(980, window.innerWidth - 32);
            const fallbackHeight = Math.min(620, window.innerHeight - 32);
            const panelWidth = panel?.offsetWidth ?? fallbackWidth;
            const panelHeight = panel?.offsetHeight ?? fallbackHeight;
            const maxLeft = Math.max(16, window.innerWidth - panelWidth - 16);
            const maxTop = Math.max(16, window.innerHeight - panelHeight - 16);

            if (anchor) {
                const rect = anchor.getBoundingClientRect();
                const desiredLeft = window.innerWidth / 2 - panelWidth / 2;
                const safeLeft = Math.min(Math.max(16, desiredLeft), maxLeft);
                const desiredTop = rect.bottom + spacing;
                const safeTop = Math.min(Math.max(16, desiredTop), maxTop);
                setPosition({ top: safeTop, left: safeLeft });
            } else {
                const centeredLeft = Math.max(16, (window.innerWidth - panelWidth) / 2);
                setPosition({ top: Math.min(160, maxTop), left: centeredLeft });
            }
        };

        updatePosition();
        window.addEventListener("resize", updatePosition);
        window.addEventListener("scroll", updatePosition, true);
        return () => {
            window.removeEventListener("resize", updatePosition);
            window.removeEventListener("scroll", updatePosition, true);
        };
    }, [anchorRef]);

    useEffect(() => {
        const handlePointerDown = (event: MouseEvent) => {
            const target = event.target as Node;
            if (panelRef.current?.contains(target)) {
                return;
            }
            if (anchorRef?.current && anchorRef.current.contains(target)) {
                return;
            }
            onClose();
        };
        document.addEventListener("mousedown", handlePointerDown);
        return () => document.removeEventListener("mousedown", handlePointerDown);
    }, [anchorRef, onClose]);

    const labels = useMemo(() => {
        if (mode === "events") {
            return {
                title: t("event.filter.title"),
                cityLabel: t("event.filter.city", { defaultValue: t("journey.filter.destination") }),
                cityPlaceholder: t("event.filter.city.placeholder"),
                afterLabel: t("event.filter.afterDate"),
                beforeLabel: t("event.filter.beforeDate"),
                interestLabel: t("event.filter.interest"),
                interestPlaceholder: t("event.filter.interest.placeholder"),
                universityLabel: t("university.detail.title", { defaultValue: "University" }),
                universityPlaceholder: t("event.university.hint", { defaultValue: "Search for a university..." }),
                minRatingLabel: t("event.filter.minRating", { defaultValue: "Minimum rating" }),
                capacityLabel: t("event.filter.hasCapacity", { defaultValue: "Only show events with available spots" }),
                applyLabel: t("event.filter.button"),
                resetLabel: t("event.filter.reset"),
                closeLabel: t("event.filter.close"),
            } as const;
        }
        return {
            title: t("journey.filter.title"),
            cityLabel: t("journey.filter.destination"),
            cityPlaceholder: t("journey.filter.destination.placeholder"),
            afterLabel: t("journey.filter.startDate"),
            beforeLabel: t("journey.filter.endDate"),
            interestLabel: t("journey.filter.interest"),
            interestPlaceholder: t("journey.filter.interest.placeholder"),
            universityLabel: t("journey.filters.university", { defaultValue: "University" }),
            universityPlaceholder: t("journey.university.search", { defaultValue: "Search for a university..." }),
            minRatingLabel: "",
            capacityLabel: "",
            applyLabel: t("journey.filter.button"),
            resetLabel: t("journey.filter.reset"),
            closeLabel: t("journey.filter.close"),
        } as const;
    }, [mode, t]);

    const formatLabel = t("listing.filters.dateFormat");
    const loadingLabel = t("listing.filters.autocomplete.loading");
    const emptyLabel = t("listing.filters.autocomplete.empty");
    const toggleLabel = t("listing.filters.autocomplete.toggle");
    const changeLabel = t("register.autocomplete.change");

    const cityOption = draft.cityId && draft.cityName ? { id: draft.cityId, name: draft.cityName } : null;
    const universityOption = draft.universityId && draft.universityName ? { id: draft.universityId, name: draft.universityName } : null;
    const interestOption = draft.interestId && draft.interestName ? { id: draft.interestId, name: draft.interestName } : null;
    const minRatingOptions = [
        { value: "", label: t("listing.filters.any", { defaultValue: "Any" }) },
        { value: "5", label: "5+" },
        { value: "4", label: "4+" },
        { value: "3", label: "3+" },
        { value: "2", label: "2+" },
        { value: "1", label: "1+" },
    ];

    const hasDateConflict = Boolean(draft.afterDate && draft.beforeDate && draft.afterDate > draft.beforeDate);
    const dateErrorMessage = hasDateConflict
        ? t("listing.filters.error.dateRange", {
              values: { afterLabel: labels.afterLabel, beforeLabel: labels.beforeLabel },
          })
        : undefined;

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (hasDateConflict) {
            return;
        }
        onApply(draft);
    };

    const handleReset = () => {
        setDraft({ ...EMPTY_LISTING_FILTERS });
        onReset();
    };

    const handleCityChange = (option: CatalogOption | null) => {
        setDraft((prev) => ({
            ...prev,
            cityId: option?.id ?? null,
            cityName: option?.name ?? "",
        }));
    };

    const handleInterestChange = (option: CatalogOption | null) => {
        setDraft((prev) => ({
            ...prev,
            interestId: option?.id ?? null,
            interestName: option?.name ?? "",
        }));
    };

    const handleUniversityChange = (option: CatalogOption | null) => {
        setDraft((prev) => ({
            ...prev,
            universityId: option?.id ?? null,
            universityName: option?.name ?? "",
        }));
    };

    const handleDateChange = (field: "afterDate" | "beforeDate") => (value: string) => {
        setDraft((prev) => ({
            ...prev,
            [field]: value,
        }));
    };

    const handleMinRatingChange = (value: string) => {
        setDraft((prev) => ({
            ...prev,
            minRating: value ? Number(value) : null,
        }));
    };

    const handleHasCapacityChange = (checked: boolean) => {
        setDraft((prev) => ({
            ...prev,
            hasCapacity: checked,
        }));
    };

    return (
        <div
            ref={panelRef}
            className="filters-dropdown"
            role="dialog"
            aria-modal="true"
            aria-labelledby={titleId}
            style={{ top: `${position.top}px`, left: `${position.left}px` }}
        >
            <header className="filters-header">
                <div>
                    <p className="eyebrow">{t("listing.filters")}</p>
                    <h2 id={titleId}>{labels.title}</h2>
                </div>
                <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="filters-close-btn"
                    aria-label={labels.closeLabel}
                    onClick={onClose}
                >
                    ×
                </Button>
            </header>

            <form className="filters-form" onSubmit={handleSubmit} noValidate>
                <div className="filters-grid">
                    <CatalogSelectField
                        label={labels.cityLabel}
                        placeholder={labels.cityPlaceholder}
                        value={cityOption}
                        onChange={handleCityChange}
                        fetcher={searchCities}
                        catalogKey="city"
                        loadingLabel={loadingLabel}
                        emptyLabel={emptyLabel}
                        toggleLabel={toggleLabel}
                        changeLabel={changeLabel}
                    />
                    <CatalogSelectField
                        label={labels.universityLabel}
                        placeholder={labels.universityPlaceholder}
                        value={universityOption}
                        onChange={handleUniversityChange}
                        fetcher={searchUniversities}
                        catalogKey={`${mode}-university`}
                        loadingLabel={loadingLabel}
                        emptyLabel={emptyLabel}
                        toggleLabel={toggleLabel}
                        changeLabel={changeLabel}
                    />
                    <DateField
                        label={labels.afterLabel}
                        placeholder="dd / mm / yyyy"
                        value={draft.afterDate}
                        onChange={handleDateChange("afterDate")}
                        max={draft.beforeDate || undefined}
                        helperText={formatLabel}
                    />
                    <DateField
                        label={labels.beforeLabel}
                        placeholder="dd / mm / yyyy"
                        value={draft.beforeDate}
                        onChange={handleDateChange("beforeDate")}
                        min={draft.afterDate || undefined}
                        helperText={formatLabel}
                        error={dateErrorMessage}
                    />
                    <CatalogSelectField
                        label={labels.interestLabel}
                        placeholder={labels.interestPlaceholder}
                        value={interestOption}
                        onChange={handleInterestChange}
                        fetcher={searchInterests}
                        catalogKey="interest"
                        loadingLabel={loadingLabel}
                        emptyLabel={emptyLabel}
                        toggleLabel={toggleLabel}
                        changeLabel={changeLabel}
                    />
                    {mode === "events" && (
                        <>
                            <SelectField
                                label={labels.minRatingLabel}
                                value={draft.minRating ? String(draft.minRating) : ""}
                                onChange={handleMinRatingChange}
                                options={minRatingOptions}
                            />
                            <CheckboxField
                                label={labels.capacityLabel}
                                checked={draft.hasCapacity}
                                onChange={handleHasCapacityChange}
                            />
                        </>
                    )}
                </div>

                <div className="filters-actions">
                    <Button type="button" variant="danger" onClick={handleReset} className="filters-action">
                        <span className="filters-action__icon" aria-hidden="true">
                            <ResetIcon />
                        </span>
                        {labels.resetLabel}
                    </Button>
                    <Button type="submit" variant="primary" className="filters-action" disabled={hasDateConflict}>
                        <span className="filters-action__icon" aria-hidden="true">
                            <FilterIcon />
                        </span>
                        {labels.applyLabel}
                    </Button>
                </div>
            </form>
        </div>
    );
}

const useDebouncedValue = (value: string, delay = 250) => {
    const [debounced, setDebounced] = useState(value);

    useEffect(() => {
        const handle = window.setTimeout(() => setDebounced(value), delay);
        return () => window.clearTimeout(handle);
    }, [value, delay]);

    return debounced;
};

function CatalogSelectField({
    label,
    placeholder,
    value,
    onChange,
    fetcher,
    catalogKey,
    loadingLabel,
    emptyLabel,
    toggleLabel,
    changeLabel,
}: CatalogSelectFieldProps) {
    const inputId = useId();
    const containerRef = useRef<HTMLDivElement>(null);
    const [query, setQuery] = useState(value?.name ?? "");
    const [open, setOpen] = useState(false);
    const debouncedQuery = useDebouncedValue(query);
    // El texto tipeado es un borrador de la opción elegida: si la selección
    // cambia desde afuera, el borrador vuelve a reflejarla.
    const [lastValue, setLastValue] = useState(value);
    if (lastValue !== value) {
        setLastValue(value);
        setQuery(value?.name ?? "");
    }

    useEffect(() => {
        if (!open) {
            return;
        }
        const handleClick = (event: MouseEvent) => {
            if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClick);
        return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    const { data: options = [], isLoading } = useQuery<CatalogOption[]>({
        queryKey: ["listing", "catalog", catalogKey, debouncedQuery],
        queryFn: ({ signal }) => fetcher(debouncedQuery, signal),
        enabled: open,
        staleTime: 60_000,
    });

    const handleSelect = (option: CatalogOption) => {
        onChange(option);
        setOpen(false);
    };

    return (
        <div className={classNames("form-field", "autocomplete-field")}
            ref={containerRef}
        >
            <label className="input-label" htmlFor={inputId}>
                {label}
            </label>
            {value ? (
                <div className="selected-option">
                    <div>
                        <p className="selected-option__value">{value.name}</p>
                    </div>
                    <button
                        type="button"
                        className="selected-option__action"
                        onClick={() => {
                            onChange(null);
                            setQuery("");
                            setOpen(true);
                        }}
                    >
                        {changeLabel}
                    </button>
                </div>
            ) : (
                <>
                    <div className="input-with-addon">
                        <input
                            id={inputId}
                            className="input-control"
                            value={query}
                            placeholder={placeholder}
                            onFocus={() => setOpen(true)}
                            onChange={(event) => {
                                setQuery(event.target.value);
                                if (!open) {
                                    setOpen(true);
                                }
                            }}
                            autoComplete="off"
                            spellCheck="false"
                            aria-autocomplete="list"
                            aria-expanded={open}
                            aria-haspopup="listbox"
                        />
                        <button
                            type="button"
                            className="input-addon"
                            onClick={() => setOpen((prev) => !prev)}
                            aria-label={toggleLabel}
                        >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                            </svg>
                        </button>
                    </div>
                    <div className={classNames("autocomplete-panel", open && "is-open")}
                        role="listbox"
                        aria-label={label}
                    >
                        {isLoading && <p className="autocomplete-status">{loadingLabel}</p>}
                        {!isLoading && options.length === 0 && (
                            <p className="autocomplete-status">{emptyLabel}</p>
                        )}
                        {!isLoading &&
                            options.map((option) => (
                                <button
                                    type="button"
                                    key={option.id}
                                    className="autocomplete-option"
                                    onMouseDown={(event) => event.preventDefault()}
                                    onClick={() => handleSelect(option)}
                                >
                                    {option.name}
                                </button>
                            ))}
                    </div>
                </>
            )}
        </div>
    );
}

function DateField({ label, value, onChange, placeholder, max, min, helperText, error }: DateFieldProps) {
    const inputId = useId();
    const describedBy = error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined;
    return (
        <div className="form-field filters-field">
            <label className="form-field__label" htmlFor={inputId}>
                {label}
            </label>
            <div className="filters-field__control filters-field__control--date">
                <span className="filters-field__icon" aria-hidden="true">
                    <CalendarIcon />
                </span>
                <input
                    id={inputId}
                    type="date"
                    className={classNames("input-control", error && "input-control--error")}
                    value={value}
                    onChange={(event) => onChange(event.target.value)}
                    placeholder={placeholder}
                    max={max}
                    min={min}
                    aria-invalid={error ? true : undefined}
                    aria-describedby={describedBy}
                />
            </div>
            {helperText && !error && (
                <p id={describedBy} className="form-field__text">
                    {helperText}
                </p>
            )}
            {error && (
                <p id={describedBy} className="form-field__text form-field__text--error">
                    {error}
                </p>
            )}
        </div>
    );
}

function SelectField({ label, value, onChange, options }: SelectFieldProps) {
    const inputId = useId();
    return (
        <div className="form-field filters-field">
            <label className="form-field__label" htmlFor={inputId}>
                {label}
            </label>
            <select id={inputId} className="input-control" value={value} onChange={(event) => onChange(event.target.value)}>
                {options.map((option) => (
                    <option key={option.value || "empty"} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
        </div>
    );
}

function CheckboxField({ label, checked, onChange }: CheckboxFieldProps) {
    const { t } = useI18n();
    const inputId = useId();
    return (
        <div className="form-field filters-field">
            <label className="form-field__label" htmlFor={inputId}>
                {label}
            </label>
            <label className="selected-option" htmlFor={inputId}>
                <span className="selected-option__value">{checked ? t("common.on") : t("common.off")}</span>
                <input
                    id={inputId}
                    type="checkbox"
                    checked={checked}
                    onChange={(event) => onChange(event.target.checked)}
                />
            </label>
        </div>
    );
}

import { useMemo, useState } from "react";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";

interface Option {
    id: number;
    name: string;
}

interface SingleSelectAutocompleteProps {
    id: string;
    options: Option[];
    value: Option | null;
    placeholder: string;
    error?: string;
    onChange: (option: Option | null) => void;
}

export default function SingleSelectAutocomplete({
    id,
    options,
    value,
    placeholder,
    error,
    onChange,
}: SingleSelectAutocompleteProps) {
    const { t } = useI18n();
    const [query, setQuery] = useState(value?.name ?? "");
    const [open, setOpen] = useState(false);
    // El texto tipeado es un borrador de la opción elegida: si la selección
    // cambia desde afuera, el borrador vuelve a reflejarla.
    const [lastValue, setLastValue] = useState(value);
    if (lastValue !== value) {
        setLastValue(value);
        setQuery(value?.name ?? "");
    }

    const filtered = useMemo(() => {
        const normalized = query.trim().toLowerCase();
        if (!normalized) {
            return options;
        }
        return options.filter((option) => option.name.toLowerCase().includes(normalized));
    }, [options, query]);

    return (
        <div className="autocomplete-wrapper">
            <input
                id={id}
                type="text"
                className={classNames("autocomplete-input", error && "error")}
                placeholder={placeholder}
                value={query}
                onFocus={() => setOpen(true)}
                onBlur={() => setOpen(false)}
                onChange={(event) => {
                    const nextValue = event.target.value;
                    setQuery(nextValue);
                    setOpen(true);
                    if (!nextValue && value) {
                        onChange(null);
                    }
                }}
                autoComplete="off"
            />
            <div className="autocomplete-dropdown" style={{ display: open ? "block" : "none" }}>
                {filtered.map((option) => (
                    <div
                        key={option.id}
                        className={classNames("autocomplete-item", value?.id === option.id && "selected")}
                        onMouseDown={(event) => event.preventDefault()}
                        onClick={() => {
                            onChange(option);
                            setQuery(option.name);
                            setOpen(false);
                        }}
                    >
                        {option.name}
                    </div>
                ))}
            </div>
            <div className="selected-tags required-selected-tags">
                {value && (
                    <span className="selected-tag">
                        {value.name}
                        <button
                            type="button"
                            className="tag-remove"
                            aria-label={t("common.remove")}
                            onClick={() => {
                                onChange(null);
                                setQuery("");
                            }}
                        >
                            ×
                        </button>
                    </span>
                )}
            </div>
        </div>
    );
}

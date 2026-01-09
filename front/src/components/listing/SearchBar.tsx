import type { FormEvent } from "react";
import { classNames } from "@/lib/utils/classNames";

interface SearchBarProps {
    value: string;
    placeholder: string;
    onChange: (value: string) => void;
    onSubmit?: (value: string) => void;
    ariaLabel?: string;
}

export default function SearchBar({ value, placeholder, onChange, onSubmit, ariaLabel }: SearchBarProps) {
    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onSubmit?.(value);
    };

    return (
        <form className="search-bar" onSubmit={handleSubmit}>
            <input
                className="search-bar__input"
                value={value}
                onChange={(event) => onChange(event.target.value)}
                placeholder={placeholder}
            />
            <button
                type="submit"
                className={classNames("search-bar__button", value && "is-active")}
                aria-label={ariaLabel || "Buscar"}
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                    <circle cx="11" cy="11" r="8" />
                    <path d="m21 21-4.3-4.3" />
                </svg>
            </button>
        </form>
    );
}

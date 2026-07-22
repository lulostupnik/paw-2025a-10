import { useState, type FormEvent } from "react";
import { classNames } from "@/lib/utils/classNames";

type SearchBarProps = {
    placeholder: string;
    onSubmit?: (value: string) => void;
    ariaLabel?: string;
} & (
    | {
          value: string;
          onChange: (value: string) => void;
          defaultValue?: never;
      }
    | {
          defaultValue?: string;
          onChange?: (value: string) => void;
          value?: never;
      }
);

export default function SearchBar(props: SearchBarProps) {
    const [draftValue, setDraftValue] = useState(props.defaultValue ?? "");
    const value = typeof props.value === "string" ? props.value : draftValue;

    const handleChange = (nextValue: string) => {
        if (typeof props.value !== "string") {
            setDraftValue(nextValue);
        }
        props.onChange?.(nextValue);
    };

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        props.onSubmit?.(value);
    };

    return (
        <form className="search-bar" onSubmit={handleSubmit}>
            <input
                className="search-bar__input"
                value={value}
                onChange={(event) => handleChange(event.target.value)}
                placeholder={props.placeholder}
            />
            <button
                type="submit"
                className={classNames("search-bar__button", value && "is-active")}
                aria-label={props.ariaLabel || "Buscar"}
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                    <circle cx="11" cy="11" r="8" />
                    <path d="m21 21-4.3-4.3" />
                </svg>
            </button>
        </form>
    );
}

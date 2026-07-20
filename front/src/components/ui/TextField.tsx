import { useId } from "react";
import type { InputHTMLAttributes, ReactNode } from "react";
import { classNames } from "@/lib/utils/classNames";

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
    label: ReactNode;
    helperText?: string;
    helperTextClassName?: string;
    errorText?: string;
    containerClassName?: string;
}

export default function TextField({
    label,
    helperText,
    helperTextClassName,
    errorText,
    id,
    className,
    containerClassName,
    ...props
}: TextFieldProps) {
    const autoId = useId();
    const inputId = id ?? autoId;
    const describedBy = [helperText ? `${inputId}-helper` : null, errorText ? `${inputId}-error` : null]
        .filter(Boolean)
        .join(" ") || undefined;

    return (
        <div className={classNames("form-field", containerClassName)}>
            <label className="form-field__label" htmlFor={inputId}>
                {label}
            </label>
            <input
                id={inputId}
                className={classNames("input-control", errorText && "input-control--error", className)}
                aria-invalid={errorText ? true : undefined}
                aria-describedby={describedBy}
                {...props}
            />
            {helperText ? (
                <p id={`${inputId}-helper`} className={classNames("form-field__text", helperTextClassName)}>
                    {helperText}
                </p>
            ) : null}
            {errorText ? (
                <p id={`${inputId}-error`} className="form-field__text form-field__text--error">
                    {errorText}
                </p>
            ) : null}
        </div>
    );
}

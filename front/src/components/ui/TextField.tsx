import { useId } from "react";
import type { InputHTMLAttributes } from "react";
import { classNames } from "@/lib/utils/classNames";

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
    helperText?: string;
    errorText?: string;
    containerClassName?: string;
}

export default function TextField({
    label,
    helperText,
    errorText,
    id,
    className,
    containerClassName,
    ...props
}: TextFieldProps) {
    const autoId = useId();
    const inputId = id ?? autoId;
    const describedBy = errorText ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined;

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
            {errorText ? (
                <p id={`${inputId}-error`} className="form-field__text form-field__text--error">
                    {errorText}
                </p>
            ) : helperText ? (
                <p id={`${inputId}-helper`} className="form-field__text">
                    {helperText}
                </p>
            ) : null}
        </div>
    );
}

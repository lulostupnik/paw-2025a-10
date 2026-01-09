import { forwardRef, useId } from "react";
import type { InputHTMLAttributes } from "react";
import { classNames } from "@/lib/utils/classNames";

interface CheckboxProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "type"> {
    label: string;
    description?: string;
    errorText?: string;
    containerClassName?: string;
}

const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(function Checkbox(
    { label, description, errorText, id, className, containerClassName, ...props },
    ref
) {
    const autoId = useId();
    const inputId = id ?? autoId;
    const descriptionId = description ? `${inputId}-description` : undefined;
    const errorId = errorText ? `${inputId}-error` : undefined;
    const describedBy = errorText ? errorId : descriptionId;

    return (
        <div className={classNames("form-field", containerClassName)}>
            <label className={classNames("checkbox-field", errorText && "checkbox-field--error")} htmlFor={inputId}>
                <input
                    ref={ref}
                    id={inputId}
                    type="checkbox"
                    className={classNames("checkbox-field__input", className)}
                    aria-invalid={errorText ? true : undefined}
                    aria-describedby={describedBy}
                    {...props}
                />
                <span className="checkbox-field__box" aria-hidden="true" />
                <div>
                    <span className="checkbox-field__label-text">{label}</span>
                    {description && (
                        <span id={descriptionId} className="checkbox-field__description">
                            {description}
                        </span>
                    )}
                </div>
            </label>
            {errorText && (
                <p id={errorId} className="form-field__text form-field__text--error">
                    {errorText}
                </p>
            )}
        </div>
    );
});

Checkbox.displayName = "Checkbox";

export default Checkbox;

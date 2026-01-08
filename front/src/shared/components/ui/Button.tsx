import { forwardRef } from "react";
import type { ButtonHTMLAttributes } from "react";
import { classNames } from "../../utils/classNames";

type ButtonVariant = "primary" | "secondary" | "outline" | "ghost" | "danger";
type ButtonSize = "sm" | "md" | "lg";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: ButtonVariant;
    size?: ButtonSize;
    fullWidth?: boolean;
}

const Button = forwardRef<HTMLButtonElement, ButtonProps>(function Button(
    { variant = "primary", size = "md", fullWidth = false, className, type = "button", ...props },
    ref
) {
    const classes = classNames(
        "btn",
        `btn--${variant}`,
        size !== "md" && `btn--${size}`,
        fullWidth && "btn--full",
        className
    );

    return (
        <button ref={ref} className={classes} type={type} {...props} />
    );
});

Button.displayName = "Button";

export default Button;

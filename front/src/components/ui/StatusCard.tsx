import type { ReactNode } from "react";
import { classNames } from "@/lib/utils/classNames";

export type StatusVariant = "info" | "success" | "warning" | "error";

const defaultIcons: Record<StatusVariant, ReactNode> = {
    success: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <circle cx="12" cy="12" r="10" />
            <path d="m9 12 2 2 4-4" />
        </svg>
    ),
    info: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <circle cx="12" cy="12" r="10" />
            <path d="M12 16v-4" />
            <path d="M12 8h.01" />
        </svg>
    ),
    warning: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0Z" />
            <path d="M12 9v4" />
            <path d="M12 17h.01" />
        </svg>
    ),
    error: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <circle cx="12" cy="12" r="10" />
            <path d="M15 9 9 15" />
            <path d="M9 9l6 6" />
        </svg>
    ),
};

interface StatusCardProps {
    title: ReactNode;
    description?: ReactNode;
    children?: ReactNode;
    actions?: ReactNode;
    variant?: StatusVariant;
    icon?: ReactNode;
    className?: string;
}

export default function StatusCard({
    title,
    description,
    children,
    actions,
    variant = "info",
    icon,
    className,
}: StatusCardProps) {
    const resolvedIcon = icon ?? defaultIcons[variant];

    return (
        <div className={classNames("status-card", `status-card--${variant}`, className)}>
            <div className="status-card__icon" aria-hidden="true">
                {resolvedIcon}
            </div>
            <div className="status-card__body">
                <h3>{title}</h3>
                {description && <p>{description}</p>}
                {children}
                {actions && <div className="status-card__actions">{actions}</div>}
            </div>
        </div>
    );
}

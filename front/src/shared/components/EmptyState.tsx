import type { ReactNode } from "react";
import { classNames } from "../utils/classNames";

interface EmptyStateProps {
    title: string;
    description?: string;
    action?: ReactNode;
    className?: string;
}

export default function EmptyState({ title, description, action, className }: EmptyStateProps) {
    return (
        <div className={classNames("empty-state", className)}>
            <div className="empty-state__icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M12 9v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                </svg>
            </div>
            <p className="empty-state__title">{title}</p>
            {description && <p className="empty-state__description">{description}</p>}
            {action && <div className="empty-state__action">{action}</div>}
        </div>
    );
}

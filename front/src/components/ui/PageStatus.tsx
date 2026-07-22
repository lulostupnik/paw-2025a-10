import type { ReactNode } from "react";
import { classNames } from "@/lib/utils/classNames";

interface PageStatusProps {
    message: ReactNode;
    variant?: "loading" | "error";
    className?: string;
    compact?: boolean;
}

export default function PageStatus({ message, variant = "loading", className, compact = false }: PageStatusProps) {
    return (
        <div className={classNames("page-status", compact && "page-status--compact", className)}>
            <div
                className={classNames(
                    "status-icon",
                    variant === "loading" && "status-icon--loading",
                    variant === "error" && "status-icon--error"
                )}
                aria-hidden="true"
            >
                {variant === "loading" ? (
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M21 12a9 9 0 1 1-2.64-6.36" />
                        <path d="M21 3v6h-6" />
                    </svg>
                ) : (
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10" />
                        <line x1="12" y1="8" x2="12" y2="12" />
                        <line x1="12" y1="16" x2="12.01" y2="16" />
                    </svg>
                )}
            </div>
            <p className="page-status__message">{message}</p>
        </div>
    );
}

import { ReactNode } from "react";
import { Link } from "react-router-dom";
import { useI18n } from "../i18n";

export type ErrorVariant = "400" | "403" | "404" | "405" | "415" | "500";

const iconPaths: Record<ErrorVariant, ReactNode> = {
    "404": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <path d="M14 3v4a1 1 0 0 0 1 1h4" />
            <path d="M5 8V5a2 2 0 0 1 2-2h7l5 5v11a2 2 0 0 1-2 2h-5" />
            <circle cx="8" cy="16" r="6" />
            <path d="m9.5 14.5 2.5 2.5m0-2.5-2.5 2.5" />
        </svg>
    ),
    "403": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            <path d="m14.5 9-5 5m0-5 5 5" />
        </svg>
    ),
    "500": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242" />
            <path d="M12 12v9" />
            <path d="m8 17 4 4 4-4" />
        </svg>
    ),
    "400": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <circle cx="12" cy="12" r="10" />
            <path d="M12 6v6" />
            <path d="M12 18h.01" />
        </svg>
    ),
    "405": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <circle cx="12" cy="12" r="10" />
            <path d="M15 9 9 15" />
            <path d="M9 9l6 6" />
        </svg>
    ),
    "415": (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
            <rect x="3" y="4" width="18" height="16" rx="2" />
            <path d="m9 10 2 2 4-4" />
            <path d="M3 8h18" />
        </svg>
    ),
};

const helpKeys: Partial<Record<ErrorVariant, string[]>> = {
    "404": ["error.404.help.1", "error.404.help.2"],
    "403": ["error.403.help.1", "error.403.help.2"],
    "500": ["error.500.help.1", "error.500.help.2"],
    "400": ["error.400.help.1", "error.400.help.2"],
    "405": ["error.405.help.1"],
    "415": ["error.415.help.1"],
};

interface ErrorStateProps {
    variant: ErrorVariant;
}

export default function ErrorState({ variant }: ErrorStateProps) {
    const { t } = useI18n();
    const icon = iconPaths[variant];
    const helpItems = helpKeys[variant] ?? [];

    return (
        <div className="page-shell error-page">
            <div className="error-card">
                <div className="error-icon-container" aria-hidden="true">
                    {icon}
                </div>
                <span className="error-code">{t(`error.${variant}.code`)}</span>
                <h1 className="error-title">{t(`error.${variant}.title`)}</h1>
                <p className="error-message">{t(`error.${variant}.message`)}</p>

                <div className="help-section">
                    <h2>{t("error.help.title")}</h2>
                    <ul>
                        {helpItems.map((key) => (
                            <li key={key}>{t(key)}</li>
                        ))}
                        <li>{t("error.help.contact")}</li>
                    </ul>
                </div>

                <div className="error-actions">
                    <Link to="/" className="primary-action">
                        {t("error.action.home")}
                    </Link>
                    <div className="secondary-actions">
                        <Link to="/journeys">{t("nav.journeys")}</Link>
                        <Link to="/events">{t("nav.events")}</Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

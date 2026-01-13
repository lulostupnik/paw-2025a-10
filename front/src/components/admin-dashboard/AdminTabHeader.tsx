import type { FormEvent, ReactNode } from "react";

interface AdminTabHeaderProps {
    title: string;
    searchPlaceholder: string;
    searchValue: string;
    searchButtonLabel: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: () => void;
    actions?: ReactNode;
    searchIconSrc: string;
}

export default function AdminTabHeader({
    title,
    searchPlaceholder,
    searchValue,
    searchButtonLabel,
    onSearchChange,
    onSearchSubmit,
    actions,
    searchIconSrc,
}: AdminTabHeaderProps) {
    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onSearchSubmit();
    };

    return (
        <div className="content-header">
            <h2>{title}</h2>
            <div className="action-bar">
                <div className="actions-container">
                    <form className="search-form" onSubmit={handleSubmit}>
                        <input
                            type="text"
                            name="search"
                            className="search-input"
                            placeholder={searchPlaceholder}
                            aria-label={searchPlaceholder}
                            value={searchValue}
                            onChange={(event) => onSearchChange(event.target.value)}
                        />
                        <button type="submit" className="btn-secondary" aria-label={searchButtonLabel}>
                            <img src={searchIconSrc} alt={searchButtonLabel} className="search-icon" />
                        </button>
                    </form>
                    {actions}
                </div>
            </div>
        </div>
    );
}

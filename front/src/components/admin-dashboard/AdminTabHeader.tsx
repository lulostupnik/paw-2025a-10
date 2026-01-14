import type { ReactNode } from "react";
import SearchBar from "@/components/listing/SearchBar";

interface AdminTabHeaderProps {
    title: string;
    searchPlaceholder: string;
    searchValue: string;
    searchButtonLabel: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    actions?: ReactNode;
}

export default function AdminTabHeader({
    title,
    searchPlaceholder,
    searchValue,
    searchButtonLabel,
    onSearchChange,
    onSearchSubmit,
    actions,
}: AdminTabHeaderProps) {
    return (
        <div className="content-header">
            <h2>{title}</h2>
            <div className="action-bar">
                <div className="actions-container">
                    <SearchBar
                        value={searchValue}
                        placeholder={searchPlaceholder}
                        ariaLabel={searchButtonLabel}
                        onChange={onSearchChange}
                        onSubmit={onSearchSubmit}
                    />
                    {actions}
                </div>
            </div>
        </div>
    );
}

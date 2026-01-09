import type { ReactNode } from "react";
import SearchBar from "./SearchBar";
import Tabs, { type TabOption } from "./Tabs";
import ToolbarButton from "./ToolbarButton";
import PrimaryActionButton from "./PrimaryActionButton";

interface ToolbarButtonConfig {
    id: string;
    label: string;
    icon?: ReactNode;
    onClick?: () => void;
}

interface ListingLayoutProps {
    title: string;
    searchPlaceholder: string;
    searchAriaLabel?: string;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit?: (value: string) => void;
    tabs: TabOption[];
    activeTab: string;
    onTabChange: (id: string) => void;
    toolbarButtons?: ToolbarButtonConfig[];
    createLabel: string;
    onCreate: () => void;
    children: ReactNode;
}

export default function ListingLayout({
    title,
    searchPlaceholder,
    searchValue,
    searchAriaLabel,
    onSearchChange,
    onSearchSubmit,
    tabs,
    activeTab,
    onTabChange,
    toolbarButtons,
    createLabel,
    onCreate,
    children,
}: ListingLayoutProps) {
    return (
        <div className="listing-page">
            <header className="listing-header">
                <div className="listing-header__row">
                    <h1 className="listing-title">{title}</h1>
                    <div className="listing-toolbar">
                        <SearchBar
                            value={searchValue}
                            placeholder={searchPlaceholder}
                            onChange={onSearchChange}
                            onSubmit={onSearchSubmit}
                            ariaLabel={searchAriaLabel}
                        />
                        <div className="listing-toolbar__actions">
                            {toolbarButtons?.map((button) => (
                                <ToolbarButton key={button.id} label={button.label} icon={button.icon} onClick={button.onClick} />
                            ))}
                            <PrimaryActionButton label={createLabel} onClick={onCreate} />
                        </div>
                    </div>
                </div>
                <Tabs tabs={tabs} activeTab={activeTab} onTabChange={onTabChange} />
            </header>

            <div className="listing-content">{children}</div>
        </div>
    );
}

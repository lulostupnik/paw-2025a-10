import type { ReactNode, Ref } from "react";
import SearchBar from "./SearchBar";
import Tabs, { type TabOption } from "./Tabs";
import ToolbarButton from "./ToolbarButton";
import PrimaryActionButton from "./PrimaryActionButton";

interface ToolbarButtonConfig {
    id: string;
    label: string;
    icon?: ReactNode;
    onClick?: () => void;
    ref?: Ref<HTMLButtonElement>;
}

interface ListingLayoutProps {
    title: string;
    searchPlaceholder: string;
    searchAriaLabel?: string;
    searchValue?: string;
    searchDefaultValue?: string;
    searchResetKey?: string;
    onSearchChange?: (value: string) => void;
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
    searchDefaultValue,
    searchResetKey,
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
                        {typeof searchValue === "string" ? (
                            <SearchBar
                                value={searchValue}
                                placeholder={searchPlaceholder}
                                onChange={onSearchChange ?? (() => undefined)}
                                onSubmit={onSearchSubmit}
                                ariaLabel={searchAriaLabel}
                            />
                        ) : (
                            <SearchBar
                                key={searchResetKey}
                                defaultValue={searchDefaultValue}
                                placeholder={searchPlaceholder}
                                onChange={onSearchChange}
                                onSubmit={onSearchSubmit}
                                ariaLabel={searchAriaLabel}
                            />
                        )}
                        <div className="listing-toolbar__actions">
                            {toolbarButtons?.map((button) => (
                                <ToolbarButton
                                    key={button.id}
                                    label={button.label}
                                    icon={button.icon}
                                    onClick={button.onClick}
                                    ref={button.ref}
                                />
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

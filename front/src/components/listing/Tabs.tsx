export interface TabOption {
    id: string;
    label: string;
}

interface TabsProps {
    tabs: TabOption[];
    activeTab: string;
    onTabChange: (id: string) => void;
}

export default function Tabs({ tabs, activeTab, onTabChange }: TabsProps) {
    return (
        <div className="listing-tabs-container">
            <div className="listing-tabs" role="tablist">
                {tabs.map((tab) => (
                    <button
                        key={tab.id}
                        role="tab"
                        aria-selected={tab.id === activeTab}
                        className={tab.id === activeTab ? "tab-button is-active" : "tab-button"}
                        type="button"
                        onClick={() => onTabChange(tab.id)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
        </div>
    );
}

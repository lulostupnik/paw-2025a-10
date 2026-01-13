import type { AdminDashboardTab } from "@/mocks/adminDashboard.mock";
import { classNames } from "@/lib/utils/classNames";

interface AdminDashboardTabsProps {
    activeTab: AdminDashboardTab;
    tabs: Array<{ key: AdminDashboardTab; label: string }>;
    onTabChange: (tab: AdminDashboardTab) => void;
}

export default function AdminDashboardTabs({ activeTab, tabs, onTabChange }: AdminDashboardTabsProps) {
    return (
        <div className="dashboard-tabs">
            {tabs.map((tab) => (
                <button
                    key={tab.key}
                    type="button"
                    className={classNames("tab-button", activeTab === tab.key && "active")}
                    onClick={() => onTabChange(tab.key)}
                >
                    {tab.label}
                </button>
            ))}
        </div>
    );
}

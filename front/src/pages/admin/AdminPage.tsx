import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import AdminDashboardTabs from "@/components/admin-dashboard/AdminDashboardTabs";
import JourneysTab from "@/components/admin-dashboard/JourneysTab";
import UsersTab from "@/components/admin-dashboard/UsersTab";
import EventsTab from "@/components/admin-dashboard/EventsTab";
import UniversitiesTab from "@/components/admin-dashboard/UniversitiesTab";
import InterestsTab from "@/components/admin-dashboard/InterestsTab";
import CitiesTab from "@/components/admin-dashboard/CitiesTab";
import CareersTab from "@/components/admin-dashboard/CareersTab";
import ReportsTab from "@/components/admin-dashboard/ReportsTab";
import { useAdminDashboardData } from "@/hooks/useAdminDashboardData";
import type { AdminDashboardScenario, AdminDashboardTab } from "@/mocks/adminDashboard.mock";
import searchIcon from "@/assets/icons/search.svg";
import plusIcon from "@/assets/icons/plus.svg";
import blockIcon from "@/assets/icons/block.svg";
import unblockIcon from "@/assets/icons/unblock.svg";

// TODO: switch to "empty", "error", or "loading" to validate UI states.
const SCENARIO: AdminDashboardScenario = "normal";
const DEFAULT_TAB: AdminDashboardTab = "journeys";

const parsePositiveInt = (value: string | null, fallback: number) => {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback;
};

const getTabParam = (value: string | null): AdminDashboardTab => {
    switch (value) {
        case "journeys":
        case "users":
        case "events":
        case "universities":
        case "interests":
        case "cities":
        case "careers":
        case "reports":
            return value;
        default:
            return DEFAULT_TAB;
    }
};

export default function AdminPage() {
    const { t } = useI18n();
    const [searchParams, setSearchParams] = useSearchParams();
    const [searchValue, setSearchValue] = useState(searchParams.get("search") ?? "");

    const activeTab = getTabParam(searchParams.get("tab"));
    const searchQuery = searchParams.get("search") ?? "";
    const page = parsePositiveInt(searchParams.get("page"), 1);
    const pageSize = parsePositiveInt(searchParams.get("pageSize"), 10);

    useEffect(() => {
        setSearchValue(searchQuery);
    }, [searchQuery, activeTab]);

    const { data, isLoading, isError } = useAdminDashboardData({
        scenario: SCENARIO,
        search: searchQuery,
        page,
        pageSize,
    });

    const tabs = useMemo(
        () => [
            { key: "journeys" as const, label: t("admin.tab.journeys") },
            { key: "users" as const, label: t("admin.tab.users") },
            { key: "events" as const, label: t("admin.tab.events") },
            { key: "universities" as const, label: t("admin.tab.universities") },
            { key: "interests" as const, label: t("admin.tab.interests") },
            { key: "cities" as const, label: t("admin.tab.cities") },
            { key: "careers" as const, label: t("admin.tab.careers") },
            { key: "reports" as const, label: t("admin.tab.reports") },
        ],
        [t]
    );

    const updateParams = (nextTab: AdminDashboardTab, nextSearch: string, nextPage: number) => {
        setSearchParams({
            tab: nextTab,
            search: nextSearch,
            page: String(nextPage),
            pageSize: String(pageSize),
        });
    };

    const handleTabChange = (tab: AdminDashboardTab) => {
        updateParams(tab, "", 1);
    };

    const handleSearchSubmit = () => {
        updateParams(activeTab, searchValue, 1);
    };

    const handlePageChange = (nextPage: number) => {
        updateParams(activeTab, searchQuery, nextPage);
    };

    // TODO: replace with API-backed role checks when auth is fully wired.
    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    return (
        <div className="admin-dashboard">
            <div className="main-content">
                <div className="content-container">
                    <header className="header">
                        <h1 className="page-title">{t("admin.dashboard.heading")}</h1>
                    </header>

                    <AdminDashboardTabs activeTab={activeTab} tabs={tabs} onTabChange={handleTabChange} />

                    {activeTab === "journeys" && (
                        <JourneysTab
                            data={data.journeys}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                        />
                    )}

                    {activeTab === "users" && (
                        <UsersTab
                            data={data.users}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                            blockIconSrc={blockIcon}
                            unblockIconSrc={unblockIcon}
                        />
                    )}

                    {activeTab === "events" && (
                        <EventsTab
                            data={data.events}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                        />
                    )}

                    {activeTab === "universities" && (
                        <UniversitiesTab
                            data={data.universities}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "interests" && (
                        <InterestsTab
                            data={data.interests}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "cities" && (
                        <CitiesTab
                            data={data.cities}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "careers" && (
                        <CareersTab
                            data={data.careers}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "reports" && (
                        <ReportsTab
                            data={data.reports}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={isLoading}
                            isError={isError}
                            searchIconSrc={searchIcon}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
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
import { useReports } from "@/hooks/useReports";
import type { AdminDashboardTab } from "@/types/admin";
import plusIcon from "@/assets/icons/plus.svg";
import blockIcon from "@/assets/icons/block.svg";
import unblockIcon from "@/assets/icons/unblock.svg";

const DEFAULT_TAB: AdminDashboardTab = "journeys";
const ADMIN_TABS: AdminDashboardTab[] = [
    "journeys",
    "users",
    "events",
    "universities",
    "interests",
    "cities",
    "careers",
    "reports",
];
const ADMIN_TAB_SET = new Set(ADMIN_TABS);

const parsePositiveInt = (value: string | null, fallback: number) => {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback;
};

const getTabParam = (value?: string | null): AdminDashboardTab => {
    if (value && ADMIN_TAB_SET.has(value as AdminDashboardTab)) {
        return value as AdminDashboardTab;
    }
    return DEFAULT_TAB;
};

export default function AdminPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { tab: tabParam } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const [searchValue, setSearchValue] = useState(searchParams.get("search") ?? "");

    const legacyTabParam = searchParams.get("tab");
    const activeTab = getTabParam(tabParam ?? legacyTabParam);
    const searchQuery = searchParams.get("search") ?? "";
    const page = parsePositiveInt(searchParams.get("page"), 1);
    const pageSize = parsePositiveInt(searchParams.get("pageSize"), 10);

    useEffect(() => {
        setSearchValue(searchQuery);
    }, [searchQuery]);

    useEffect(() => {
        if (tabParam === activeTab) {
            return;
        }
        const nextParams = new URLSearchParams(searchParams);
        nextParams.delete("tab");
        const search = nextParams.toString();
        navigate(
            {
                pathname: `/admin/${activeTab}`,
                search: search ? `?${search}` : "",
            },
            { replace: true }
        );
    }, [activeTab, navigate, searchParams, tabParam]);

    const { data, isLoading, isError, refetch } = useAdminDashboardData({
        search: searchQuery,
        page,
        pageSize,
    });
    const reportsQuery  = useReports({ search: searchQuery, page, pageSize });

    const tabs = useMemo(
        () => ADMIN_TABS.map((key) => ({ key, label: t(`admin.tab.${key}`) })),
        [t]
    );

    const buildParams = (nextSearch: string, nextPage: number) => {
        const nextParams = new URLSearchParams(searchParams);
        nextParams.delete("tab");
        nextParams.set("search", nextSearch);
        nextParams.set("page", String(nextPage));
        nextParams.set("pageSize", String(pageSize));
        return nextParams;
    };

    const handleTabChange = (tab: AdminDashboardTab) => {
        const nextParams = buildParams("", 1);
        const search = nextParams.toString();
        navigate({
            pathname: `/admin/${tab}`,
            search: search ? `?${search}` : "",
        });
    };

    const handleSearchSubmit = (value: string) => {
        setSearchParams(buildParams(value, 1));
    };

    const handlePageChange = (nextPage: number) => {
        setSearchParams(buildParams(searchQuery, nextPage));
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
                            blockIconSrc={blockIcon}
                            unblockIconSrc={unblockIcon}
                            onRefresh={refetch}
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
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "reports" && (
                        <ReportsTab
                            data={reportsQuery.data}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            isLoading={reportsQuery.isLoading}
                            isError={reportsQuery.isError}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

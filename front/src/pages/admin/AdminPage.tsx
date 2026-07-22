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
import { useReports } from "@/hooks/useReports";
import {
    useAdminCareers,
    useAdminCities,
    useAdminEvents,
    useAdminInterests,
    useAdminJourneys,
    useAdminUniversities,
    useAdminUsers,
} from "@/hooks/admin/useAdminTabData";
import { EMPTY_ADMIN_USER_FILTERS, type AdminDashboardTab, type AdminUserFilters } from "@/types/admin";
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

const parseOptionalInt = (value: string | null): number | null => {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
};

const parseOptionalBoolean = (value: string | null): boolean | null => {
    if (value === "true") {
        return true;
    }
    if (value === "false") {
        return false;
    }
    return null;
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

    const legacyTabParam = searchParams.get("tab");
    const activeTab = getTabParam(tabParam ?? legacyTabParam);
    const searchQuery = searchParams.get("search") ?? "";
    const page = parsePositiveInt(searchParams.get("page"), 1);
    const pageSize = parsePositiveInt(searchParams.get("pageSize"), 10);
    const userFilters: AdminUserFilters = {
        blocked: parseOptionalBoolean(searchParams.get("blocked")),
        universityId: parseOptionalInt(searchParams.get("university")),
        universityName: searchParams.get("universityName") ?? "",
        careerId: parseOptionalInt(searchParams.get("career")),
        careerName: searchParams.get("careerName") ?? "",
        interestId: parseOptionalInt(searchParams.get("interest")),
        interestName: searchParams.get("interestName") ?? "",
    };

    const [searchValue, setSearchValue] = useState(searchQuery);
    const [lastSearchQuery, setLastSearchQuery] = useState(searchQuery);
    if (lastSearchQuery !== searchQuery) {
        setLastSearchQuery(searchQuery);
        setSearchValue(searchQuery);
    }

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

    const tabs = useMemo(
        () => ADMIN_TABS.map((key) => ({ key, label: t(`admin.tab.${key}`) })),
        [t]
    );

    const buildParams = (nextSearch: string, nextPage: number) => {
        const nextParams = new URLSearchParams(searchParams);
        nextParams.delete("tab");
        if (nextSearch) {
            nextParams.set("search", nextSearch);
        } else {
            nextParams.delete("search");
        }
        nextParams.set("page", String(nextPage));
        nextParams.set("pageSize", String(pageSize));
        return nextParams;
    };

    const buildUserFilterParams = (filters: AdminUserFilters) => {
        const nextParams = buildParams(searchQuery, 1);
        const setParam = (key: string, value: string | null) => {
            if (value) {
                nextParams.set(key, value);
            } else {
                nextParams.delete(key);
            }
        };

        setParam("blocked", filters.blocked === null ? null : String(filters.blocked));
        setParam("university", filters.universityId ? String(filters.universityId) : null);
        setParam("universityName", filters.universityId && filters.universityName ? filters.universityName : null);
        setParam("career", filters.careerId ? String(filters.careerId) : null);
        setParam("careerName", filters.careerId && filters.careerName ? filters.careerName : null);
        setParam("interest", filters.interestId ? String(filters.interestId) : null);
        setParam("interestName", filters.interestId && filters.interestName ? filters.interestName : null);
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

    const handlePageChange = (nextPage: number | string) => {
        if (typeof nextPage === 'string') {
            const pageNumber = Number(new URL(nextPage).searchParams.get('page') ?? '1');
            setSearchParams(buildParams(searchQuery, pageNumber));
        } else
            setSearchParams(buildParams(searchQuery, nextPage));
    };

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
                        <AdminJourneysTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                        />
                    )}

                    {activeTab === "users" && (
                        <AdminUsersTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            blockIconSrc={blockIcon}
                            unblockIconSrc={unblockIcon}
                            filters={userFilters}
                            onApplyFilters={(nextFilters) => setSearchParams(buildUserFilterParams(nextFilters))}
                            onResetFilters={() => setSearchParams(buildUserFilterParams(EMPTY_ADMIN_USER_FILTERS))}
                        />
                    )}

                    {activeTab === "events" && (
                        <AdminEventsTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                        />
                    )}

                    {activeTab === "universities" && (
                        <AdminUniversitiesTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "interests" && (
                        <AdminInterestsTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "cities" && (
                        <AdminCitiesTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "careers" && (
                        <AdminCareersTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                            plusIconSrc={plusIcon}
                        />
                    )}

                    {activeTab === "reports" && (
                        <AdminReportsTabContainer
                            searchQuery={searchQuery}
                            page={page}
                            pageSize={pageSize}
                            searchValue={searchValue}
                            onSearchChange={setSearchValue}
                            onSearchSubmit={handleSearchSubmit}
                            onPageChange={handlePageChange}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

interface AdminTabContainerProps {
    searchQuery: string;
    page: number;
    pageSize: number;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number | string) => void;
}

function AdminJourneysTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
}: AdminTabContainerProps) {
    const { data, isLoading, isError } = useAdminJourneys({ search: searchQuery, page, pageSize });

    return (
        <JourneysTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
        />
    );
}

function AdminUsersTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    blockIconSrc,
    unblockIconSrc,
    filters,
    onApplyFilters,
    onResetFilters,
}: AdminTabContainerProps & {
    blockIconSrc: string;
    unblockIconSrc: string;
    filters: AdminUserFilters;
    onApplyFilters: (filters: AdminUserFilters) => void;
    onResetFilters: () => void;
}) {
    const { data, isLoading, isError, refetch } = useAdminUsers({
        search: searchQuery,
        page,
        pageSize,
        blocked: filters.blocked ?? undefined,
        university: filters.universityId ?? undefined,
        career: filters.careerId ?? undefined,
        interest: filters.interestId ?? undefined,
    });

    return (
        <UsersTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
            blockIconSrc={blockIconSrc}
            unblockIconSrc={unblockIconSrc}
            onRefresh={refetch}
            filters={filters}
            onApplyFilters={onApplyFilters}
            onResetFilters={onResetFilters}
        />
    );
}

function AdminEventsTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
}: AdminTabContainerProps) {
    const { data, isLoading, isError } = useAdminEvents({ search: searchQuery, page, pageSize });

    return (
        <EventsTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
        />
    );
}

function AdminUniversitiesTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    plusIconSrc,
}: AdminTabContainerProps & { plusIconSrc: string }) {
    const { data, isLoading, isError } = useAdminUniversities({ search: searchQuery, page, pageSize });

    return (
        <UniversitiesTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
            plusIconSrc={plusIconSrc}
        />
    );
}

function AdminInterestsTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    plusIconSrc,
}: AdminTabContainerProps & { plusIconSrc: string }) {
    const { data, isLoading, isError } = useAdminInterests({ search: searchQuery, page, pageSize });

    return (
        <InterestsTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
            plusIconSrc={plusIconSrc}
        />
    );
}

function AdminCitiesTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    plusIconSrc,
}: AdminTabContainerProps & { plusIconSrc: string }) {
    const { data, isLoading, isError } = useAdminCities({ search: searchQuery, page, pageSize });

    return (
        <CitiesTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
            plusIconSrc={plusIconSrc}
        />
    );
}

function AdminCareersTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    plusIconSrc,
}: AdminTabContainerProps & { plusIconSrc: string }) {
    const { data, isLoading, isError } = useAdminCareers({ search: searchQuery, page, pageSize });

    return (
        <CareersTab
            data={data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={isLoading}
            isError={isError}
            plusIconSrc={plusIconSrc}
        />
    );
}

function AdminReportsTabContainer({
    searchQuery,
    page,
    pageSize,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
}: AdminTabContainerProps) {
    const reportsQuery = useReports({ search: searchQuery, page, pageSize });

    return (
        <ReportsTab
            data={reportsQuery.data}
            searchValue={searchValue}
            onSearchChange={onSearchChange}
            onSearchSubmit={onSearchSubmit}
            onPageChange={onPageChange}
            isLoading={reportsQuery.isLoading}
            isError={reportsQuery.isError}
        />
    );
}

import { useMemo } from "react";
import {
    type AdminCareer,
    type AdminCity,
    type AdminDashboardMockData,
    type AdminDashboardScenario,
    type AdminEvent,
    type AdminInterest,
    type AdminJourney,
    type AdminReport,
    type AdminUniversity,
    type AdminUser,
    getAdminDashboardMock,
} from "@/mocks/adminDashboard.mock";

export interface PagedResult<T> {
    content: T[];
    totalPages: number;
    currentPage: number;
    pageSize: number;
    totalItems: number;
}

export interface AdminDashboardData {
    journeys: PagedResult<AdminJourney>;
    users: PagedResult<AdminUser>;
    events: PagedResult<AdminEvent>;
    universities: PagedResult<AdminUniversity>;
    interests: PagedResult<AdminInterest>;
    cities: PagedResult<AdminCity>;
    careers: PagedResult<AdminCareer>;
    reports: PagedResult<AdminReport>;
}

interface AdminDashboardDataParams {
    scenario?: AdminDashboardScenario;
    search?: string;
    page?: number;
    pageSize?: number;
    overrides?: Partial<AdminDashboardMockData>;
}

const normalize = (value: string) => value.toLowerCase();
const includesText = (value: string, query: string) => normalize(value).includes(query);

const filterJourneys = (items: AdminJourney[], query: string) =>
    items.filter(
        (journey) =>
            includesText(journey.user.username, query) ||
            includesText(journey.destinationUniversity.name, query) ||
            includesText(journey.destinationUniversity.city, query)
    );

const filterUsers = (items: AdminUser[], query: string) =>
    items.filter(
        (user) =>
            includesText(user.firstname, query) ||
            includesText(user.email, query) ||
            includesText(user.university, query)
    );

const filterEvents = (items: AdminEvent[], query: string) =>
    items.filter(
        (event) =>
            includesText(event.title, query) ||
            includesText(event.user.username, query) ||
            includesText(event.city, query)
    );

const filterUniversities = (items: AdminUniversity[], query: string) =>
    items.filter(
        (university) =>
            includesText(university.name, query) ||
            includesText(university.abbreviation, query) ||
            includesText(university.city, query)
    );

const filterInterests = (items: AdminInterest[], query: string) =>
    items.filter((interest) => includesText(interest.name, query));

const filterCities = (items: AdminCity[], query: string) =>
    items.filter((city) => includesText(city.name, query) || includesText(city.country, query));

const filterCareers = (items: AdminCareer[], query: string) =>
    items.filter((career) => includesText(career.name, query));

const filterReports = (items: AdminReport[], query: string) =>
    items.filter(
        (report) =>
            includesText(report.reportedUser.username, query) ||
            includesText(report.reportingUser.username, query) ||
            includesText(report.description, query) ||
            includesText(report.reason, query) ||
            includesText(report.status, query)
    );

const paginate = <T>(items: T[], page: number, pageSize: number): PagedResult<T> => {
    const safePageSize = Math.max(1, pageSize);
    const totalItems = items.length;
    const totalPages = Math.ceil(totalItems / safePageSize);
    const safePage = Math.min(Math.max(page, 1), Math.max(totalPages, 1));
    const start = (safePage - 1) * safePageSize;
    const content = items.slice(start, start + safePageSize);

    return {
        content,
        totalPages,
        currentPage: totalPages === 0 ? 1 : safePage,
        pageSize: safePageSize,
        totalItems,
    };
};

export const useAdminDashboardData = ({
    scenario = "normal",
    search = "",
    page = 1,
    pageSize = 10,
    overrides,
}: AdminDashboardDataParams = {}) => {
    const query = normalize(search.trim());
    // TODO: GET /api/admin/dashboard?search={search}&page={page}&pageSize={pageSize}
    // TODO: expected response shape: AdminDashboardData
    const USE_MOCKS = true;
    const baseData = useMemo(() => getAdminDashboardMock(scenario), [scenario]);
    const data = useMemo(() => ({ ...baseData, ...overrides }), [baseData, overrides]);

    // TODO: move filtering, sorting, and pagination to the API layer once available.
    const filteredData = useMemo(() => {
        if (!query) {
            return data;
        }

        return {
            ...data,
            journeys: filterJourneys(data.journeys, query),
            users: filterUsers(data.users, query),
            events: filterEvents(data.events, query),
            universities: filterUniversities(data.universities, query),
            interests: filterInterests(data.interests, query),
            cities: filterCities(data.cities, query),
            careers: filterCareers(data.careers, query),
            reports: filterReports(data.reports, query),
        };
    }, [data, query]);

    const paged = useMemo<AdminDashboardData>(() => {
        if (!USE_MOCKS) {
            // TODO: Replace in-memory pagination when backend handles it.
        }
        return {
            journeys: paginate(filteredData.journeys, page, pageSize),
            users: paginate(filteredData.users, page, pageSize),
            events: paginate(filteredData.events, page, pageSize),
            universities: paginate(filteredData.universities, page, pageSize),
            interests: paginate(filteredData.interests, page, pageSize),
            cities: paginate(filteredData.cities, page, pageSize),
            careers: paginate(filteredData.careers, page, pageSize),
            reports: paginate(filteredData.reports, page, pageSize),
        };
    }, [filteredData, page, pageSize]);

    return {
        data: paged,
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

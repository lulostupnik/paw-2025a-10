import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type {
    AdminCareer,
    AdminCity,
    AdminEvent,
    AdminInterest,
    AdminJourney,
    AdminUniversity,
    AdminUser,
} from "@/types/admin";
import { listCareers } from "@/lib/api/careers";
import { listCities } from "@/lib/api/cities";
import { fetchEvents } from "@/lib/api/events";
import { listInterests } from "@/lib/api/interests";
import { getJourneys, resolveJourneySummary } from "@/lib/api/journeys";
import { listUniversities } from "@/lib/api/universities";
import { getCityByUrl, getUserByUrl, getUniversityByUrl } from "@/lib/api/journeys";
import { listUsers } from "@/lib/api/users";

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
    reports: PagedResult<never>;
}

interface AdminDashboardDataParams {
    search?: string;
    page?: number;
    pageSize?: number;
}

const buildPagedResult = <T>(items: T[], page: number, pageSize: number): PagedResult<T> => {
    const safePageSize = Math.max(1, pageSize);
    const totalItems = items.length;
    const totalPages = items.length < safePageSize ? page : page + 1;
    const safePage = Math.min(Math.max(page, 1), Math.max(totalPages, 1));
    const content = items;
    return {
        content,
        totalPages,
        currentPage: safePage,
        pageSize: safePageSize,
        totalItems,
    };
};

export const useAdminDashboardData = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminDashboardDataParams = {}) => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const params = {
        search: search.trim() || undefined,
        page: safePage - 1,
        size: safePageSize,
    };

    const query = useQuery({
        queryKey: ["adminDashboard", params],
        queryFn: async ({ signal }) => {
            const [
                journeys,
                users,
                events,
                universities,
                interests,
                cities,
                careers,
            ] = await Promise.all([
                getJourneys(params, signal),
                listUsers(params, signal),
                fetchEvents(params, signal),
                listUniversities(params, signal),
                listInterests(params, signal),
                listCities(params, signal),
                listCareers(params, signal),
            ]);

            const resolvedJourneys = await Promise.all(
                journeys.map((journey) => resolveJourneySummary(journey, signal))
            );
            const adminJourneys = resolvedJourneys.map(
                (journey) =>
                    ({
                        id: journey.id,
                        user: { username: journey.userName ?? "—" },
                        destinationUniversity: {
                            name: journey.university ?? "—",
                            city: journey.city ?? "—",
                        },
                        startDate: journey.startDate,
                        endDate: journey.endDate,
                    }) satisfies AdminJourney
            );

            const adminUsers = await Promise.all(
                users.map(async (user) => {
                    const university = await getUniversityByUrl(user.universityUrl, signal);
                    return {
                        id: user.id,
                        firstname: user.firstname ?? "",
                        email: user.email ?? "",
                        university: university?.name ?? "",
                        blocked: user.active === false,
                    } satisfies AdminUser;
                })
            );

            const adminEvents = await Promise.all(
                events.map(async (event) => {
                    const [creator, city] = await Promise.all([
                        getUserByUrl(event.creatorUrl, signal),
                        getCityByUrl(event.cityUrl, signal),
                    ]);
                    return {
                        id: event.id,
                        title: event.title,
                        user: { username: creator?.username ?? "—" },
                        city: city?.name ?? "—",
                        date: event.date ?? "",
                        attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : 0,
                        attendeesLimit: event.attendeesLimit ?? null,
                    } satisfies AdminEvent;
                })
            );

            const adminUniversities = await Promise.all(
                universities.map(async (university) => {
                    const city = await getCityByUrl(university.cityUrl, signal);
                    return {
                        id: university.id,
                        name: university.name,
                        abbreviation: university.abbreviation,
                        city: city?.name ?? "",
                    } satisfies AdminUniversity;
                })
            );

            const adminInterests = interests.map(
                (interest) =>
                    ({
                        id: interest.id,
                        name: interest.name,
                    }) satisfies AdminInterest
            );

            const adminCities = cities.map(
                (city) =>
                    ({
                        id: city.id,
                        name: city.name,
                        country: city.country ?? "",
                    }) satisfies AdminCity
            );

            const adminCareers = careers.map(
                (career) =>
                    ({
                        id: career.id,
                        name: career.name,
                    }) satisfies AdminCareer
            );

            return {
                journeys: buildPagedResult(adminJourneys, safePage, safePageSize),
                users: buildPagedResult(adminUsers, safePage, safePageSize),
                events: buildPagedResult(adminEvents, safePage, safePageSize),
                universities: buildPagedResult(adminUniversities, safePage, safePageSize),
                interests: buildPagedResult(adminInterests, safePage, safePageSize),
                cities: buildPagedResult(adminCities, safePage, safePageSize),
                careers: buildPagedResult(adminCareers, safePage, safePageSize),
                reports: buildPagedResult([], safePage, safePageSize),
            } satisfies AdminDashboardData;
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? {
            journeys: buildPagedResult([], safePage, safePageSize),
            users: buildPagedResult([], safePage, safePageSize),
            events: buildPagedResult([], safePage, safePageSize),
            universities: buildPagedResult([], safePage, safePageSize),
            interests: buildPagedResult([], safePage, safePageSize),
            cities: buildPagedResult([], safePage, safePageSize),
            careers: buildPagedResult([], safePage, safePageSize),
            reports: buildPagedResult([], safePage, safePageSize),
        },
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

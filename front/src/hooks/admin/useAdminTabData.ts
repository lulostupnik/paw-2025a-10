import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type {
    AdminCareer,
    AdminCity,
    AdminEvent,
    AdminInterest,
    AdminJourney,
    AdminUniversity,
    AdminUser,
    PagedResult,
} from "@/types/admin";
import { useJourneys } from "@/hooks/useJourneys";
import { fetchEvents } from "@/lib/api/events";
import { getCityByUrl, getUniversityByUrl, getUserByUrl } from "@/lib/api/journeys";
import { listCareers } from "@/lib/api/careers";
import { listCities } from "@/lib/api/cities";
import { listInterests } from "@/lib/api/interests";
import { listUniversities } from "@/lib/api/universities";
import { listUsers } from "@/lib/api/users";
import { buildPagedResult } from "@/lib/admin/pagination";

interface AdminTabParams {
    search?: string;
    page?: number;
    pageSize?: number;
}

interface AdminTabResult<T> {
    data: PagedResult<T>;
    isLoading: boolean;
    isError: boolean;
    refetch: () => void;
}

const buildListParams = (search: string, page: number, pageSize: number) => ({
    search: search.trim() || undefined,
    page: page - 1,
    size: pageSize,
});

const useAdminListParams = ({ search = "", page = 1, pageSize = 10 }: AdminTabParams) => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const params = useMemo(
        () => buildListParams(search, safePage, safePageSize),
        [search, safePage, safePageSize]
    );

    return { params, safePage, safePageSize };
};

export const useAdminJourneys = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminJourney> => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const listParams = useMemo(
        () => buildListParams(search, safePage, safePageSize),
        [search, safePage, safePageSize]
    );

    const journeysQuery = useJourneys(listParams);

    const adminJourneys = useMemo(
        () =>
            journeysQuery.journeys.map(
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
            ),
        [journeysQuery.journeys]
    );

    return {
        data: buildPagedResult(adminJourneys, safePage, safePageSize),
        isLoading: journeysQuery.loading,
        isError: Boolean(journeysQuery.error),
        refetch: journeysQuery.refetch,
    };
};

export const useAdminUsers = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminUser> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminUsers", params],
        queryFn: async ({ signal }) => {
            const users = await listUsers(params, signal);
            return Promise.all(
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
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

export const useAdminEvents = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminEvent> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminEvents", params],
        queryFn: async ({ signal }) => {
            const events = await fetchEvents(params, signal);
            return Promise.all(
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
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

export const useAdminUniversities = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminUniversity> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminUniversities", params],
        queryFn: async ({ signal }) => {
            const universities = await listUniversities(params, signal);
            return Promise.all(
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
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

export const useAdminInterests = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminInterest> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminInterests", params],
        queryFn: async ({ signal }) => {
            const interests = await listInterests(params, signal);
            return interests.map(
                (interest) =>
                    ({
                        id: interest.id,
                        name: interest.name,
                    }) satisfies AdminInterest
            );
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

export const useAdminCities = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminCity> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminCities", params],
        queryFn: async ({ signal }) => {
            const cities = await listCities(params, signal);
            return cities.map(
                (city) =>
                    ({
                        id: city.id,
                        name: city.name,
                        country: city.country ?? "",
                    }) satisfies AdminCity
            );
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

export const useAdminCareers = ({
    search = "",
    page = 1,
    pageSize = 10,
}: AdminTabParams = {}): AdminTabResult<AdminCareer> => {
    const { params, safePage, safePageSize } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminCareers", params],
        queryFn: async ({ signal }) => {
            const careers = await listCareers(params, signal);
            return careers.map(
                (career) =>
                    ({
                        id: career.id,
                        name: career.name,
                    }) satisfies AdminCareer
            );
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: buildPagedResult(query.data ?? [], safePage, safePageSize),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

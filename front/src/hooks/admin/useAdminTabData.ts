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
} from "@/types/admin";
import { useJourneys } from "@/hooks/useJourneys";
import { fetchEvents } from "@/lib/api/events";
import { getCityByUrl, getUniversityByUrl, getUserByUrl } from "@/lib/api/journeys";
import { listCareers } from "@/lib/api/careers";
import { listCities } from "@/lib/api/cities";
import { listInterests } from "@/lib/api/interests";
import { listUniversities } from "@/lib/api/universities";
import { listUsers, type UserApi } from "@/lib/api/users";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";

interface AdminTabParams {
    search?: string;
    page?: number;
    pageSize?: number;
    url?: string;
}

interface AdminTabResult<T> {
    data: PageResult<T>;
    isLoading: boolean;
    isError: boolean;
    refetch: () => void;
}

const buildListParams = (search: string, page: number, pageSize: number, url?: string) => ({
    search: search.trim() || undefined,
    page: page,
    size: pageSize,
    url: url
});

const useAdminListParams = ({ search = "", page = 1, pageSize = 10, url = undefined }: AdminTabParams) => {
    const safePage = Math.max(1, page);
    const safePageSize = Math.max(1, pageSize);
    const params = useMemo(
        () => buildListParams(search, safePage, safePageSize, url),
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
            journeysQuery.journeys.content.map(
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
        data: mapPageList(journeysQuery.journeys, adminJourneys),
        isLoading: journeysQuery.loading,
        isError: Boolean(journeysQuery.error),
        refetch: journeysQuery.refetch,
    };
};

export const useAdminUsers = ({
    search = "",
    page = 1,
    pageSize = 10,
    url = undefined
}: AdminTabParams = {}): AdminTabResult<AdminUser> => {
    const { params } = useAdminListParams({ search, page, pageSize, url });

    const query = useQuery({
        queryKey: ["adminUsers", params],
        queryFn: async ({ signal }) => {
            const users = await listUsers(params, signal);
            const adminUsers = await Promise.all(
                users.content.map(async (user: UserApi): Promise<AdminUser> => {
                    const university = await getUniversityByUrl(user.links?.universityUrl, signal);
                    return {
                        id: user.id,
                        firstname: user.firstname ?? "",
                        email: user.email ?? "",
                        university: university?.name ?? "",
                        blocked: user.active === false,
                    } satisfies AdminUser;
                })
            );
            return mapPageList(users, adminUsers);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
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
    const { params } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminEvents", params],
        queryFn: async ({ signal }) => {
            const events = await fetchEvents(params, signal);
            const adminEvents = await Promise.all(
                events.content.map(async (event) => {
                    const [creator, city] = await Promise.all([
                        getUserByUrl(event.links?.creatorUrl, signal),
                        getCityByUrl(event.links?.cityUrl, signal),
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
            return mapPageList(events, adminEvents);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
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
    const { params } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminUniversities", params],
        queryFn: async ({ signal }) => {
            const universities = await listUniversities(params, signal);
            const adminUniversities = await Promise.all(
                universities.content.map(async (university) => {
                    const city = await getCityByUrl(university.links?.cityUrl, signal);
                    return {
                        id: university.id,
                        name: university.name,
                        abbreviation: university.abbreviation,
                        city: city?.name ?? "",
                    } satisfies AdminUniversity;
                })
            );
            return mapPageList(universities, adminUniversities);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
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
    const { params } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminInterests", params],
        queryFn: async ({ signal }) => {
            const interests = await listInterests(params, signal);
            const adminInterests = interests.content.map(
                (interest) =>
                    ({
                        id: interest.id,
                        name: interest.name,
                    }) satisfies AdminInterest
            );
            return mapPageList(interests, adminInterests);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
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
    const { params } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminCities", params],
        queryFn: async ({ signal }) => {
            const cities = await listCities(params, signal);
            const adminCities = cities.content.map(
                (city) =>
                    ({
                        id: city.id,
                        name: city.name,
                        country: city.country ?? "",
                    }) satisfies AdminCity
            );
            return mapPageList(cities, adminCities);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
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
    const { params } = useAdminListParams({ search, page, pageSize });

    const query = useQuery({
        queryKey: ["adminCareers", params],
        queryFn: async ({ signal }) => {
            const careers = await listCareers(params, signal);
            const adminCareers = careers.content.map(
                (career) =>
                    ({
                        id: career.id,
                        name: career.name,
                    }) satisfies AdminCareer
            );
            return mapPageList(careers, adminCareers);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
        isLoading: query.isLoading,
        isError: query.isError,
        refetch: query.refetch,
    };
};

import type { QueryClient } from "@tanstack/react-query";
import type { JourneyCreator, JourneySummary } from "@/types/journey";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { toPaged, type PageResult } from "@/types/pagination";
import { fetchByUrl } from "@/lib/utils/fetchByUrl";

interface UserApi {
    id: number;
    username: string;
    firstname?: string | null;
    lastname?: string | null;
    links?: {
        selfUrl?: string | null;
        profilePictureUrl?: string | null;
        universityUrl?: string | null;
        careerUrl?: string | null;
    } | null;
}

interface UniversityApi {
    id: number;
    name: string;
    abbreviation?: string | null;
    links?: {
        cityUrl?: string | null;
        selfUrl?: string | null;
    } | null;
}

interface CityApi {
    id: number;
    name: string;
    country: string;
    links?: {
        selfUrl?: string | null;
    } | null;
}

interface InterestApi {
    id: number;
    name: string;
    links?: {
        selfUrl?: string | null;
    } | null;
}

interface CareerApi {
    name: string;
    links?: {
        selfUrl?: string | null;
    } | null;
}

interface JourneyResponseApi {
    id: number;
    message: string;
    dateTime: string;
    links?: {
        authorUrl?: string | null;
        selfUrl?: string | null;
        journeyUrl?: string | null;
    } | null;
}

interface TipApi {
    id: number;
    title: string;
    content: string;
    dateTime: string;
    links?: {
        selfUrl?: string | null;
        journeyUrl?: string | null;
    } | null;
}

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

export interface FetchJourneysParams {
    city?: string;
    university?: string;
    startDate?: string;
    endDate?: string;
    interest?: string;
    upcoming?: boolean;
    past?: boolean;
    ongoing?: boolean;
    destinationCity?: number;
    excludeUser?: number;
    recommendedForUser?: number;
    search?: string;
    sort?: string;
    direction?: string;
    page?: number;
    size?: number;
}

export const getJourneys = async (params: FetchJourneysParams = {}, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneySummary[]>("/journeys", { params, signal, headers: { Accept: ContentTypes.JOURNEY_LIST } });
    return toPaged(response);
};

export const getJourneyById = async (id: string | number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneySummary>(`/journeys/${id}`, { signal, headers: { Accept: ContentTypes.JOURNEY } });
    return response.data;
};

export const createJourney = async (
    payload: {
        destinationUniversityId: number;
        startDate: string;
        endDate: string;
        description: string;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<JourneySummary>("/journeys", payload, { signal, headers: { "Content-Type": ContentTypes.JOURNEY, Accept: ContentTypes.JOURNEY } });
    return response.data;
};

export const updateJourney = async (
    id: string | number,
    payload: {
        destinationUniversityId: number;
        startDate: string;
        endDate: string;
        description: string;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.patch<JourneySummary>(`/journeys/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.JOURNEY, Accept: ContentTypes.JOURNEY } });
    return response.data;
};

export const deleteJourney = async (id: string | number, payload?: { message?: string | null }, signal?: AbortSignal) => {
    const body = { deleted: true, ...(payload?.message ? { deletionMessage: payload.message } : {}) };
    await apiClient.patch(`/journeys/${id}`, body, { signal, headers: { "Content-Type": ContentTypes.JOURNEY, Accept: ContentTypes.JOURNEY } });
};

export const getUserByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient) =>
    fetchByUrl(queryClient, "user-public", url, async (fetchSignal) => {
        const response = await apiClient.get<UserApi>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.USER_PUBLIC } });
        return response.data;
    }, signal);

export const getUniversityByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient) =>
    fetchByUrl(queryClient, "university", url, async (fetchSignal) => {
        const response = await apiClient.get<UniversityApi>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.UNIVERSITY } });
        return response.data;
    }, signal);

export const getCityByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient) =>
    fetchByUrl(queryClient, "city", url, async (fetchSignal) => {
        const response = await apiClient.get<CityApi>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.CITY } });
        return response.data;
    }, signal);

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient) =>
    fetchByUrl(queryClient, "career", url, async (fetchSignal) => {
        const response = await apiClient.get<CareerApi>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.CAREER } });
        return response.data;
    }, signal);

export const getUserInterests = async (userId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<InterestApi[]>(`/users/${userId}/interests`, { signal, headers: { Accept: ContentTypes.USER_INTEREST_LIST } });
    return response.data ?? [];
};

export const getJourneyResponses = async (
    journeyId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<JourneyResponseApi>> => {
    const response = await apiClient.get<JourneyResponseApi[]>(`/journeys/${journeyId}/responses`, {
        params,
        signal,
        headers: { Accept: ContentTypes.JOURNEY_RESPONSE_LIST },
    });
    return toPaged(response);
};

export const getJourneyResponse = async (journeyId: number, responseId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneyResponseApi>(`/journeys/${journeyId}/responses/${responseId}`, { signal, headers: { Accept: ContentTypes.JOURNEY_RESPONSE } });
    return response.data;
};

export const createJourneyResponse = async (journeyId: number, payload: { message: string }, signal?: AbortSignal) => {
    const response = await apiClient.post<JourneyResponseApi>(`/journeys/${journeyId}/responses`, payload, { signal, headers: { "Content-Type": ContentTypes.JOURNEY_RESPONSE, Accept: ContentTypes.JOURNEY_RESPONSE } });
    return response.data;
};

export const deleteJourneyResponse = async (
    journeyId: number,
    responseId: number,
    payload?: { message?: string | null },
    signal?: AbortSignal
) => {
    const body = { deleted: true, deletionMessage: payload?.message ?? null };
    await apiClient.patch(`/journeys/${journeyId}/responses/${responseId}`, body, { signal, headers: { "Content-Type": ContentTypes.JOURNEY_RESPONSE } });
};

export const listJourneyTips = async (journeyId: number, params?: { page?: number; size?: number }, signal?: AbortSignal): Promise<PageResult<TipApi>> => {
    const response = await apiClient.get<TipApi[]>(`/journeys/${journeyId}/tips`, {
        params,
        signal,
        headers: { Accept: ContentTypes.TIP_LIST },
    });
    return toPaged(response);
};

export const getJourneyTip = async (journeyId: number, tipId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<TipApi>(`/journeys/${journeyId}/tips/${tipId}`, { signal, headers: { Accept: ContentTypes.TIP } });
    return response.data;
};

export const createJourneyTip = async (
    journeyId: number,
    payload: { title: string; content: string },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<TipApi>(`/journeys/${journeyId}/tips`, payload, { signal, headers: { "Content-Type": ContentTypes.TIP, Accept: ContentTypes.TIP } });
    return response.data;
};

export const updateJourneyTip = async (
    journeyId: number,
    tipId: number,
    payload: { title: string; content: string },
    signal?: AbortSignal
) => {
    const response = await apiClient.patch<TipApi>(`/journeys/${journeyId}/tips/${tipId}`, payload, { signal, headers: { "Content-Type": ContentTypes.TIP, Accept: ContentTypes.TIP } });
    return response.data;
};

export const deleteJourneyTip = async (journeyId: number, tipId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/journeys/${journeyId}/tips/${tipId}`, { signal });
};

export const resolveJourneySummary = async (journey: JourneySummary, signal?: AbortSignal, queryClient?: QueryClient): Promise<JourneySummary> => {
    const [user, university] = await Promise.all([
        getUserByUrl(journey.links?.userUrl, signal, queryClient),
        getUniversityByUrl(journey.links?.destinationUniversityUrl, signal, queryClient),
    ]);
    const city = university?.links?.cityUrl ? await getCityByUrl(university.links.cityUrl, signal, queryClient) : null;

    return {
        ...journey,
        userName: user?.username ?? journey.userName,
        university: university?.name ?? journey.university,
        city: city?.name ?? journey.city,
        country: city?.country ?? journey.country,
        profilePictureUrl: user?.links?.profilePictureUrl ?? journey.profilePictureUrl ?? null,
    };
};

// Resolves the journey host plus their university/career. Kept separate from the
// core journey fetch so the page can render before these lookups complete.
export const buildJourneyCreator = async (
    journey: JourneySummary,
    signal?: AbortSignal,
    queryClient?: QueryClient
): Promise<JourneyCreator> => {
    const user = journey.links?.userUrl ? await getUserByUrl(journey.links.userUrl, signal, queryClient) : null;
    const [creatorUniversity, creatorCareer] = await Promise.all([
        user?.links?.universityUrl ? getUniversityByUrl(user.links.universityUrl, signal, queryClient) : Promise.resolve(null),
        user?.links?.careerUrl ? getCareerByUrl(user.links.careerUrl, signal, queryClient) : Promise.resolve(null),
    ]);
    return {
        id: user?.id ?? parseIdFromUrl(journey.links?.userUrl) ?? 0,
        firstname: user?.firstname ?? user?.username ?? "—",
        lastname: user?.lastname ?? "",
        username: user?.username ?? "—",
        profilePictureUrl: user?.links?.profilePictureUrl ?? journey.profilePictureUrl ?? null,
        university: creatorUniversity ? { name: creatorUniversity.name } : null,
        career: creatorCareer ? { name: creatorCareer.name } : null,
    };
};

// Resolves the destination university and its city. Separate lookup so its
// section can show its own loading state without blocking the page.
export const buildJourneyDestination = async (
    journey: JourneySummary,
    signal?: AbortSignal,
    queryClient?: QueryClient
): Promise<{ name: string; city: string }> => {
    const university = journey.links?.destinationUniversityUrl
        ? await getUniversityByUrl(journey.links.destinationUniversityUrl, signal, queryClient)
        : null;
    const city = university?.links?.cityUrl ? await getCityByUrl(university.links.cityUrl, signal, queryClient) : null;
    return {
        name: university?.name ?? "—",
        city: city?.name ?? "—",
    };
};

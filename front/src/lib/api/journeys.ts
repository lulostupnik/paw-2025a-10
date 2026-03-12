import type { JourneyComment, JourneyDetail, JourneySummary } from "@/types/journey";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { toPaged, type PageResult } from "@/types/pagination";

interface UserApi {
    id: number;
    username: string;
    firstname?: string;
    lastname?: string;
    links?: {
        selfUrl?: string | null;
        profilePictureUrl?: string | null;
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
    destination?: string;
    startDate?: string;
    endDate?: string;
    interest?: string;
    upcoming?: boolean;
    past?: boolean;
    ongoing?: boolean;
    myDestination?: boolean;
    search?: string;
    sort?: string;
    direction?: string;
    page?: number;
    size?: number;
}

export const getJourneys = async (params: FetchJourneysParams = {}, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneySummary[]>("/journeys", { params, signal });
    return toPaged(response);
};

export const getJourneyById = async (id: string | number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneySummary>(`/journeys/${id}`, { signal });
    return response.data;
};

export const createJourney = async (
    payload: {
        destinationUniversity: string;
        startDate: string;
        endDate: string;
        description: string;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<JourneySummary>("/journeys", payload, { signal });
    return response.data;
};

export const updateJourney = async (
    id: string | number,
    payload: {
        destinationUniversity: string;
        startDate: string;
        endDate: string;
        description: string;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.put<JourneySummary>(`/journeys/${id}`, payload, { signal });
    return response.data;
};

export const deleteJourney = async (id: string | number, payload?: { message?: string | null }, signal?: AbortSignal) => {
    await apiClient.delete(`/journeys/${id}`, { data: payload, signal });
};

export const getUserByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<UserApi>(normalizeApiPath(url), { signal });
    return response.data;
};

export const getUniversityByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<UniversityApi>(normalizeApiPath(url), { signal });
    return response.data;
};

export const getCityByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<CityApi>(normalizeApiPath(url), { signal });
    return response.data;
};

export const getUserInterests = async (userId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<InterestApi[]>(`/users/${userId}/interests`, { signal });
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
    });
    return toPaged(response);
};

export const getJourneyResponse = async (journeyId: number, responseId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneyResponseApi>(`/journeys/${journeyId}/responses/${responseId}`, { signal });
    return response.data;
};

export const createJourneyResponse = async (journeyId: number, payload: { message: string }, signal?: AbortSignal) => {
    const response = await apiClient.post<JourneyResponseApi>(`/journeys/${journeyId}/responses`, payload, { signal });
    return response.data;
};

export const deleteJourneyResponse = async (
    journeyId: number,
    responseId: number,
    payload?: { message?: string | null },
    signal?: AbortSignal
) => {
    await apiClient.delete(`/journeys/${journeyId}/responses/${responseId}`, { data: payload, signal });
};

export const listJourneyTips = async (journeyId: number, params?: { page?: number; size?: number }, signal?: AbortSignal): Promise<PageResult<TipApi>> => {
    const response = await apiClient.get<TipApi[]>(`/journeys/${journeyId}/tips`, {
        params,
        signal,
    });
    return toPaged(response);
};

export const getJourneyTip = async (journeyId: number, tipId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<TipApi>(`/journeys/${journeyId}/tips/${tipId}`, { signal });
    return response.data;
};

export const createJourneyTip = async (
    journeyId: number,
    payload: { title: string; content: string },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<TipApi>(`/journeys/${journeyId}/tips`, payload, { signal });
    return response.data;
};

export const updateJourneyTip = async (
    journeyId: number,
    tipId: number,
    payload: { title: string; content: string },
    signal?: AbortSignal
) => {
    const response = await apiClient.put<TipApi>(`/journeys/${journeyId}/tips/${tipId}`, payload, { signal });
    return response.data;
};

export const deleteJourneyTip = async (journeyId: number, tipId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/journeys/${journeyId}/tips/${tipId}`, { signal });
};

export const resolveJourneySummary = async (journey: JourneySummary, signal?: AbortSignal): Promise<JourneySummary> => {
    const [user, university] = await Promise.all([
        getUserByUrl(journey.links?.userUrl, signal),
        getUniversityByUrl(journey.links?.destinationUniversityUrl, signal),
    ]);
    const city = university?.links?.cityUrl ? await getCityByUrl(university.links.cityUrl, signal) : null;

    return {
        ...journey,
        userName: user?.username ?? journey.userName,
        university: university?.name ?? journey.university,
        city: city?.name ?? journey.city,
        country: city?.country ?? journey.country,
        profilePictureUrl: journey.profilePictureUrl ?? null, // TODO: fetch profile picture id/url from user details endpoint.
    };
};

export const buildJourneyDetail = async (journey: JourneySummary, signal?: AbortSignal): Promise<JourneyDetail> => {
    const [user, university] = await Promise.all([
        getUserByUrl(journey.links?.userUrl, signal),
        getUniversityByUrl(journey.links?.destinationUniversityUrl, signal),
    ]);
    const city = university?.links?.cityUrl ? await getCityByUrl(university.links.cityUrl, signal) : null;

    const userId = user?.id ?? parseIdFromUrl(journey.links?.userUrl);
    const [interests, responses, tips] = await Promise.all([
        userId ? getUserInterests(userId, signal) : Promise.resolve([]),
        getJourneyResponses(journey.id, { page: 1, size: 4 }, signal),
        listJourneyTips(journey.id, { page: 1, size: 4 }, signal),
    ]);

    const responseUsers = await Promise.all(
        responses.content.map((response) => (response.links?.authorUrl ? getUserByUrl(response.links.authorUrl, signal) : Promise.resolve(null)))
    );

    const comments: JourneyComment[] = responses.content.map((response, index) => ({
        id: response.id,
        message: response.message,
        dateTime: response.dateTime,
        user: {
            username: responseUsers[index]?.username ?? "—", // TODO: backend must provide response author data (firstname/lastname if needed).
        },
    }));

    return {
        id: journey.id,
        description: journey.description,
        startDate: journey.startDate,
        endDate: journey.endDate,
        links: journey.links ?? null,
        destinationUniversity: {
            name: university?.name ?? "—", // TODO: backend must provide destination university name.
            city: city?.name ?? "—", // TODO: backend must provide destination city name.
        },
        user: {
            id: user?.id ?? 0,
            firstname: user?.username ?? "—", // TODO: fetch full user details (firstname) endpoint.
            lastname: "", // TODO: fetch full user details (lastname) endpoint.
            username: user?.username ?? "—",
            profilePictureUrl: journey.profilePictureUrl ?? null, // TODO: backend must provide profile picture id/url endpoint.
        },
        interests: interests.map((interest) => interest.name),
        events: [],
        tips: tips.content.map((tip) => ({
            id: tip.id,
            title: tip.title,
            content: tip.content,
            dateTime: tip.dateTime,
        })),
        comments,
    };
};

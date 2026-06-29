import type { JourneyComment, JourneyDetail, JourneySummary } from "@/types/journey";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { toPaged, type PageResult } from "@/types/pagination";

interface UserApi {
    id: number;
    username: string;
    email?: string | null;
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
    const response = await apiClient.put<JourneySummary>(`/journeys/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.JOURNEY, Accept: ContentTypes.JOURNEY } });
    return response.data;
};

export const deleteJourney = async (id: string | number, payload?: { message?: string | null }, signal?: AbortSignal) => {
    await apiClient.patch(`/journeys/${id}`, payload ?? {}, { signal, headers: { "Content-Type": ContentTypes.JOURNEY, Accept: ContentTypes.JOURNEY } });
};

export const getUserByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<UserApi>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.USER_PUBLIC } });
    return response.data;
};

export const getUniversityByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<UniversityApi>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.UNIVERSITY } });
    return response.data;
};

export const getCityByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<CityApi>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.CITY } });
    return response.data;
};

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal) => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<CareerApi>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.CAREER } });
    return response.data;
};

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
    await apiClient.delete(`/journeys/${journeyId}/responses/${responseId}`, { data: payload, signal, headers: { "Content-Type": ContentTypes.JOURNEY_DELETE } });
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
    const response = await apiClient.put<TipApi>(`/journeys/${journeyId}/tips/${tipId}`, payload, { signal, headers: { "Content-Type": ContentTypes.TIP, Accept: ContentTypes.TIP } });
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
        profilePictureUrl: user?.links?.profilePictureUrl ?? journey.profilePictureUrl ?? null,
    };
};

export const buildJourneyDetail = async (journey: JourneySummary, signal?: AbortSignal): Promise<JourneyDetail> => {
    const [user, university] = await Promise.all([
        getUserByUrl(journey.links?.userUrl, signal),
        getUniversityByUrl(journey.links?.destinationUniversityUrl, signal),
    ]);
    const [city, creatorUniversity, creatorCareer] = await Promise.all([
        university?.links?.cityUrl ? getCityByUrl(university.links.cityUrl, signal) : Promise.resolve(null),
        user?.links?.universityUrl ? getUniversityByUrl(user.links.universityUrl, signal) : Promise.resolve(null),
        user?.links?.careerUrl ? getCareerByUrl(user.links.careerUrl, signal) : Promise.resolve(null),
    ]);

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
            username: responseUsers[index]?.username ?? "—",
        },
    }));

    return {
        id: journey.id,
        description: journey.description,
        startDate: journey.startDate,
        endDate: journey.endDate,
        links: journey.links ?? null,
        destinationUniversity: {
            name: university?.name ?? "—",
            city: city?.name ?? "—",
        },
        user: {
            id: user?.id ?? 0,
            firstname: user?.firstname ?? user?.username ?? "—",
            lastname: user?.lastname ?? "",
            username: user?.username ?? "—",
            profilePictureUrl: user?.links?.profilePictureUrl ?? journey.profilePictureUrl ?? null,
            university: creatorUniversity ? { name: creatorUniversity.name } : null,
            career: creatorCareer ? { name: creatorCareer.name } : null,
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

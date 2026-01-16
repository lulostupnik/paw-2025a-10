import type { JourneyComment, JourneyDetail, JourneySummary } from "@/types/journey";
import { apiBaseUrl, apiClient, normalizeApiPath } from "@/lib/api/client";

const API_BASE_URL = apiBaseUrl;

interface UserApi {
    id: number;
    username: string;
    selfUrl?: string | null;
}

interface UniversityApi {
    id: number;
    name: string;
    abbreviation?: string | null;
    cityUrl?: string | null;
    selfUrl?: string | null;
}

interface CityApi {
    id: number;
    name: string;
    country: string;
    selfUrl?: string | null;
}

interface InterestApi {
    id: number;
    name: string;
    selfUrl?: string | null;
}

interface JourneyResponseApi {
    id: number;
    message: string;
    dateTime: string;
    authorUrl?: string | null;
    selfUrl?: string | null;
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
    return response.data ?? [];
};

export const getJourneyById = async (id: string | number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneySummary>(`/journeys/${id}`, { signal });
    return response.data;
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

export const getJourneyResponses = async (journeyId: number, signal?: AbortSignal) => {
    const response = await apiClient.get<JourneyResponseApi[]>(`/journeys/${journeyId}/responses`, {
        params: { page: 0, size: 50 }, // TODO: support paginated responses.
        signal,
    });
    return response.data ?? [];
};

export const resolveJourneySummary = async (journey: JourneySummary, signal?: AbortSignal): Promise<JourneySummary> => {
    const [user, university] = await Promise.all([
        getUserByUrl(journey.userUrl, signal),
        getUniversityByUrl(journey.destinationUniversityUrl, signal),
    ]);
    const city = university?.cityUrl ? await getCityByUrl(university.cityUrl, signal) : null;

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
        getUserByUrl(journey.userUrl, signal),
        getUniversityByUrl(journey.destinationUniversityUrl, signal),
    ]);
    const city = university?.cityUrl ? await getCityByUrl(university.cityUrl, signal) : null;

    const userId = user?.id ?? parseIdFromUrl(journey.userUrl);
    const [interests, responses] = await Promise.all([
        userId ? getUserInterests(userId, signal) : Promise.resolve([]),
        getJourneyResponses(journey.id, signal),
    ]);

    const responseUsers = await Promise.all(
        responses.map((response) => (response.authorUrl ? getUserByUrl(response.authorUrl, signal) : Promise.resolve(null)))
    );

    const comments: JourneyComment[] = responses.map((response, index) => ({
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
        selfUrl: journey.selfUrl ?? null,
        userUrl: journey.userUrl ?? null,
        destinationUniversityUrl: journey.destinationUniversityUrl ?? null,
        tipsUrl: journey.tipsUrl ?? null,
        responsesUrl: journey.responsesUrl ?? null,
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
        events: [], // TODO: fetch journey events endpoint when available.
        comments,
    };
};

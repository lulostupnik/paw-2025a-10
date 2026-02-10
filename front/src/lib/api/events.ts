import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { getCityByUrl, getUserByUrl } from "@/lib/api/journeys";
import type { EventAttendee, EventComment, EventDetail, EventRating, ProfileEvent } from "@/types/event";
import { emptyPage, mapPageList, toPaged, type PageResult } from "@/types/pagination";

interface EventLinks {
    selfUrl?: string | null;
    creatorUrl?: string | null;
    cityUrl?: string | null;
    flyerUrl?: string | null;
    responsesUrl?: string | null;
    attendancesUrl?: string | null;
    ratingsUrl?: string | null;
    statisticsUrl?: string | null;
}


export interface EventDto {
    id: number;
    title: string;
    description?: string | null;
    date?: string | null;
    time?: string | null;
    address?: string | null;
    attendeesLimit?: number | null;
    attendeesCount?: number | null;
    rating?: number | null;
    isFull?: boolean;
    isFuture?: boolean;
    links?: EventLinks | null;
}

export interface EventSummary {
    id: number;
    title: string;
    description?: string;
    date?: string;
    time?: string;
    address?: string;
    attendeesLimit?: number | null;
    attendeesCount?: number | null;
    isFull?: boolean;
    isFuture?: boolean;
    flyerUrl?: string;
    imageUrl?: string;
}

interface EventResponseApi {
    id: number;
    message: string;
    dateTime: string;
    links?: {
        authorUrl?: string | null;
        selfUrl?: string | null;
        eventUrl?: string | null;
    } | null;
}

interface EventAttendanceApi {
    links?: {
        userUrl?: string | null;
        selfUrl?: string | null;
        eventUrl?: string | null;
    } | null;
}

interface RatingApi {
    id: number;
    rating: number;
    links?: {
        userUrl?: string | null;
        selfUrl?: string | null;
        eventUrl?: string | null;
    } | null;
}

interface UserApi {
    id: number;
    username: string;
    email?: string | null;
    firstname: string | null;
    lastname: string | null;
    links?: {
        profilePictureUrl?: string | null;
    } | null;
}

export interface EventStatisticsDto {
    eventsCreatedByOrganizer: number;
    eventsOrganizerAttends: number;
    topCountry: string;
    topCountryCount: number;
    totalParticipants: number;
    maxParticipants: number;
    links?: {
        selfUrl?: string;
        eventUrl?: string;
    } | null;
}

export interface FetchEventsParams {
    destination?: string;
    interest?: string;
    afterDate?: string;
    beforeDate?: string;
    search?: string;
    sort?: "date" | "attendees" | "rating";
    direction?: "asc" | "desc";
    page?: number;
    size?: number;
    attendedBy?: number;
    university?: string;
    minRating?: number;
    hasCapacity?: boolean;
    journeyId?: number;
    creatorId?: number;
}

export async function fetchEvents(params: FetchEventsParams = {}, signal?: AbortSignal): Promise<PageResult<EventDto>> {
    const response = await apiClient.get<EventDto[]>("/events", {
        params,
        signal,
    });
    return toPaged(response);
}

export const getEventById = async (id: number | string, signal?: AbortSignal) => {
    const response = await apiClient.get<EventDto>(`/events/${id}`, { signal });
    return response.data;
};

export const getEventStatistics = async (eventId: number | string, signal?: AbortSignal) => {
    const response = await apiClient.get<EventStatisticsDto>(`/events/${eventId}/statistics`, { signal });
    return response.data;
};

export const listEventResponses = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<EventResponseApi>> => {
    const response = await apiClient.get<EventResponseApi[]>(`/events/${eventId}/responses`, { params, signal });
    return toPaged(response);
};

export const listEventAttendees = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<EventAttendee>> => {
    const response = await apiClient.get<EventAttendanceApi[]>(`/events/${eventId}/attendances`, { params, signal });
    const page = toPaged(response);
    const users = await Promise.all(
        page.content.map((attendance) =>
            attendance.links?.userUrl ? getUserByUrl(attendance.links.userUrl, signal) : Promise.resolve(null)
        )
    );
    const mapped = page.content.map((attendance, index) => {
        const user = users[index];
        return {
            id: user?.id ?? 0,
            firstname: user?.firstname ?? user?.username ?? "—",
            lastname: user?.lastname ?? "",
            email: user?.email ?? "",
            profilePictureUrl: user?.links?.profilePictureUrl ?? null,
        };
    });
    return mapPageList(page, mapped);
};

export const listEventRatings = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<RatingApi>> => {
    const response = await apiClient.get<RatingApi[]>(`/events/${eventId}/ratings`, { params, signal });
    return toPaged(response);
};

export const mapEventDtoToSummary = (dto: EventDto): EventSummary => ({
    id: dto.id,
    title: dto.title,
    description: dto.description ?? undefined,
    date: dto.date ?? undefined,
    time: dto.time ?? undefined,
    address: dto.address ?? undefined,
    attendeesLimit: typeof dto.attendeesLimit === "number" ? dto.attendeesLimit : undefined,
    attendeesCount: typeof dto.attendeesCount === "number" ? dto.attendeesCount : undefined,
    isFull: dto.isFull ?? false,
    isFuture: dto.isFuture ?? false,
    flyerUrl: dto.links?.flyerUrl ?? undefined,
    imageUrl: dto.links?.flyerUrl ?? undefined,
});

export const buildProfileEvent = async (events: EventDto[], signal?: AbortSignal): Promise<ProfileEvent[]> => {
    const results = await Promise.all(
        events.map(async (event) => {
            const [city, user] = await Promise.all([
                getCityByUrl(event.links?.cityUrl, signal),
                getUserByUrl(event.links?.creatorUrl, signal),
            ]);
            return {
                id: event.id,
                title: event.title,
                description: event.description ?? undefined,
                date: event.date ?? "",
                attendeesLimit: typeof event.attendeesLimit === "number" ? event.attendeesLimit : undefined,
                attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : undefined,
                isFull: event.isFull ?? false,
                flyerImageUrl: event.links?.flyerUrl ?? undefined,
                city: { name: city?.name ?? "" },
                user: {
                    id: user?.id ?? 0,
                    username: user?.username ?? "",
                    firstname: user?.firstname ?? "",
                    lastname: user?.lastname ?? "",
                },
            } satisfies ProfileEvent;
        })
    );
    return results;
};

export const buildEventDetail = async (event: EventDto, signal?: AbortSignal): Promise<EventDetail> => {
    const listAttendeesSafe = async () => {
        try {
            return await listEventAttendees(event.id, { page: 1, size: 10 }, signal);
        } catch (error) {
            const status = (error as { response?: { status?: number } } | undefined)?.response?.status;
            if (status === 401 || status === 403) {
                return emptyPage<EventAttendee>();
            }
            throw error;
        }
    };

    const [creator, city, responses, attendees, ratings] = await Promise.all([
        event.links?.creatorUrl ? getUserByUrl(event.links.creatorUrl, signal) : Promise.resolve(null),
        event.links?.cityUrl ? getCityByUrl(event.links.cityUrl, signal) : Promise.resolve(null),
        listEventResponses(event.id, { page: 1, size: 10 }, signal),
        listAttendeesSafe(),
        listEventRatings(event.id, { page: 1, size: 10 }, signal),
    ]);

    const responseUsers = await Promise.all(
        responses.content.map((response) =>
            response.links?.authorUrl ? getUserByUrl(normalizeApiPath(response.links.authorUrl), signal) : Promise.resolve(null)
        )
    );
    const ratingUsers = await Promise.all(
        ratings.content.map((rating) =>
            rating.links?.userUrl ? getUserByUrl(normalizeApiPath(rating.links.userUrl), signal) : Promise.resolve(null)
        )
    );

    const comments: EventComment[] = responses.content.map((response, index) => ({
        id: response.id,
        message: response.message,
        dateTime: response.dateTime,
        user: {
            username: responseUsers[index]?.username ?? "—",
        },
    }));

    const attendeesList: EventAttendee[] = attendees.content;

    const ratingsList: EventRating[] = ratings.content.map((rating, index) => ({
        id: rating.id,
        rating: rating.rating,
        dateTime: new Date().toISOString(),
        user: { username: ratingUsers[index]?.username ?? "—" },
    }));

    const averageRating = typeof event.rating === "number"
        ? event.rating
        : ratingsList.length > 0
          ? ratingsList.reduce((sum, rating) => sum + rating.rating, 0) / ratingsList.length
          : null;

    return {
        id: event.id,
        title: event.title,
        description: event.description ?? "",
        city: { name: city?.name ?? "—" },
        date: event.date ?? "",
        time: event.time ?? null,
        address: event.address ?? null,
        flyerImageUrl: event.links?.flyerUrl ?? null,
        attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : attendeesList.length,
        attendeesLimit: event.attendeesLimit ?? null,
        isFuture: event.isFuture ?? false,
        user: {
            id: creator?.id ?? 0,
            firstname: creator?.username ?? "—",
            lastname: "",
            username: creator?.username ?? "—",
            profilePictureUrl: creator?.links?.profilePictureUrl ?? null,
            university: null,
            career: null,
        },
        comments,
        attendees: attendeesList,
        ratings: ratingsList,
        averageRating,
    };
};

export const updateEvent = async (
    id: number,
    payload: {
        city: string;
        date: string;
        description: string;
        title: string;
        time?: string | null;
        address?: string | null;
        attendeesLimit?: number | null;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.put<EventDto>(`/events/${id}`, payload, { signal });
    return response.data;
};

export const createEvent = async (
    payload: {
        city: string;
        date: string;
        description: string;
        title: string;
        time?: string | null;
        address?: string | null;
        attendeesLimit?: number | null;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<EventDto>("/events", payload, { signal });
    return response.data;
};

export const deleteEvent = async (id: number, payload?: { message?: string | null }, signal?: AbortSignal) => {
    await apiClient.delete(`/events/${id}`, { data: payload, signal });
};

export const createEventResponse = async (eventId: number, payload: { message: string }, signal?: AbortSignal) => {
    const response = await apiClient.post<EventResponseApi>(`/events/${eventId}/responses`, payload, { signal });
    return response.data;
};

export const deleteEventResponse = async (
    eventId: number,
    responseId: number,
    payload?: { message?: string | null },
    signal?: AbortSignal
) => {
    await apiClient.delete(`/events/${eventId}/responses/${responseId}`, { data: payload, signal });
};

export const attendEvent = async (eventId: number, signal?: AbortSignal) => {
    const response = await apiClient.post(`/events/${eventId}/attendances`, undefined, { signal });
    return response.data;
};

export const getEventAttendance = async (eventId: number, userId: number, signal?: AbortSignal) => {
    const response = await apiClient.get(`/events/${eventId}/attendances/${userId}`, { signal });
    return response.data;
};

export const unattendEvent = async (eventId: number, userId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/events/${eventId}/attendances/${userId}`, { signal });
};

export const updateEventFlyer = async (eventId: number, flyer: File, signal?: AbortSignal) => {
    const formData = new FormData();
    formData.append("flyer", flyer);
    const response = await apiClient.put(`/events/${eventId}/flyer`, formData, {
        signal,
        headers: { Accept: "image/jpeg, image/png, image/webp, */*" },
        responseType: "arraybuffer",
    });
    return response.data;
};

export const createEventRating = async (eventId: number, payload: { rating: number }, signal?: AbortSignal) => {
    const response = await apiClient.post(`/events/${eventId}/ratings`, payload, { signal });
    return response.data;
};

export const updateEventRating = async (
    eventId: number,
    ratingId: number,
    payload: { rating: number },
    signal?: AbortSignal
) => {
    const response = await apiClient.put(`/events/${eventId}/ratings/${ratingId}`, payload, { signal });
    return response.data;
};

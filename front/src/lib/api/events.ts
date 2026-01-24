import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { getCityByUrl, getUserByUrl } from "@/lib/api/journeys";
import type { EventAttendee, EventComment, EventDetail, EventRating, ProfileEvent } from "@/types/event";


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
    selfUrl?: string;
    creatorUrl?: string;
    cityUrl?: string;
    flyerUrl?: string;
    responsesUrl?: string;
    attendancesUrl?: string;
    ratingsUrl?: string;
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
    authorUrl?: string | null;
    selfUrl?: string | null;
    eventUrl?: string | null;
}

interface RatingApi {
    id: number;
    rating: number;
    userUrl?: string | null;
    selfUrl?: string | null;
    eventUrl?: string | null;
}

interface UserApi {
    id: number;
    username: string;
    email?: string | null;
    profilePictureUrl?: string | null;
    firstname: string | null;
    lastname: string | null;
}

export interface FetchEventsParams {
    destination?: string;
    interest?: string;
    startDate?: string;
    endDate?: string;
    upcoming?: boolean;
    past?: boolean;
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

export async function fetchEvents(params: FetchEventsParams = {}, signal?: AbortSignal): Promise<EventDto[]> {
    const response = await apiClient.get<EventDto[]>("/events", {
        params,
        signal,
    });
    const data = response.data;
    return Array.isArray(data) ? data : [];
}

export const getEventById = async (id: number | string, signal?: AbortSignal) => {
    const response = await apiClient.get<EventDto>(`/events/${id}`, { signal });
    return response.data;
};

export const listEventResponses = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
) => {
    const response = await apiClient.get<EventResponseApi[]>(`/events/${eventId}/responses`, { params, signal });
    return response.data ?? [];
};

export const listEventAttendees = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
) => {
    const response = await apiClient.get<UserApi[]>(`/events/${eventId}/attendances`, { params, signal });
    return response.data ?? [];
};

export const listEventRatings = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
) => {
    const response = await apiClient.get<RatingApi[]>(`/events/${eventId}/ratings`, { params, signal });
    return response.data ?? [];
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
    flyerUrl: dto.flyerUrl ?? undefined,
    imageUrl: dto.flyerUrl ?? undefined,
});

export const buildProfileEvent = async (events: EventDto[], signal?: AbortSignal): Promise<ProfileEvent[]> => {
    const results = await Promise.all(
        events.map(async (event) => {
            const [city, user] = await Promise.all([
                getCityByUrl(event.cityUrl, signal),
                getUserByUrl(event.creatorUrl, signal),
            ]);
            return {
                id: event.id,
                title: event.title,
                description: event.description ?? undefined,
                date: event.date ?? "",
                attendeesLimit: typeof event.attendeesLimit === "number" ? event.attendeesLimit : undefined,
                attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : undefined,
                isFull: event.isFull ?? false,
                flyerImageUrl: event.flyerUrl ?? undefined,
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
    const [creator, city, responses, attendees, ratings] = await Promise.all([
        event.creatorUrl ? getUserByUrl(event.creatorUrl, signal) : Promise.resolve(null),
        event.cityUrl ? getCityByUrl(event.cityUrl, signal) : Promise.resolve(null),
        listEventResponses(event.id, { page: 0, size: 10 }, signal),
        listEventAttendees(event.id, { page: 0, size: 10 }, signal),
        listEventRatings(event.id, { page: 0, size: 10 }, signal),
    ]);

    const responseUsers = await Promise.all(
        responses.map((response) =>
            response.authorUrl ? getUserByUrl(normalizeApiPath(response.authorUrl), signal) : Promise.resolve(null)
        )
    );
    const ratingUsers = await Promise.all(
        ratings.map((rating) =>
            rating.userUrl ? getUserByUrl(normalizeApiPath(rating.userUrl), signal) : Promise.resolve(null)
        )
    );

    const comments: EventComment[] = responses.map((response, index) => ({
        id: response.id,
        message: response.message,
        dateTime: response.dateTime,
        user: {
            username: responseUsers[index]?.username ?? "—",
        },
    }));

    const attendeesList: EventAttendee[] = attendees.map((attendee) => ({
        id: attendee.id,
        firstname: attendee.username ?? "—",
        lastname: "",
        email: attendee.email ?? "",
        profilePictureUrl: attendee.profilePictureUrl ?? null,
    }));

    const ratingsList: EventRating[] = ratings.map((rating, index) => ({
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
        flyerImageUrl: event.flyerUrl ?? null,
        attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : attendeesList.length,
        attendeesLimit: event.attendeesLimit ?? null,
        isFuture: event.isFuture ?? false,
        user: {
            id: creator?.id ?? 0,
            firstname: creator?.username ?? "—",
            lastname: "",
            username: creator?.username ?? "—",
            profilePictureUrl: creator?.profilePictureUrl ?? null,
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

export const unattendEvent = async (eventId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/events/${eventId}/attendances`, { signal });
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

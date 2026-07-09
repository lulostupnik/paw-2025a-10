import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { getCareerByUrl, getCityByUrl, getUniversityByUrl, getUserByUrl } from "@/lib/api/journeys";
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
    recommendedForUser?: number;
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
        headers: { Accept: ContentTypes.EVENT_LIST },
    });
    return toPaged(response);
}

export const getEventById = async (id: number | string, signal?: AbortSignal) => {
    const response = await apiClient.get<EventDto>(`/events/${id}`, { signal, headers: { Accept: ContentTypes.EVENT } });
    return response.data;
};

export const getEventStatistics = async (eventId: number | string, signal?: AbortSignal) => {
    const response = await apiClient.get<EventStatisticsDto>(`/events/${eventId}/statistics`, { signal, headers: { Accept: ContentTypes.EVENT_STATISTICS } });
    return response.data;
};

export const listEventResponses = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<EventResponseApi>> => {
    const response = await apiClient.get<EventResponseApi[]>(`/events/${eventId}/responses`, { params, signal, headers: { Accept: ContentTypes.EVENT_RESPONSE_LIST } });
    return toPaged(response);
};

export const listEventAttendees = async (
    eventId: number,
    params: { page?: number; size?: number } = {},
    signal?: AbortSignal
): Promise<PageResult<EventAttendee>> => {
    const response = await apiClient.get<EventAttendanceApi[]>(`/events/${eventId}/attendances`, { params, signal, headers: { Accept: ContentTypes.EVENT_ATTENDANCE_LIST } });
    const page = toPaged(response);
    const users = await Promise.all(
        page.content.map((attendance) =>
            attendance.links?.userUrl ? getUserByUrl(attendance.links.userUrl, signal) : Promise.resolve(null)
        )
    );
    const mapped = page.content.map((_, index) => {
        const user = users[index];
        return {
            id: user?.id ?? 0,
            firstname: user?.firstname ?? user?.username ?? "—",
            lastname: user?.lastname ?? "",
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
    const response = await apiClient.get<RatingApi[]>(`/events/${eventId}/ratings`, { params, signal, headers: { Accept: ContentTypes.EVENT_RATING_LIST } });
    return toPaged(response);
};

const getEventDateTime = (date?: string | null, time?: string | null): Date | null => {
    if (!date) {
        return null;
    }
    const eventDateTime = new Date(`${date}T${time || "00:00:00"}`);
    return Number.isNaN(eventDateTime.getTime()) ? null : eventDateTime;
};

export const calculateEventIsFuture = (date?: string | null, time?: string | null): boolean => {
    const eventDateTime = getEventDateTime(date, time);
    return eventDateTime !== null && eventDateTime.getTime() > Date.now();
};

const calculateEventIsFull = (attendeesCount?: number | null, attendeesLimit?: number | null): boolean =>
    typeof attendeesCount === "number" &&
    typeof attendeesLimit === "number" &&
    attendeesLimit > 0 &&
    attendeesCount >= attendeesLimit;

export const mapEventDtoToSummary = (dto: EventDto): EventSummary => ({
    id: dto.id,
    title: dto.title,
    description: dto.description ?? undefined,
    date: dto.date ?? undefined,
    time: dto.time ?? undefined,
    address: dto.address ?? undefined,
    attendeesLimit: typeof dto.attendeesLimit === "number" ? dto.attendeesLimit : undefined,
    attendeesCount: typeof dto.attendeesCount === "number" ? dto.attendeesCount : undefined,
    isFull: calculateEventIsFull(dto.attendeesCount, dto.attendeesLimit),
    isFuture: calculateEventIsFuture(dto.date, dto.time),
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
                isFull: calculateEventIsFull(event.attendeesCount, event.attendeesLimit),
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
    const [creatorUniversity, creatorCareer] = await Promise.all([
        creator?.links?.universityUrl ? getUniversityByUrl(creator.links.universityUrl, signal) : Promise.resolve(null),
        creator?.links?.careerUrl ? getCareerByUrl(creator.links.careerUrl, signal) : Promise.resolve(null),
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
        city: { id: city?.id, name: city?.name ?? "—" },
        date: event.date ?? "",
        time: event.time ?? null,
        address: event.address ?? null,
        flyerImageUrl: event.links?.flyerUrl ?? null,
        attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : attendeesList.length,
        attendeesLimit: event.attendeesLimit ?? null,
        isFuture: calculateEventIsFuture(event.date, event.time),
        user: {
            id: creator?.id ?? 0,
            firstname: creator?.firstname ?? creator?.username ?? "—",
            lastname: creator?.lastname ?? "",
            username: creator?.username ?? "—",
            profilePictureUrl: creator?.links?.profilePictureUrl ?? null,
            university: creatorUniversity ? { name: creatorUniversity.name } : null,
            career: creatorCareer ? { name: creatorCareer.name } : null,
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
        cityId: number;
        date: string;
        description: string;
        title: string;
        time?: string | null;
        address?: string | null;
        attendeesLimit?: number | null;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.put<EventDto>(`/events/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.EVENT, Accept: ContentTypes.EVENT } });
    return response.data;
};

export const createEvent = async (
    payload: {
        cityId: number;
        date: string;
        description: string;
        title: string;
        time?: string | null;
        address?: string | null;
        attendeesLimit?: number | null;
    },
    signal?: AbortSignal
) => {
    const response = await apiClient.post<EventDto>("/events", payload, { signal, headers: { "Content-Type": ContentTypes.EVENT, Accept: ContentTypes.EVENT } });
    return response.data;
};

export const deleteEvent = async (id: number, payload?: { message?: string | null }, signal?: AbortSignal) => {
    const body = { deleted: true, ...(payload?.message ? { deletionMessage: payload.message } : {}) };
    await apiClient.patch(`/events/${id}`, body, { signal, headers: { "Content-Type": ContentTypes.EVENT, Accept: ContentTypes.EVENT } });
};

export const createEventResponse = async (eventId: number, payload: { message: string }, signal?: AbortSignal) => {
    const response = await apiClient.post<EventResponseApi>(`/events/${eventId}/responses`, payload, { signal, headers: { "Content-Type": ContentTypes.EVENT_RESPONSE, Accept: ContentTypes.EVENT_RESPONSE } });
    return response.data;
};

export const deleteEventResponse = async (
    eventId: number,
    responseId: number,
    payload?: { message?: string | null },
    signal?: AbortSignal
) => {
    const body = { deleted: true, deletionMessage: payload?.message ?? null };
    await apiClient.patch(`/events/${eventId}/responses/${responseId}`, body, { signal, headers: { "Content-Type": ContentTypes.EVENT_RESPONSE } });
};

export const attendEvent = async (eventId: number, signal?: AbortSignal) => {
    const response = await apiClient.post(`/events/${eventId}/attendances`, undefined, { signal, headers: { Accept: ContentTypes.EVENT_ATTENDANCE } });
    return response.data;
};

export const getEventAttendance = async (eventId: number, userId: number, signal?: AbortSignal) => {
    const response = await apiClient.get(`/events/${eventId}/attendances/${userId}`, { signal, headers: { Accept: ContentTypes.EVENT_ATTENDANCE } });
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
    const response = await apiClient.post(`/events/${eventId}/ratings`, payload, { signal, headers: { "Content-Type": ContentTypes.EVENT_RATING, Accept: ContentTypes.EVENT_RATING } });
    return response.data;
};

export const updateEventRating = async (
    eventId: number,
    ratingId: number,
    payload: { rating: number },
    signal?: AbortSignal
) => {
    const response = await apiClient.put(`/events/${eventId}/ratings/${ratingId}`, payload, { signal, headers: { "Content-Type": ContentTypes.EVENT_RATING, Accept: ContentTypes.EVENT_RATING } });
    return response.data;
};

export const deleteEventRating = async (eventId: number, ratingId: number, signal?: AbortSignal) => {
    await apiClient.delete(`/events/${eventId}/ratings/${ratingId}`, { signal });
};

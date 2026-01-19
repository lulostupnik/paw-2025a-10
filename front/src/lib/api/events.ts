import { apiBaseUrl, apiClient } from "@/lib/api/client";

const EVENTS_ENDPOINT = `${apiBaseUrl}/events`;

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

export interface FetchEventsParams {
    page?: number;
    size?: number;
    upcoming?: boolean;
    search?: string;
}

export async function fetchEvents(params: FetchEventsParams = {}, signal?: AbortSignal): Promise<EventDto[]> {
    const query = new URLSearchParams();

    if (typeof params.page === "number") {
        query.set("page", String(params.page));
    }

    if (typeof params.size === "number") {
        query.set("size", String(params.size));
    }

    if (typeof params.search === "string" && params.search.trim().length > 0) {
        query.set("search", params.search.trim());
    }

    if (typeof params.upcoming === "boolean") {
        query.set("upcoming", String(params.upcoming));
    }

    const queryString = query.toString();
    const response = await fetch(`${EVENTS_ENDPOINT}${queryString ? `?${queryString}` : ""}`, {
        signal,
        headers: { Accept: "application/json" },
    });

    if (!response.ok) {
        throw new Error("Failed to fetch events");
    }

    const data = await response.json();
    return Array.isArray(data) ? data : [];
}

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
        headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
};

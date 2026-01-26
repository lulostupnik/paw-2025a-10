import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { getEventById, type EventDto } from "@/lib/api/events";
import { getJourneyById } from "@/lib/api/journeys";
import type { JourneySummary } from "@/types/journey";
import { toPaged, type PageResult } from "@/types/pagination";

export type ReportType = "JOURNEY" | "EVENT" | "JOURNEY_RESPONSE" | "EVENT_RESPONSE";
export type ReportReason =
    | "SPAM"
    | "INAPPROPRIATE_CONTENT"
    | "HARASSMENT"
    | "MISINFORMATION"
    | "HATE_SPEECH"
    | "VIOLENCE"
    | "OTHER";

export type ReportStatus = "PENDING" | "UNDER_REVIEW" | "RESOLVED" | "DISMISSED" | string;

export interface CreateReportPayload {
    reportType: ReportType;
    targetId: number;
    description: string;
    reason: ReportReason;
}

export const createReport = async (payload: CreateReportPayload, signal?: AbortSignal) => {
    const response = await apiClient.post("/reports", payload, { signal });
    return response.data;
};

export interface ReportDto {
    id: number;
    description?: string | null;
    reason: ReportReason | string;
    status: ReportStatus;
    createdAt?: string | null;
    updatedAt?: string | null;
    selfUrl?: string | null;
    reportedUserUrl?: string | null;
    reportingUserUrl?: string | null;
    journeyUrl?: string | null;
    eventUrl?: string | null;
    journeyResponseUrl?: string | null;
    eventResponseUrl?: string | null;
}

interface UserDto {
    id: number;
    username: string;
    email?: string | null;
    isActive?: boolean | null;
    active?: boolean | null;
    selfUrl?: string | null;
    firstname: string;
    lastname: string;
}

interface JourneyResponseDto {
    id: number;
    message: string;
    dateTime?: string | null;
    journeyUrl?: string | null;
}

interface EventResponseDto {
    id: number;
    message: string;
    dateTime?: string | null;
    eventUrl?: string | null;
}

export interface ReportUser {
    id: number;
    username: string;
    firstname: string;
    lastname: string;
    email?: string | null;
    blocked: boolean;
}

export interface ReportListItem {
    id: number;
    description?: string | null;
    reason: string;
    status: ReportStatus;
    reportedUser: ReportUser;
    reportingUser: ReportUser;
    createdAt?: string | null;
    contentType: "journey" | "event" | "journeyResponse" | "eventResponse" | "unknown";
}

export interface ReportDetail {
    id: number;
    description?: string | null;
    reason: string;
    status: ReportStatus;
    reportedUser: ReportUser;
    reportingUser: ReportUser;
    journey?: {
        id: number;
        description: string;
        startDate?: string | null;
        endDate?: string | null;
        deleted: boolean;
    } | null;
    event?: {
        id: number;
        title: string;
        description: string;
        date?: string | null;
        address?: string | null;
        deleted: boolean;
    } | null;
    journeyResponse?: {
        id: number;
        message: string;
        dateTime?: string | null;
        deleted: boolean;
        journey: {
            id: number;
            user: { username: string };
        };
    } | null;
    eventResponse?: {
        id: number;
        message: string;
        dateTime?: string | null;
        deleted: boolean;
        event: {
            id: number;
            title: string;
        };
    } | null;
}

export interface ListReportsParams {
    search?: string;
    page?: number;
    size?: number;
}

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

const fetchByUrl = async <T>(url?: string | null, signal?: AbortSignal): Promise<T | null> => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<T>(normalizeApiPath(url), { signal });
    return response.data ?? null;
};

const mapUser = (user: UserDto | null): ReportUser => ({
    id: user?.id ?? 0,
    username: user?.username ?? "—",
    email: user?.email ?? null,
    blocked: user?.isActive === false || user?.active === false,
    firstname: user?.firstname ?? "—",
    lastname: user?.lastname ?? "—"
});

const resolveContentType = (report: ReportDto): ReportListItem["contentType"] => {
    if (report.journeyUrl) {
        return "journey";
    }
    if (report.eventUrl) {
        return "event";
    }
    if (report.journeyResponseUrl) {
        return "journeyResponse";
    }
    if (report.eventResponseUrl) {
        return "eventResponse";
    }
    return "unknown";
};

export const listReports = async (params: ListReportsParams = {}, signal?: AbortSignal): Promise<PageResult<ReportDto>> => {
    const response = await apiClient.get<ReportDto[]>("/reports", { params, signal });
    return toPaged(response);
};

export const getReportById = async (id: number | string, signal?: AbortSignal): Promise<ReportDto> => {
    const response = await apiClient.get<ReportDto>(`/reports/${id}`, { signal });
    return response.data;
};

export const updateReportStatus = async (id: number, status: ReportStatus, signal?: AbortSignal) => {
    const response = await apiClient.put<ReportDto>(`/reports/${id}`, { status }, { signal });
    return response.data;
};

export const deleteReport = async (id: number, signal?: AbortSignal) => {
    await apiClient.delete(`/reports/${id}/`, { signal });
};

export const resolveReportListItem = async (report: ReportDto, signal?: AbortSignal): Promise<ReportListItem> => {
    const [reportedUser, reportingUser] = await Promise.all([
        fetchByUrl<UserDto>(report.reportedUserUrl, signal),
        fetchByUrl<UserDto>(report.reportingUserUrl, signal),
    ]);

    return {
        id: report.id,
        description: report.description ?? null,
        reason: report.reason,
        status: report.status,
        reportedUser: mapUser(reportedUser),
        reportingUser: mapUser(reportingUser),
        createdAt: report.createdAt ?? null,
        contentType: resolveContentType(report),
    };
};

export const getReportDetail = async (id: number, signal?: AbortSignal): Promise<ReportDetail> => {
    const report = await getReportById(id, signal);
    const [reportedUser, reportingUser] = await Promise.all([
        fetchByUrl<UserDto>(report.reportedUserUrl, signal),
        fetchByUrl<UserDto>(report.reportingUserUrl, signal),
    ]);

    const journeyId = parseIdFromUrl(report.journeyUrl);
    const eventId = parseIdFromUrl(report.eventUrl);

    const [journeyData, eventData, journeyResponseData, eventResponseData] = await Promise.all([
        journeyId ? getJourneyById(journeyId, signal) : report.journeyUrl ? fetchByUrl<JourneySummary>(report.journeyUrl, signal) : null,
        eventId ? getEventById(eventId, signal) : report.eventUrl ? fetchByUrl<EventDto>(report.eventUrl, signal) : null,
        fetchByUrl<JourneyResponseDto>(report.journeyResponseUrl, signal),
        fetchByUrl<EventResponseDto>(report.eventResponseUrl, signal),
    ]);

    const journeyResponseJourney = journeyResponseData?.journeyUrl
        ? await fetchByUrl<JourneySummary>(journeyResponseData.journeyUrl, signal)
        : null;
    const journeyResponseOwner = journeyResponseJourney?.userUrl
        ? await fetchByUrl<UserDto>(journeyResponseJourney.userUrl, signal)
        : null;

    const eventResponseEvent = eventResponseData?.eventUrl
        ? await fetchByUrl<EventDto>(eventResponseData.eventUrl, signal)
        : null;

    return {
        id: report.id,
        description: report.description ?? null,
        reason: report.reason,
        status: report.status,
        reportedUser: mapUser(reportedUser),
        reportingUser: mapUser(reportingUser),
        journey: journeyData
            ? {
                  id: journeyData.id,
                  description: journeyData.description,
                  startDate: journeyData.startDate ?? null,
                  endDate: journeyData.endDate ?? null,
                  deleted: false,
              }
            : null,
        event: eventData
            ? {
                  id: eventData.id,
                  title: eventData.title,
                  description: eventData.description ?? "",
                  date: eventData.date ?? null,
                  address: eventData.address ?? null,
                  deleted: false,
              }
            : null,
        journeyResponse: journeyResponseData
            ? {
                  id: journeyResponseData.id,
                  message: journeyResponseData.message,
                  dateTime: journeyResponseData.dateTime ?? null,
                  deleted: false,
                  journey: {
                      id: parseIdFromUrl(journeyResponseData.journeyUrl) ?? 0,
                      user: { username: journeyResponseOwner?.username ?? "—" },
                  },
              }
            : null,
        eventResponse: eventResponseData
            ? {
                  id: eventResponseData.id,
                  message: eventResponseData.message,
                  dateTime: eventResponseData.dateTime ?? null,
                  deleted: false,
                  event: {
                      id: parseIdFromUrl(eventResponseData.eventUrl) ?? 0,
                      title: eventResponseEvent?.title ?? "—",
                  },
              }
            : null,
    };
};

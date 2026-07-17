import { apiClient, apiErrorStatus, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
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
    const response = await apiClient.post("/reports", payload, { signal, headers: { "Content-Type": ContentTypes.REPORT, Accept: ContentTypes.REPORT } });
    return response.data;
};

export interface ReportDto {
    id: number;
    description?: string | null;
    reason: ReportReason | string;
    status: ReportStatus;
    createdAt?: string | null;
    updatedAt?: string | null;
    links?: {
        selfUrl?: string | null;
        reportedUserUrl?: string | null;
        reportingUserUrl?: string | null;
        targetUrl?: string | null;
    } | null;
}

interface UserDto {
    id: number;
    username: string;
    firstname: string;
    lastname: string;
    links?: {
        selfUrl?: string | null;
    } | null;
}

interface JourneyResponseDto {
    id: number;
    message: string;
    dateTime?: string | null;
    links?: {
        journeyUrl?: string | null;
        selfUrl?: string | null;
    } | null;
}

interface EventResponseDto {
    id: number;
    message: string;
    dateTime?: string | null;
    links?: {
        eventUrl?: string | null;
        selfUrl?: string | null;
    } | null;
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
    contentType: ReportListItem["contentType"];
    contentDeleted: boolean;
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

type ReportTargetKind = "journey" | "event" | "journeyResponse" | "eventResponse" | "unknown";

interface ReportTarget {
    kind: ReportTargetKind;
    url: string;
    id: number | null;
}

const parseReportTarget = (url?: string | null): ReportTarget | null => {
    if (!url) {
        return null;
    }
    const normalized = normalizeApiPath(url);
    if (/\/journeys\/\d+\/responses\/\d+\/?$/.test(normalized)) {
        return { kind: "journeyResponse", url, id: parseIdFromUrl(url) };
    }
    if (/\/events\/\d+\/responses\/\d+\/?$/.test(normalized)) {
        return { kind: "eventResponse", url, id: parseIdFromUrl(url) };
    }
    if (/\/journeys\/\d+\/?$/.test(normalized)) {
        return { kind: "journey", url, id: parseIdFromUrl(url) };
    }
    if (/\/events\/\d+\/?$/.test(normalized)) {
        return { kind: "event", url, id: parseIdFromUrl(url) };
    }
    return { kind: "unknown", url, id: parseIdFromUrl(url) };
};

const fetchMissingAsNull = async <T>(loader: () => Promise<T | null>): Promise<T | null> => {
    try {
        return await loader();
    } catch (error) {
        if (apiErrorStatus(error) === 404) {
            return null;
        }
        throw error;
    }
};

const fetchByUrl = async <T>(url: string | null | undefined, accept: string, signal?: AbortSignal): Promise<T | null> => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<T>(normalizeApiPath(url), { signal, headers: { Accept: accept } });
    return response.data ?? null;
};

const mapUser = (user: UserDto | null): ReportUser => ({
    id: user?.id ?? 0,
    username: user?.username ?? "—",
    email: null,
    blocked: false,
    firstname: user?.firstname ?? "—",
    lastname: user?.lastname ?? "—"
});

const resolveContentType = (report: ReportDto): ReportListItem["contentType"] => {
    return parseReportTarget(report.links?.targetUrl)?.kind ?? "unknown";
};

export const listReports = async (params: ListReportsParams = {}, signal?: AbortSignal): Promise<PageResult<ReportDto>> => {
    const response = await apiClient.get<ReportDto[]>("/reports", { params, signal, headers: { Accept: ContentTypes.REPORT_LIST } });
    return toPaged(response);
};

export const getReportById = async (id: number | string, signal?: AbortSignal): Promise<ReportDto> => {
    const response = await apiClient.get<ReportDto>(`/reports/${id}`, { signal, headers: { Accept: ContentTypes.REPORT } });
    return response.data;
};

export const updateReportStatus = async (id: number, status: ReportStatus, signal?: AbortSignal) => {
    const response = await apiClient.patch<ReportDto>(`/reports/${id}`, { status }, { signal, headers: { "Content-Type": ContentTypes.REPORT, Accept: ContentTypes.REPORT } });
    return response.data;
};

export const deleteReport = async (id: number, signal?: AbortSignal) => {
    await apiClient.delete(`/reports/${id}`, { signal });
};

export const resolveReportListItem = async (report: ReportDto, signal?: AbortSignal): Promise<ReportListItem> => {
    const [reportedUser, reportingUser] = await Promise.all([
        fetchByUrl<UserDto>(report.links?.reportedUserUrl, ContentTypes.USER_PUBLIC, signal),
        fetchByUrl<UserDto>(report.links?.reportingUserUrl, ContentTypes.USER_PUBLIC, signal),
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
        fetchByUrl<UserDto>(report.links?.reportedUserUrl, ContentTypes.USER_PUBLIC, signal),
        fetchByUrl<UserDto>(report.links?.reportingUserUrl, ContentTypes.USER_PUBLIC, signal),
    ]);

    const target = parseReportTarget(report.links?.targetUrl);

    const [journeyData, eventData, journeyResponseData, eventResponseData] = await Promise.all([
        target?.kind === "journey"
            ? fetchMissingAsNull(() =>
                  target.id ? getJourneyById(target.id, signal) : fetchByUrl<JourneySummary>(target.url, ContentTypes.JOURNEY, signal)
              )
            : null,
        target?.kind === "event"
            ? fetchMissingAsNull(() =>
                  target.id ? getEventById(target.id, signal) : fetchByUrl<EventDto>(target.url, ContentTypes.EVENT, signal)
              )
            : null,
        target?.kind === "journeyResponse"
            ? fetchMissingAsNull(() => fetchByUrl<JourneyResponseDto>(target.url, ContentTypes.JOURNEY_RESPONSE, signal))
            : null,
        target?.kind === "eventResponse"
            ? fetchMissingAsNull(() => fetchByUrl<EventResponseDto>(target.url, ContentTypes.EVENT_RESPONSE, signal))
            : null,
    ]);

    const contentDeleted = Boolean(target) && !journeyData && !eventData && !journeyResponseData && !eventResponseData;

    const journeyResponseJourney = journeyResponseData?.links?.journeyUrl
        ? await fetchMissingAsNull(() =>
              fetchByUrl<JourneySummary>(journeyResponseData.links!.journeyUrl!, ContentTypes.JOURNEY, signal)
          )
        : null;
    const journeyResponseOwner = journeyResponseJourney?.links?.userUrl
        ? await fetchMissingAsNull(() =>
              fetchByUrl<UserDto>(journeyResponseJourney.links!.userUrl!, ContentTypes.USER_PUBLIC, signal)
          )
        : null;

    const eventResponseEvent = eventResponseData?.links?.eventUrl
        ? await fetchMissingAsNull(() =>
              fetchByUrl<EventDto>(eventResponseData.links!.eventUrl!, ContentTypes.EVENT, signal)
          )
        : null;

    return {
        id: report.id,
        description: report.description ?? null,
        reason: report.reason,
        status: report.status,
        reportedUser: mapUser(reportedUser),
        reportingUser: mapUser(reportingUser),
        contentType: resolveContentType(report),
        contentDeleted,
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
                      id: parseIdFromUrl(journeyResponseData.links?.journeyUrl) ?? 0,
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
                      id: parseIdFromUrl(eventResponseData.links?.eventUrl) ?? 0,
                      title: eventResponseEvent?.title ?? "—",
                  },
              }
            : null,
    };
};

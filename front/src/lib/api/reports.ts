import { apiClient } from "@/lib/api/client";

export type ReportType = "JOURNEY" | "EVENT" | "JOURNEY_RESPONSE" | "EVENT_RESPONSE";
export type ReportReason =
    | "SPAM"
    | "INAPPROPRIATE_CONTENT"
    | "HARASSMENT"
    | "MISINFORMATION"
    | "HATE_SPEECH"
    | "VIOLENCE"
    | "OTHER";

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

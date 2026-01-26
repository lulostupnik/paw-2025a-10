import { apiClient } from "@/lib/api/client";
import { toPaged, type PageResult } from "@/types/pagination";

export interface InterestDto {
    id: number;
    name: string;
    selfUrl?: string | null;
}

export interface InterestPayload {
    name: string;
}

export interface ListInterestsParams {
    search?: string;
    page?: number;
    size?: number;
}

export const listInterests = async (params: ListInterestsParams = {}, signal?: AbortSignal): Promise<PageResult<InterestDto>> => {
    const response = await apiClient.get<InterestDto[]>("/interests", { params, signal });
    return toPaged(response);
};

export const getInterestById = async (id: number | string, signal?: AbortSignal): Promise<InterestDto> => {
    const response = await apiClient.get<InterestDto>(`/interests/${id}`, { signal });
    return response.data;
};

export const createInterest = async (payload: InterestPayload, signal?: AbortSignal): Promise<InterestDto> => {
    const response = await apiClient.post<InterestDto>("/interests", payload, { signal });
    return response.data;
};

export const updateInterest = async (
    id: number | string,
    payload: InterestPayload,
    signal?: AbortSignal
): Promise<InterestDto> => {
    const response = await apiClient.put<InterestDto>(`/interests/${id}`, payload, { signal });
    return response.data;
};

export const deleteInterest = async (id: number | string, signal?: AbortSignal): Promise<void> => {
    await apiClient.delete(`/interests/${id}`, { signal });
};

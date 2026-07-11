import type { QueryClient } from "@tanstack/react-query";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { toPaged, type PageResult } from "@/types/pagination";
import { fetchByUrl } from "@/lib/utils/fetchByUrl";

export interface CareerDto {
    id: number;
    name: string;
    links?: {
        selfUrl?: string | null;
    } | null;
}

export interface CareerPayload {
    name: string;
}

export interface ListCareersParams {
    search?: string;
    page?: number;
    size?: number;
}

export const listCareers = async (params: ListCareersParams = {}, signal?: AbortSignal): Promise<PageResult<CareerDto>> => {
    const response = await apiClient.get<CareerDto[]>("/careers", { params, signal, headers: { Accept: ContentTypes.CAREER_LIST } });
    return toPaged(response);
};

export const getCareerById = async (id: number | string, signal?: AbortSignal): Promise<CareerDto> => {
    const response = await apiClient.get<CareerDto>(`/careers/${id}`, { signal, headers: { Accept: ContentTypes.CAREER } });
    return response.data;
};

export const getCareerByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient): Promise<CareerDto | null> =>
    fetchByUrl(queryClient, "career", url, async (fetchSignal) => {
        const response = await apiClient.get<CareerDto>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.CAREER } });
        return response.data ?? null;
    }, signal);

export const createCareer = async (payload: CareerPayload, signal?: AbortSignal): Promise<CareerDto> => {
    const response = await apiClient.post<CareerDto>("/careers", payload, { signal, headers: { "Content-Type": ContentTypes.CAREER, Accept: ContentTypes.CAREER } });
    return response.data;
};

export const updateCareer = async (id: number | string, payload: CareerPayload, signal?: AbortSignal): Promise<CareerDto> => {
    const response = await apiClient.put<CareerDto>(`/careers/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.CAREER, Accept: ContentTypes.CAREER } });
    return response.data;
};

export const deleteCareer = async (id: number | string, signal?: AbortSignal): Promise<void> => {
    await apiClient.delete(`/careers/${id}`, { signal });
};

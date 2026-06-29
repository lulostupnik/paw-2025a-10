import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { toPaged, type PageResult } from "@/types/pagination";

export interface UniversityDto {
    id: number;
    name: string;
    abbreviation: string;
    links?: {
        selfUrl?: string | null;
        cityUrl?: string | null;
    } | null;
}

export interface UniversityPayload {
    name: string;
    abbreviation: string;
    city: string;
}

export interface ListUniversitiesParams {
    search?: string;
    page?: number;
    size?: number;
}

export const listUniversities = async (
    params: ListUniversitiesParams = {},
    signal?: AbortSignal
): Promise<PageResult<UniversityDto>> => {
    const response = await apiClient.get<UniversityDto[]>("/universities", { params, signal, headers: { Accept: ContentTypes.UNIVERSITY_LIST } });
    return toPaged(response);
};

export const getUniversityById = async (id: number | string, signal?: AbortSignal): Promise<UniversityDto> => {
    const response = await apiClient.get<UniversityDto>(`/universities/${id}`, { signal, headers: { Accept: ContentTypes.UNIVERSITY } });
    return response.data;
};

export const getUniversityByUrl = async (url?: string | null, signal?: AbortSignal): Promise<UniversityDto | null> => {
    if (!url) {
        return null;
    }
    const response = await apiClient.get<UniversityDto>(normalizeApiPath(url), { signal, headers: { Accept: ContentTypes.UNIVERSITY } });
    return response.data ?? null;
};

export const createUniversity = async (payload: UniversityPayload, signal?: AbortSignal): Promise<UniversityDto> => {
    const response = await apiClient.post<UniversityDto>("/universities", payload, { signal, headers: { "Content-Type": ContentTypes.UNIVERSITY, Accept: ContentTypes.UNIVERSITY } });
    return response.data;
};

export const updateUniversity = async (
    id: number | string,
    payload: UniversityPayload,
    signal?: AbortSignal
): Promise<UniversityDto> => {
    const response = await apiClient.put<UniversityDto>(`/universities/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.UNIVERSITY, Accept: ContentTypes.UNIVERSITY } });
    return response.data;
};

export const deleteUniversity = async (id: number | string, signal?: AbortSignal): Promise<void> => {
    await apiClient.delete(`/universities/${id}`, { signal });
};

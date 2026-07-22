import type { QueryClient } from "@tanstack/react-query";
import { apiClient, normalizeApiPath } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";
import { toPaged, type PageResult } from "@/types/pagination";
import { fetchByUrl } from "@/lib/utils/fetchByUrl";

export interface CityDto {
    id: number;
    name: string;
    country?: string | null;
    links?: {
        selfUrl?: string | null;
        countryUrl?: string | null;
    } | null;
}

export interface CityPayload {
    name: string;
    countryId: number;
}

export interface ListCitiesParams {
    search?: string;
    page?: number;
    size?: number;
}

export const listCities = async (params: ListCitiesParams = {}, signal?: AbortSignal): Promise<PageResult<CityDto>> => {
    const response = await apiClient.get<CityDto[]>("/cities", { params, signal, headers: { Accept: ContentTypes.CITY_LIST } });
    return toPaged(response);
};

export const getCityById = async (id: number | string, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.get<CityDto>(`/cities/${id}`, { signal, headers: { Accept: ContentTypes.CITY } });
    return response.data;
};

export const getCityByUrl = async (url?: string | null, signal?: AbortSignal, queryClient?: QueryClient): Promise<CityDto | null> =>
    fetchByUrl(queryClient, "city", url, async (fetchSignal) => {
        const response = await apiClient.get<CityDto>(normalizeApiPath(url as string), { signal: fetchSignal, headers: { Accept: ContentTypes.CITY } });
        return response.data ?? null;
    }, signal);

export const createCity = async (payload: CityPayload, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.post<CityDto>("/cities", payload, { signal, headers: { "Content-Type": ContentTypes.CITY, Accept: ContentTypes.CITY } });
    return response.data;
};

export const updateCity = async (id: number | string, payload: CityPayload, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.patch<CityDto>(`/cities/${id}`, payload, { signal, headers: { "Content-Type": ContentTypes.CITY, Accept: ContentTypes.CITY } });
    return response.data;
};

export const deleteCity = async (id: number | string, signal?: AbortSignal): Promise<void> => {
    await apiClient.delete(`/cities/${id}`, { signal });
};

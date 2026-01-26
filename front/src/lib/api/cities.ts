import { apiClient } from "@/lib/api/client";
import { toPaged, type PageResult } from "@/types/pagination";

export interface CityDto {
    id: number;
    name: string;
    country?: string | null;
    selfUrl?: string | null;
}

export interface CityPayload {
    name: string;
    country: string;
}

export interface ListCitiesParams {
    search?: string;
    page?: number;
    size?: number;
}

export const listCities = async (params: ListCitiesParams = {}, signal?: AbortSignal): Promise<PageResult<CityDto>> => {
    const response = await apiClient.get<CityDto[]>("/cities", { params, signal });
    return toPaged(response);
};

export const getCityById = async (id: number | string, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.get<CityDto>(`/cities/${id}`, { signal });
    return response.data;
};

export const createCity = async (payload: CityPayload, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.post<CityDto>("/cities", payload, { signal });
    return response.data;
};

export const updateCity = async (id: number | string, payload: CityPayload, signal?: AbortSignal): Promise<CityDto> => {
    const response = await apiClient.put<CityDto>(`/cities/${id}`, payload, { signal });
    return response.data;
};

export const deleteCity = async (id: number | string, signal?: AbortSignal): Promise<void> => {
    await apiClient.delete(`/cities/${id}`, { signal });
};

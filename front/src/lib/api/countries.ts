import { apiClient } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";

export interface CountryDto {
    id: number;
    name: string;
    code?: string | null;
    links?: {
        selfUrl?: string | null;
    } | null;
}

export const listCountries = async (signal?: AbortSignal): Promise<CountryDto[]> => {
    const response = await apiClient.get<CountryDto[]>("/countries", {
        signal,
        headers: { Accept: ContentTypes.COUNTRY_LIST },
    });
    return response.data ?? [];
};

export const getCountryById = async (id: number | string, signal?: AbortSignal): Promise<CountryDto> => {
    const response = await apiClient.get<CountryDto>(`/countries/${id}`, {
        signal,
        headers: { Accept: ContentTypes.COUNTRY },
    });
    return response.data;
};

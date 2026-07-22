import { apiClient } from "@/lib/api/client";
import { ContentTypes } from "@/lib/api/contentTypes";

export interface CatalogOption {
    id: number;
    name: string;
}

export type CatalogSearchFn = (query: string, signal?: AbortSignal) => Promise<CatalogOption[]>;

async function requestCatalog(path: string, accept: string, query: string, signal?: AbortSignal): Promise<CatalogOption[]> {
    const trimmed = query.trim();
    const params = trimmed.length > 0 ? { search: trimmed } : undefined;
    const response = await apiClient.get(path, { params, signal, headers: { Accept: accept } });
    const payload = response.data;
    const collection: unknown[] = Array.isArray(payload)
        ? payload
        : Array.isArray(payload?.content)
          ? payload.content
          : [];

    return collection
        .map((item) => {
            const source = item as Record<string, unknown>;
            const rawId = source.id ?? source.value ?? source.ID ?? source.uid;
            const rawName = source.name ?? source.title ?? source.label ?? source.username ?? "";
            const id = typeof rawId === "number" ? rawId : Number(rawId);
            const name = typeof rawName === "string" && rawName.trim().length > 0 ? rawName.trim() : `#${id}`;
            if (Number.isNaN(id)) {
                return null;
            }
            return { id, name } as CatalogOption;
        })
        .filter((option): option is CatalogOption => Boolean(option));
}

export const searchCareers: CatalogSearchFn = (query, signal) => requestCatalog("/careers", ContentTypes.CAREER_LIST, query, signal);

export const searchUniversities: CatalogSearchFn = (query, signal) => requestCatalog("/universities", ContentTypes.UNIVERSITY_LIST, query, signal);

export const searchInterests: CatalogSearchFn = (query, signal) => requestCatalog("/interests", ContentTypes.INTEREST_LIST, query, signal);

export const searchCities: CatalogSearchFn = (query, signal) => requestCatalog("/cities", ContentTypes.CITY_LIST, query, signal);

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "/api").replace(/\/$/, "");
const JSON_HEADERS = { Accept: "application/json" } as const;

export interface CatalogOption {
    id: number;
    name: string;
}

export type CatalogSearchFn = (query: string, signal?: AbortSignal) => Promise<CatalogOption[]>;

async function requestCatalog(path: string, query: string, signal?: AbortSignal): Promise<CatalogOption[]> {
    const params = new URLSearchParams();
    if (query.trim().length > 0) {
        params.set("search", query.trim());
    }

    const url = `${API_BASE_URL}${path}${params.toString() ? `?${params.toString()}` : ""}`;
    const response = await fetch(url, { signal, headers: JSON_HEADERS });
    if (!response.ok) {
        throw new Error(`Failed to fetch ${path}`);
    }

    const payload = await response.json();
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

export const searchCareers: CatalogSearchFn = (query, signal) => requestCatalog("/careers", query, signal);

export const searchUniversities: CatalogSearchFn = (query, signal) => requestCatalog("/universities", query, signal);

export const searchInterests: CatalogSearchFn = (query, signal) => requestCatalog("/interests", query, signal);

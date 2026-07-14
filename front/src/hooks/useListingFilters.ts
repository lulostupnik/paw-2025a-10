import { useCallback, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";

export interface ListingFiltersState {
    cityId: number | null;
    cityName: string;
    universityId: number | null;
    universityName: string;
    interestId: number | null;
    interestName: string;
    afterDate: string;
    beforeDate: string;
    minRating: number | null;
    hasCapacity: boolean;
}

export const EMPTY_LISTING_FILTERS: ListingFiltersState = {
    cityId: null,
    cityName: "",
    universityId: null,
    universityName: "",
    interestId: null,
    interestName: "",
    afterDate: "",
    beforeDate: "",
    minRating: null,
    hasCapacity: false,
};

export interface ListingFiltersController {
    filters: ListingFiltersState;
    applyFilters: (next: ListingFiltersState) => void;
    resetFilters: () => void;
}

const cloneFilters = (filters: ListingFiltersState): ListingFiltersState => ({ ...filters });

const parseNumberParam = (value: string | null): number | null => {
    if (!value) {
        return null;
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
};

const parseFiltersFromParams = (params: URLSearchParams): ListingFiltersState => ({
    cityId: parseNumberParam(params.get("city")),
    cityName: params.get("cityName") ?? "",
    universityId: parseNumberParam(params.get("university")),
    universityName: params.get("universityName") ?? "",
    interestId: parseNumberParam(params.get("interests") ?? params.get("interest")),
    interestName: params.get("interestName") ?? "",
    afterDate: params.get("after") ?? "",
    beforeDate: params.get("before") ?? "",
    minRating: parseNumberParam(params.get("minRating")),
    hasCapacity: params.get("hasCapacity") === "true",
});

const setParam = (params: URLSearchParams, key: string, value: string | null) => {
    if (value && value.length > 0) {
        params.set(key, value);
    } else {
        params.delete(key);
    }
};

const applyFiltersToParams = (source: URLSearchParams, next: ListingFiltersState): URLSearchParams => {
    const params = new URLSearchParams(source);
    params.delete("page");
    setParam(params, "city", next.cityId ? String(next.cityId) : null);
    setParam(params, "cityName", next.cityId && next.cityName ? next.cityName : null);
    setParam(params, "university", next.universityId ? String(next.universityId) : null);
    setParam(params, "universityName", next.universityId && next.universityName ? next.universityName : null);
    setParam(params, "interests", next.interestId ? String(next.interestId) : null);
    setParam(params, "interestName", next.interestId && next.interestName ? next.interestName : null);
    setParam(params, "after", next.afterDate || null);
    setParam(params, "before", next.beforeDate || null);
    setParam(params, "minRating", next.minRating ? String(next.minRating) : null);
    setParam(params, "hasCapacity", next.hasCapacity ? "true" : null);
    return params;
};

export function useListingFilters(initial?: Partial<ListingFiltersState>): ListingFiltersController {
    const [filters, setFilters] = useState<ListingFiltersState>(() => ({ ...EMPTY_LISTING_FILTERS, ...initial }));

    const applyFilters = useCallback((next: ListingFiltersState) => {
        setFilters(cloneFilters(next));
    }, []);

    const resetFilters = useCallback(() => {
        setFilters(cloneFilters(EMPTY_LISTING_FILTERS));
    }, []);

    return { filters, applyFilters, resetFilters };
}

export function useUrlSyncedListingFilters(): ListingFiltersController {
    const [searchParams, setSearchParams] = useSearchParams();
    // La URL es la única fuente de verdad: los filtros se derivan de ella en vez
    // de vivir en un estado espejo que hay que re-sincronizar.
    const filters = useMemo(() => parseFiltersFromParams(searchParams), [searchParams]);

    const applyFilters = useCallback(
        (next: ListingFiltersState) => {
            setSearchParams((prev) => applyFiltersToParams(prev, next));
        },
        [setSearchParams]
    );

    const resetFilters = useCallback(() => {
        setSearchParams((prev) => applyFiltersToParams(prev, EMPTY_LISTING_FILTERS));
    }, [setSearchParams]);

    return { filters, applyFilters, resetFilters };
}

export const isListingFiltersEmpty = (filters: ListingFiltersState) =>
    !filters.cityId &&
    !filters.universityId &&
    !filters.interestId &&
    !filters.afterDate &&
    !filters.beforeDate &&
    !filters.minRating &&
    !filters.hasCapacity;

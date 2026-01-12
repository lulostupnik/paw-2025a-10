import { useCallback, useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

export interface ListingFiltersState {
    cityId: number | null;
    cityName: string;
    interestId: number | null;
    interestName: string;
    afterDate: string;
    beforeDate: string;
}

export const EMPTY_LISTING_FILTERS: ListingFiltersState = {
    cityId: null,
    cityName: "",
    interestId: null,
    interestName: "",
    afterDate: "",
    beforeDate: "",
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
    interestId: parseNumberParam(params.get("interest")),
    interestName: params.get("interestName") ?? "",
    afterDate: params.get("after") ?? "",
    beforeDate: params.get("before") ?? "",
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
    setParam(params, "city", next.cityId ? String(next.cityId) : null);
    setParam(params, "cityName", next.cityId && next.cityName ? next.cityName : null);
    setParam(params, "interest", next.interestId ? String(next.interestId) : null);
    setParam(params, "interestName", next.interestId && next.interestName ? next.interestName : null);
    setParam(params, "after", next.afterDate || null);
    setParam(params, "before", next.beforeDate || null);
    return params;
};

const areFiltersEqual = (a: ListingFiltersState, b: ListingFiltersState) =>
    a.cityId === b.cityId &&
    a.cityName === b.cityName &&
    a.interestId === b.interestId &&
    a.interestName === b.interestName &&
    a.afterDate === b.afterDate &&
    a.beforeDate === b.beforeDate;

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
    const [filters, setFilters] = useState<ListingFiltersState>(() => parseFiltersFromParams(searchParams));

    useEffect(() => {
        const parsed = parseFiltersFromParams(searchParams);
        setFilters((prev) => (areFiltersEqual(prev, parsed) ? prev : parsed));
    }, [searchParams]);

    const applyFilters = useCallback(
        (next: ListingFiltersState) => {
            setFilters(cloneFilters(next));
            setSearchParams((prev) => applyFiltersToParams(prev, next), { replace: true });
        },
        [setSearchParams]
    );

    const resetFilters = useCallback(() => {
        setFilters(cloneFilters(EMPTY_LISTING_FILTERS));
        setSearchParams((prev) => applyFiltersToParams(prev, EMPTY_LISTING_FILTERS), { replace: true });
    }, [setSearchParams]);

    return { filters, applyFilters, resetFilters };
}

export const isListingFiltersEmpty = (filters: ListingFiltersState) =>
    !filters.cityId &&
    !filters.interestId &&
    !filters.afterDate &&
    !filters.beforeDate;

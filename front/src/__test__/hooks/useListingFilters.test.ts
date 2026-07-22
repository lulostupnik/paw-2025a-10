import { describe, it, expect, vi, beforeEach } from "vitest";
import { createElement, type ReactNode } from "react";
import { MemoryRouter, useLocation } from "react-router-dom";
import { renderHook, act, waitFor } from "../setup/utils";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useListingFilters, useUrlSyncedListingFilters, EMPTY_LISTING_FILTERS, type ListingFiltersState } from "@/hooks/useListingFilters";

const mockGetCityById = vi.fn();
const mockGetUniversityById = vi.fn();
const mockGetInterestById = vi.fn();

vi.mock("@/lib/api/cities", () => ({
    getCityById: (...args: unknown[]) => mockGetCityById(...args),
}));

vi.mock("@/lib/api/universities", () => ({
    getUniversityById: (...args: unknown[]) => mockGetUniversityById(...args),
}));

vi.mock("@/lib/api/interests", () => ({
    getInterestById: (...args: unknown[]) => mockGetInterestById(...args),
}));

describe("useListingFilters", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockGetCityById.mockResolvedValue({ id: 2, name: "Boston" });
        mockGetUniversityById.mockResolvedValue({ id: 4, name: "MIT" });
        mockGetInterestById.mockResolvedValue({ id: 6, name: "Photography" });
    });

    const withProviders = (entry: string) =>
        ({ children }: { children: ReactNode }) =>
            createElement(
                QueryClientProvider,
                { client: new QueryClient({ defaultOptions: { queries: { retry: false } } }) },
                createElement(MemoryRouter, { initialEntries: [entry] }, children),
            );

    it("should start with empty filters by default", () => {
        const { result } = renderHook(() => useListingFilters());
        expect(result.current.filters).toEqual(EMPTY_LISTING_FILTERS);
    });

    it("should accept initial partial filters", () => {
        const { result } = renderHook(() =>
            useListingFilters({ cityId: 1, cityName: "Buenos Aires" }),
        );
        expect(result.current.filters.cityId).toBe(1);
        expect(result.current.filters.cityName).toBe("Buenos Aires");
        expect(result.current.filters.universityId).toBeNull();
    });

    it("should apply new filters", () => {
        const { result } = renderHook(() => useListingFilters());

        const newFilters: ListingFiltersState = {
            ...EMPTY_LISTING_FILTERS,
            cityId: 2,
            cityName: "Boston",
            minRating: 4,
            hasCapacity: true,
        };

        act(() => {
            result.current.applyFilters(newFilters);
        });

        expect(result.current.filters.cityId).toBe(2);
        expect(result.current.filters.cityName).toBe("Boston");
        expect(result.current.filters.minRating).toBe(4);
        expect(result.current.filters.hasCapacity).toBe(true);
    });

    it("should reset filters to empty state", () => {
        const { result } = renderHook(() =>
            useListingFilters({ cityId: 1, cityName: "Test" }),
        );

        expect(result.current.filters.cityId).toBe(1);

        act(() => {
            result.current.resetFilters();
        });

        expect(result.current.filters).toEqual(EMPTY_LISTING_FILTERS);
    });

    it("should apply filters with date ranges", () => {
        const { result } = renderHook(() => useListingFilters());

        const filtersWithDates: ListingFiltersState = {
            ...EMPTY_LISTING_FILTERS,
            afterDate: "2026-01-01",
            beforeDate: "2026-12-31",
        };

        act(() => {
            result.current.applyFilters(filtersWithDates);
        });

        expect(result.current.filters.afterDate).toBe("2026-01-01");
        expect(result.current.filters.beforeDate).toBe("2026-12-31");
    });

    it("should reset page when applying URL-synced filters", () => {
        const wrapper = withProviders("/events?page=8&sort=event-date-desc");
        const { result } = renderHook(
            () => {
                const controller = useUrlSyncedListingFilters();
                const location = useLocation();
                return { ...controller, location };
            },
            { wrapper },
        );

        act(() => {
            result.current.applyFilters({
                ...EMPTY_LISTING_FILTERS,
                cityId: 2,
                cityName: "Boston",
            });
        });

        const params = new URLSearchParams(result.current.location.search);
        expect(params.get("page")).toBeNull();
        expect(params.get("sort")).toBe("event-date-desc");
        expect(params.get("city")).toBe("2");
        expect(params.get("cityName")).toBe("Boston");
    });

    it("should reset page when clearing URL-synced filters", () => {
        const wrapper = withProviders("/events?page=8&city=2&cityName=Boston");
        const { result } = renderHook(
            () => {
                const controller = useUrlSyncedListingFilters();
                const location = useLocation();
                return { ...controller, location };
            },
            { wrapper },
        );

        act(() => {
            result.current.resetFilters();
        });

        const params = new URLSearchParams(result.current.location.search);
        expect(params.get("page")).toBeNull();
        expect(params.get("city")).toBeNull();
        expect(params.get("cityName")).toBeNull();
    });

    it("should support legacy lowercase query params", () => {
        const wrapper = withProviders("/events?city=2&cityname=Boston&university=4&universityname=MIT&interests=6&interestsname=Photography");
        const { result } = renderHook(() => useUrlSyncedListingFilters(), { wrapper });

        expect(result.current.filters.cityName).toBe("Boston");
        expect(result.current.filters.universityName).toBe("MIT");
        expect(result.current.filters.interestName).toBe("Photography");
    });

    it("should resolve missing filter names from ids", async () => {
        const wrapper = withProviders("/events?city=2&university=4&interests=6");
        const { result } = renderHook(() => useUrlSyncedListingFilters(), { wrapper });

        await waitFor(() => expect(result.current.filters.cityName).toBe("Boston"));

        expect(result.current.filters.cityName).toBe("Boston");
        expect(result.current.filters.universityName).toBe("MIT");
        expect(result.current.filters.interestName).toBe("Photography");
    });
});

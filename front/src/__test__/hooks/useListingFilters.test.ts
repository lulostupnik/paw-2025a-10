import { describe, it, expect } from "vitest";
import { createElement, type ReactNode } from "react";
import { MemoryRouter, useLocation } from "react-router-dom";
import { renderHook, act } from "../setup/utils";
import { useListingFilters, useUrlSyncedListingFilters, EMPTY_LISTING_FILTERS, type ListingFiltersState } from "@/hooks/useListingFilters";

describe("useListingFilters", () => {
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
        const wrapper = ({ children }: { children: ReactNode }) =>
            createElement(MemoryRouter, { initialEntries: ["/events?page=8&sort=event-date-desc"] }, children);
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
        const wrapper = ({ children }: { children: ReactNode }) =>
            createElement(MemoryRouter, { initialEntries: ["/events?page=8&city=2&cityName=Boston"] }, children);
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
});

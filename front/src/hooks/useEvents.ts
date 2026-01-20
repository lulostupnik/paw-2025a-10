import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { EventSummary, FetchEventsParams } from "@/lib/api/events";
import { fetchEvents, mapEventDtoToSummary } from "@/lib/api/events";
import { getEventsMock, type EventListScenario } from "@/mocks/events.mock";

interface UseEventsResult {
    events: EventSummary[];
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

export function useEvents(params?: FetchEventsParams): UseEventsResult {
    const scenario = useMemo<EventListScenario>(() => {
        const search = typeof window !== "undefined" ? new URLSearchParams(window.location.search) : null;
        const raw = search?.get("eventsScenario") ?? search?.get("scenario");
        if (raw === "empty" || raw === "error" || raw === "loading") {
            return raw;
        }
        return "normal";
    }, []);

    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchEventsParams>(() => ({ ...(params ?? {}) }), [serializedParams]);

    const query = useQuery({
        queryKey: ["events", memoizedParams],
        queryFn: async ({ signal }) => {
            const data = await fetchEvents(memoizedParams, signal);
            return data.map(mapEventDtoToSummary);
        },
        placeholderData: keepPreviousData,
        enabled: scenario === "normal",
    });

    if (scenario === "loading") {
        return {
            events: [],
            loading: true,
            error: null,
            refetch: () => undefined,
        };
    }

    if (scenario === "error") {
        return {
            events: getEventsMock("normal"),
            loading: false,
            error: "Failed to fetch events",
            refetch: () => undefined,
        };
    }

    if (scenario === "empty") {
        return {
            events: [],
            loading: false,
            error: null,
            refetch: () => undefined,
        };
    }

    return {
        events: query.data ?? getEventsMock("normal"),
        loading: query.isLoading,
        error: query.isError ? (query.error instanceof Error ? query.error.message : "unknown-error") : null,
        refetch: query.refetch,
    };
}

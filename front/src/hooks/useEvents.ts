import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { EventSummary, FetchEventsParams } from "@/lib/api/events";
import { fetchEvents, mapEventDtoToSummary } from "@/lib/api/events";

interface UseEventsResult {
    events: EventSummary[];
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

export function useEvents(params?: FetchEventsParams): UseEventsResult {
    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchEventsParams>(() => JSON.parse(serializedParams), [serializedParams]);

    const query = useQuery({
        queryKey: ["events", memoizedParams],
        queryFn: async ({ signal }) => {
            const data = await fetchEvents(memoizedParams, signal);
            return data.map(mapEventDtoToSummary);
        },
        placeholderData: keepPreviousData,
    });

    return {
        events: query.data ?? [],
        loading: query.isLoading,
        error: query.isError
            ? query.error instanceof Error
                ? query.error.message
                : "unknown-error"
            : null,
        refetch: query.refetch,
    };
}

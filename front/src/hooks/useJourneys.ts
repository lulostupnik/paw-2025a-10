import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { JourneySummary } from "@/types/journey";
import { getJourneys, resolveJourneySummary, type FetchJourneysParams } from "@/lib/api/journeys";

interface UseJourneysResult {
    journeys: JourneySummary[];
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

export function useJourneys(params?: FetchJourneysParams): UseJourneysResult {
    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchJourneysParams>(() => ({ ...(params ?? {}) }), [serializedParams]);

    const query = useQuery({
        queryKey: ["journeys", memoizedParams],
        queryFn: async ({ signal }) => {
            const data = await getJourneys(memoizedParams, signal);
            return Promise.all(data.map((journey) => resolveJourneySummary(journey, signal)));
        },
        placeholderData: keepPreviousData,
    });

    return {
        journeys: query.data ?? [],
        loading: query.isLoading,
        error: query.isError ? (query.error instanceof Error ? query.error.message : "unknown-error") : null,
        refetch: query.refetch,
    };
}

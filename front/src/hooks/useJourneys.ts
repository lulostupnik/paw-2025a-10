import { useMemo } from "react";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import type { JourneySummary } from "@/types/journey";
import { getJourneys, resolveJourneySummary, type FetchJourneysParams } from "@/lib/api/journeys";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";

interface UseJourneysResult {
    journeys: PageResult<JourneySummary>;
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

export function useJourneys(params?: FetchJourneysParams): UseJourneysResult {
    const queryClient = useQueryClient();
    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchJourneysParams>(() => ({ ...(params ?? {}) }), [serializedParams]);

    const query = useQuery({
        queryKey: ["journeys", memoizedParams],
        queryFn: async ({ signal }) => {
            const data = await getJourneys(memoizedParams, signal);
            const journeySummary = await Promise.all(data.content.map((journey: JourneySummary) => resolveJourneySummary(journey, signal, queryClient)));
            return mapPageList(data, journeySummary);
        },
        placeholderData: keepPreviousData,
    });

    return {
        journeys: query.data ?? emptyPage(),
        loading: query.isLoading,
        error: query.isError ? (query.error instanceof Error ? query.error.message : "unknown-error") : null,
        refetch: query.refetch,
    };
}

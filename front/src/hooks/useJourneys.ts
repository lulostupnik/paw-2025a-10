import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { JourneySummary } from "@/types/journey";
import { getJourneys, resolveJourneySummary, type FetchJourneysParams } from "@/lib/api/journeys";
import { getJourneysMock, type JourneyListScenario } from "@/mocks/journeys.mock";

interface UseJourneysResult {
    journeys: JourneySummary[];
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

const USE_MOCK_FALLBACK = true; // Set to false to disable fallback mocks.

export function useJourneys(params?: FetchJourneysParams): UseJourneysResult {
    const scenario = useMemo<JourneyListScenario>(() => {
        const search = typeof window !== "undefined" ? new URLSearchParams(window.location.search) : null;
        const raw = search?.get("journeysScenario") ?? search?.get("scenario");
        if (raw === "empty" || raw === "error" || raw === "loading") {
            return raw;
        }
        return "normal";
    }, []);

    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchJourneysParams>(() => ({ ...(params ?? {}) }), [serializedParams]);

    const query = useQuery({
        queryKey: ["journeys", memoizedParams],
        queryFn: async ({ signal }) => {
            const data = await getJourneys(memoizedParams, signal);
            return Promise.all(data.map((journey) => resolveJourneySummary(journey, signal)));
        },
        placeholderData: keepPreviousData,
        enabled: scenario === "normal",
    });

    if (scenario === "loading") {
        return {
            journeys: USE_MOCK_FALLBACK ? getJourneysMock("normal") : [],
            loading: true,
            error: null,
            refetch: () => undefined,
        };
    }

    if (scenario === "error") {
        return {
            journeys: USE_MOCK_FALLBACK ? getJourneysMock("normal") : [],
            loading: false,
            error: "Failed to fetch journeys",
            refetch: () => undefined,
        };
    }

    if (scenario === "empty") {
        return {
            journeys: [],
            loading: false,
            error: null,
            refetch: () => undefined,
        };
    }

    return {
        journeys: query.data ?? (USE_MOCK_FALLBACK ? getJourneysMock("normal") : []),
        loading: query.isLoading,
        error: query.isError ? (query.error instanceof Error ? query.error.message : "unknown-error") : null,
        refetch: query.refetch,
    };
}

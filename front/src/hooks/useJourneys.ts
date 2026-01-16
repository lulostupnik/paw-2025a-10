import { useCallback, useEffect, useMemo, useState } from "react";
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
    const [journeys, setJourneys] = useState<JourneySummary[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [reloadKey, setReloadKey] = useState(0);
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

    useEffect(() => {
        if (scenario === "loading") {
            setLoading(true);
            setError(null);
            setJourneys(USE_MOCK_FALLBACK ? getJourneysMock("normal") : []);
            return;
        }

        if (scenario === "error") {
            setLoading(false);
            setError("Failed to fetch journeys");
            setJourneys(USE_MOCK_FALLBACK ? getJourneysMock("normal") : []);
            return;
        }

        if (scenario === "empty") {
            setLoading(false);
            setError(null);
            setJourneys([]);
            return;
        }

        const controller = new AbortController();
        setLoading(true);
        setError(null);

        getJourneys(memoizedParams, controller.signal)
            .then(async (data) => {
                if (controller.signal.aborted) {
                    return;
                }
                const resolved = await Promise.all(data.map((journey) => resolveJourneySummary(journey, controller.signal)));
                setJourneys(resolved);
            })
            .catch((err) => {
                if (controller.signal.aborted) {
                    return;
                }
                setError(err instanceof Error ? err.message : "unknown-error");
                setJourneys(USE_MOCK_FALLBACK ? getJourneysMock("normal") : []);
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            });

        return () => controller.abort();
    }, [memoizedParams, reloadKey, scenario]);

    const refetch = useCallback(() => setReloadKey((key) => key + 1), []);

    return { journeys, loading, error, refetch };
}

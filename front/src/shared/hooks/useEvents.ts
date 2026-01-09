import { useCallback, useEffect, useMemo, useState } from "react";
import type { EventSummary, FetchEventsParams } from "../api/events";
import { fetchEvents, mapEventDtoToSummary } from "../api/events";

interface UseEventsResult {
    events: EventSummary[];
    loading: boolean;
    error: string | null;
    refetch: () => void;
}

export function useEvents(params?: FetchEventsParams): UseEventsResult {
    const [events, setEvents] = useState<EventSummary[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [reloadKey, setReloadKey] = useState(0);

    const serializedParams = useMemo(() => JSON.stringify(params ?? {}), [params]);
    const memoizedParams = useMemo<FetchEventsParams>(() => ({ ...(params ?? {}) }), [serializedParams]);

    useEffect(() => {
        const controller = new AbortController();
        setLoading(true);
        setError(null);

        fetchEvents(memoizedParams, controller.signal)
            .then((data) => {
                if (controller.signal.aborted) {
                    return;
                }
                setEvents(data.map(mapEventDtoToSummary));
            })
            .catch((err) => {
                if (controller.signal.aborted) {
                    return;
                }
                setEvents([]);
                setError(err instanceof Error ? err.message : "unknown-error");
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            });

        return () => controller.abort();
    }, [memoizedParams, reloadKey]);

    const refetch = useCallback(() => setReloadKey((key) => key + 1), []);

    return { events, loading, error, refetch };
}

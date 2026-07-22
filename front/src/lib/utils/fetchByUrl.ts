import type { QueryClient } from "@tanstack/react-query";
import { normalizeApiPath } from "@/lib/api/client";

const DEFAULT_BY_URL_STALE_TIME = 5 * 60_000;
const BY_URL_STALE_TIME_BY_PREFIX: Record<string, number> = {
    "user-public": 60_000,
};

export async function fetchByUrl<T>(
    queryClient: QueryClient | undefined,
    keyPrefix: string,
    url: string | null | undefined,
    fetcher: (signal?: AbortSignal) => Promise<T>,
    signal?: AbortSignal
): Promise<T | null> {
    if (!url) {
        return null;
    }

    const normalizedUrl = normalizeApiPath(url as string);

    if (!queryClient) {
        return fetcher(signal);
    }

    const staleTime = BY_URL_STALE_TIME_BY_PREFIX[keyPrefix] ?? DEFAULT_BY_URL_STALE_TIME;

    return queryClient.fetchQuery({
        queryKey: ["byUrl", keyPrefix, normalizedUrl],
        staleTime,
        queryFn: ({ signal: querySignal }) => fetcher(querySignal),
    });
}

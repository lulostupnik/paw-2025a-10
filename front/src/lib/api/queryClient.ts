import { QueryClient } from "@tanstack/react-query";

const NON_RETRIABLE_QUERY_STATUSES = new Set([400, 401, 403, 404, 415]);

export const shouldRetryQuery = (failureCount: number, error: unknown): boolean => {
    const status = (error as { response?: { status?: number } } | undefined)?.response?.status;
    if (status && NON_RETRIABLE_QUERY_STATUSES.has(status)) {
        return false;
    }
    return failureCount < 3;
};

export const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 30_000,
            refetchOnWindowFocus: false,
            retry: shouldRetryQuery,
        },
    },
});

import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { PageResult } from "@/types/pagination";
import type { ProfileSummary } from "@/types/profile";
import { listUsers, mapUserToProfileSummary } from "@/lib/api/users";

export interface FetchProfilesParams {
    page?: number;
    size?: number;
    search?: string;
}

interface UseProfilesListResult {
    data: PageResult<ProfileSummary>;
    isLoading: boolean;
    isError: boolean;
    error: string | null;
    refetch: () => void;
}

const paginate = <T,>(items: T[], page: number, pageSize: number): PageResult<T> => {
    const totalItems = items.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
    const safePage = Math.min(Math.max(page, 1), totalPages);
    const start = (safePage - 1) * pageSize;
    return {
        content: items.slice(start, start + pageSize),
        totalPages,
        currentPage: safePage,
        pageSize,
        totalItems,
    };
};

export const useProfilesList = (params: FetchProfilesParams = {}): UseProfilesListResult => {
    const query = useQuery({
        queryKey: ["profilesList", params],
        queryFn: async ({ signal }) => {
            const page = params.page ?? 1;
            const size = params.size ?? 10;
            const users = await listUsers(
                {
                    search: params.search?.trim() || undefined,
                    page: page - 1,
                    size,
                },
                signal
            );
            const summaries = users.map(mapUserToProfileSummary);
            return paginate(summaries, page, size);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? paginate([], params.page ?? 1, params.size ?? 10),
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.isError ? "Failed to load profiles" : null,
        refetch: query.refetch,
    };
};

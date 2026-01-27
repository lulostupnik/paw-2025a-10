import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";
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

export const useProfilesList = (params: FetchProfilesParams = {}): UseProfilesListResult => {
    const query = useQuery({
        queryKey: ["profilesList", params],
        queryFn: async ({ signal }) => {
            const page = params.page ?? 1;
            const size = params.size ?? 10;
            const users = await listUsers(
                {
                    search: params.search?.trim() || undefined,
                    page: page,
                    size,
                },
                signal
            );
            const summaries = users.content.map(mapUserToProfileSummary);
            return mapPageList(users, summaries);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? emptyPage(),
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.isError ? "Failed to load profiles" : null,
        refetch: query.refetch,
    };
};

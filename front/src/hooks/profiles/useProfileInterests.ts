import { getUserId } from "@/lib/auth/auth";
import type { ProfileInterest } from "@/types/profile";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { getUserInterests } from "@/lib/api/users";
import { emptyPage, type PageResult } from "@/types/pagination";

interface UseProfileInterestResult {
    data: PageResult<ProfileInterest>;
    isLoading: boolean;
    isError: boolean;
    isNotFound: boolean | null;
    isFetching: boolean | null;
    refetch: () => void;
}

type ProfileInterestParams = { profileId: string, page: number, size: number, url?: string }

export const useProfileInterests = (params?: ProfileInterestParams): UseProfileInterestResult => {
    
    const query = useQuery({
        queryKey: ["profileInterests", params],
        queryFn: async ({ signal }) => {
            let resolvedId = params?.profileId;
            if (!resolvedId || resolvedId === "me") {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                resolvedId = userId.toString();
            }
            const interests = await getUserInterests(resolvedId, params, signal);
            return interests;
        },
        placeholderData: keepPreviousData
    });

    const status = (query.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data: query.data ?? emptyPage(),
        isLoading: query.isLoading,
        isError: query.isError && !isNotFound,
        isNotFound,
        refetch: query.refetch,
        isFetching: query.isFetching
    };
};

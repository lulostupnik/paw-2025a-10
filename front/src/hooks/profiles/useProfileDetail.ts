import { getUserId } from "@/lib/auth/auth";
import type { ProfileDetail } from "@/types/profile";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildProfileDetail, getProfileDetail } from "@/lib/api/users";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

interface UseProfileDetailResult {
    data: ProfileDetail | null;
    isLoading: boolean;
    isError: boolean;
    isNotFound: boolean | null;
    isFetching: boolean | null;
    refetch: () => void;
}

type ProfileDetailParams = { profileId?: string; enabled?: boolean } | string | undefined;

export const useProfileDetail = (params?: ProfileDetailParams): UseProfileDetailResult => {
    const profileId = typeof params === "string" ? params : params?.profileId;
    const enabled = typeof params === "string" ? true : params?.enabled ?? true;
    const queryClient = useQueryClient();

    const query = useQuery({
        queryKey: ["profileDetail", profileId],
        queryFn: async ({ signal }) => {
            let resolvedId = profileId;
            const currentUserId = getUserId();
            if (!resolvedId || resolvedId === "me") {
                if (!currentUserId) {
                    throw new Error("missing-user-id");
                }
                resolvedId = currentUserId.toString();
            }
            const user = await getProfileDetail(resolvedId, signal);
            return buildProfileDetail(user, signal, queryClient);
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled,
    });

    const status = (query.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data: query.data ?? null,
        isLoading: query.isLoading,
        isError: query.isError && !isNotFound,
        isNotFound,
        refetch: query.refetch,
        isFetching: query.isFetching
    };
};

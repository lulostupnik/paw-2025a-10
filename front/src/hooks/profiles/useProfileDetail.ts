import { getUserId } from "@/lib/auth/auth";
import type { ProfileDetail } from "@/types/profile";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { buildProfileDetail, getProfileDetail } from "@/lib/api/users";

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

    const query = useQuery({
        queryKey: ["profileDetail", profileId],
        queryFn: async ({ signal }) => {
            let resolvedId = profileId;
            if (!resolvedId || resolvedId === "me") {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                resolvedId = userId.toString();
            }
            const user = await getProfileDetail(resolvedId, signal);
            return buildProfileDetail(user);
        },
        placeholderData: keepPreviousData,
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

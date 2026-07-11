import { getEmail, getUserId } from "@/lib/auth/auth";
import type { ProfileDetail } from "@/types/profile";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildProfileInfo, getProfileDetail } from "@/lib/api/users";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

interface UseProfileDetailResult {
    data: ProfileDetail | null;
    isLoading: boolean;
    isError: boolean;
    isNotFound: boolean | null;
    isFetching: boolean | null;
    infoLoading: boolean;
    refetch: () => void;
}

type ProfileDetailParams = { profileId?: string; enabled?: boolean } | string | undefined;

const EMPTY_RATING_STATS = { averageCreatedEventsRating: null, averageAttendedEventsRating: null };

export const useProfileDetail = (params?: ProfileDetailParams): UseProfileDetailResult => {
    const profileId = typeof params === "string" ? params : params?.profileId;
    const enabled = typeof params === "string" ? true : params?.enabled ?? true;
    const queryClient = useQueryClient();

    // Core profile (name, username, avatar) drives the header and renders as soon
    // as it resolves. Rating stats, university and career load separately.
    const coreQuery = useQuery({
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
            return getProfileDetail(resolvedId, signal);
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled,
    });

    const core = coreQuery.data ?? null;

    const infoQuery = useQuery({
        queryKey: ["profileInfo", core?.id],
        queryFn: ({ signal }) => buildProfileInfo(core!, signal, queryClient),
        enabled: enabled && Boolean(core),
        placeholderData: keepPreviousData,
    });

    const status = (coreQuery.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    const isMine = core ? core.id == getUserId() : false;
    const data: ProfileDetail | null = core
        ? {
              ...core,
              isMine,
              email: isMine ? getEmail() : null,
              ratingStats: infoQuery.data?.ratingStats ?? EMPTY_RATING_STATS,
              university: infoQuery.data?.university ?? null,
              career: infoQuery.data?.career ?? null,
          }
        : null;

    return {
        data,
        isLoading: coreQuery.isLoading,
        isError: coreQuery.isError && !isNotFound,
        isNotFound,
        isFetching: coreQuery.isFetching,
        infoLoading: Boolean(core) && infoQuery.isLoading,
        refetch: coreQuery.refetch,
    };
};

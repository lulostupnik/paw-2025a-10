import { useCallback, useMemo, useState } from "react";
import { getUserId } from "@/lib/auth/auth";
import type { ProfileDetail } from "@/types/profile";
import { getProfileDetailMock, type ProfileScenario } from "@/mocks/profiles.mock";
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

interface ProfileDetailParams {
    scenario?: ProfileScenario;
    profileId?: string;
}

export const useProfileDetail = ({ scenario = "normal", profileId }: ProfileDetailParams): UseProfileDetailResult => {
    const activeScenario = useMemo<ProfileScenario>(() => scenario, [scenario]);
    const USE_MOCK_FALLBACK = true; // Set to false to disable fallback mocks.

    const query = useQuery({
        queryKey: ["journeyDetail", profileId],
        queryFn: async ({ signal }) => {
            if (!profileId || profileId == 'me') {
                profileId = getUserId().toString()
            }
            const user = await getProfileDetail(profileId, signal);
            return buildProfileDetail(user);
            return user
        },
        placeholderData: keepPreviousData,
        enabled: activeScenario === "normal",
    });

    if (activeScenario === "loading") {
        return {
            data: getProfileDetailMock(profileId ?? 'me', "normal"),
            isLoading: true,
            isError: false,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }
    if (activeScenario === "error") {
        return {
            data: USE_MOCK_FALLBACK ? getProfileDetailMock(profileId ?? 'me', "normal") : getProfileDetailMock(profileId ?? 'me', "empty"),
            isLoading: false,
            isError: true,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }
    if (activeScenario === "empty") {
        return {
            data: getProfileDetailMock(profileId ?? 'me', "empty"),
            isLoading: false,
            isError: false,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }

    const status = (query.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data: query.data ?? (USE_MOCK_FALLBACK ? getProfileDetailMock(profileId ?? 'me', "normal") : getProfileDetailMock(profileId ?? 'me', "empty")),
        isLoading: query.isLoading,
        isError: query.isError && !isNotFound,
        isNotFound,
        refetch: query.refetch,
        isFetching: query.isFetching
    };
};

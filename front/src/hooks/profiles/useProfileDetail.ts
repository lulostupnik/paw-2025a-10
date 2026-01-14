import { useCallback, useMemo, useState } from "react";
import { getUserId } from "@/lib/auth/auth";
import type { ProfileDetail } from "@/types/profile";
import { getProfileDetailMock } from "@/mocks/profiles.mock";
import { getProfileScenarioFromSearch } from "@/hooks/profiles/useProfileScenario";

interface UseProfileDetailResult {
    data: ProfileDetail | null;
    isLoading: boolean;
    isError: boolean;
    error: string | null;
    refetch: () => void;
}

export const useProfileDetail = (profileId: string): UseProfileDetailResult => {
    const scenario = getProfileScenarioFromSearch();
    const [reloadKey, setReloadKey] = useState(0);

    const data = useMemo(() => {
        // TODO: GET /api/profiles/{profileId}
        // TODO: expected response shape: ProfileDetail
        const USE_MOCKS = true;
        if (USE_MOCKS) {
            const mockProfile = getProfileDetailMock(profileId, scenario);
            const currentUserId = getUserId();
            return {
                ...mockProfile,
                isMine: profileId === "me" || String(currentUserId) === String(mockProfile.id),
            };
        }
        return null;
    }, [profileId, reloadKey, scenario]);

    const refetch = useCallback(() => setReloadKey((value) => value + 1), []);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
        error: scenario === "error" ? "Failed to load profile" : null,
        refetch,
    };
};

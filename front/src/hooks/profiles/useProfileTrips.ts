import { useCallback, useMemo, useState } from "react";
import type { PageResult } from "@/types/pagination";
import type { ProfileJourney } from "@/types/journey";
import { getProfileDetailMock, type ProfileScenario } from "@/mocks/profiles.mock";
import { getProfileScenarioFromSearch } from "@/hooks/profiles/useProfileScenario";

export interface ProfileTripsParams {
    page?: number;
    size?: number;
}

interface ProfileTripsResult {
    data: PageResult<ProfileJourney>;
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

const buildJourneys = (profileId: string, scenario: ProfileScenario): ProfileJourney[] => {
    const profile = getProfileDetailMock(profileId, scenario);
    return profile.journey ? [profile.journey] : [];
};

export const useProfileTrips = (profileId: string, params: ProfileTripsParams = {}): ProfileTripsResult => {
    const scenario = getProfileScenarioFromSearch();
    const [reloadKey, setReloadKey] = useState(0);

    const data = useMemo(() => {
        // TODO: GET /api/profiles/{profileId}/journeys?page={page}&size={size}
        // TODO: expected response shape: { content: ProfileJourney[], totalPages, currentPage, pageSize, totalItems }
        const USE_MOCKS = true;
        if (USE_MOCKS) {
            const journeys = buildJourneys(profileId, scenario);
            return paginate(journeys, params.page ?? 1, params.size ?? 6);
        }
        return paginate([], params.page ?? 1, params.size ?? 6);
    }, [params.page, params.size, profileId, reloadKey, scenario]);

    const refetch = useCallback(() => setReloadKey((value) => value + 1), []);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
        error: scenario === "error" ? "Failed to load profile journeys" : null,
        refetch,
    };
};

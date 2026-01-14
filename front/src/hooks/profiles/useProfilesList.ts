import { useCallback, useMemo, useState } from "react";
import type { PageResult } from "@/types/pagination";
import type { ProfileSummary } from "@/types/profile";
import { getProfilesListMock, type ProfileScenario } from "@/mocks/profiles.mock";
import { getProfileScenarioFromSearch } from "@/hooks/profiles/useProfileScenario";

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
    const scenario = getProfileScenarioFromSearch();
    const [reloadKey, setReloadKey] = useState(0);

    const data = useMemo(() => {
        // TODO: GET /api/profiles?page={page}&size={size}&search={search}
        // TODO: expected response shape: { content: ProfileSummary[], totalPages, currentPage, pageSize, totalItems }
        const USE_MOCKS = true;
        if (USE_MOCKS) {
            const allProfiles = getProfilesListMock(scenario);
            const page = params.page ?? 1;
            const size = params.size ?? 10;
            return paginate(allProfiles, page, size);
        }

        return paginate([], params.page ?? 1, params.size ?? 10);
    }, [params.page, params.search, params.size, reloadKey, scenario]);

    const refetch = useCallback(() => setReloadKey((value) => value + 1), []);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
        error: scenario === "error" ? "Failed to load profiles" : null,
        refetch,
    };
};

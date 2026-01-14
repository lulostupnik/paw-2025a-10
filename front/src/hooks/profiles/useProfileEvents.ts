import { useCallback, useMemo, useState } from "react";
import type { PageResult } from "@/types/pagination";
import type { ProfileEvent } from "@/types/event";
import { getProfileEventsMock } from "@/mocks/profiles.mock";
import { getProfileScenarioFromSearch } from "@/hooks/profiles/useProfileScenario";

export interface ProfileEventsParams {
    createdPage?: number;
    attendingPage?: number;
    finishedPage?: number;
    size?: number;
}

interface ProfileEventsResult {
    created: PageResult<ProfileEvent>;
    attending: PageResult<ProfileEvent>;
    finished: PageResult<ProfileEvent>;
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

export const useProfileEvents = (profileId: string, params: ProfileEventsParams = {}): ProfileEventsResult => {
    const scenario = getProfileScenarioFromSearch();
    const [reloadKey, setReloadKey] = useState(0);
    const pageSize = params.size ?? 6;

    const data = useMemo(() => {
        // TODO: GET /api/profiles/{profileId}/events?type=created&page={page}&size={size}
        // TODO: GET /api/profiles/{profileId}/events?type=attending&page={page}&size={size}
        // TODO: GET /api/profiles/{profileId}/events?type=finished&page={page}&size={size}
        // TODO: expected response shape per endpoint: { content: ProfileEvent[], totalPages, currentPage, pageSize, totalItems }
        const USE_MOCKS = true;
        if (USE_MOCKS) {
            const mock = getProfileEventsMock(scenario);
            return {
                created: paginate(mock.created, params.createdPage ?? 1, pageSize),
                attending: paginate(mock.attending, params.attendingPage ?? 1, pageSize),
                finished: paginate(mock.finished, params.finishedPage ?? 1, pageSize),
            };
        }
        return {
            created: paginate([], 1, pageSize),
            attending: paginate([], 1, pageSize),
            finished: paginate([], 1, pageSize),
        };
    }, [pageSize, params.attendingPage, params.createdPage, params.finishedPage, profileId, reloadKey, scenario]);

    const refetch = useCallback(() => setReloadKey((value) => value + 1), []);

    return {
        created: data.created,
        attending: data.attending,
        finished: data.finished,
        isLoading: scenario === "loading",
        isError: scenario === "error",
        error: scenario === "error" ? "Failed to load profile events" : null,
        refetch,
    };
};

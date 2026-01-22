import type { PageResult } from "@/types/pagination";
import type { ProfileEvent } from "@/types/event";
import { getProfileEventsMock } from "@/mocks/profiles.mock";
import { getProfileScenarioFromSearch } from "@/hooks/profiles/useProfileScenario";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { buildProfileEvent, fetchEvents } from "@/lib/api/events";
import { getUserId } from "@/lib/auth/auth";

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
    refetch: () => void;
    isFetching: boolean;
}

const paginate = <T,>(items: T[], page: number, pageSize: number): PageResult<T> => {
    return {
        content: items,
        totalPages: 0, //TODO send totalPages / totalItems info (could be a header?)
        currentPage: page,
        pageSize,
        totalItems: 0,
    };
};

export const useProfileEvents = (profileId: string, params: ProfileEventsParams = {}): ProfileEventsResult => {
    const scenario = getProfileScenarioFromSearch();
    const pageSize = params.size ?? 6;
    
    const USE_MOCK_FALLBACK = true; // Set to false to disable fallback mocks.

    const query = useQuery({
        queryKey: ["profileEvents", profileId],
        queryFn: async ({ signal }) => {
            if (!profileId || profileId == 'me') {
                profileId = getUserId().toString();
            }
            const [createdEvents, attendingEvents, finishedEvents] = await Promise.all([
                //TODO do proper search
                fetchEvents({creatorId: Number(profileId), page: params.createdPage, size: params.size}, signal),
                fetchEvents({attendedBy: Number(profileId), past: false, page: params.attendingPage, size: params.size}, signal),
                fetchEvents({creatorId: Number(profileId), past: true, page: params.finishedPage, size: params.size}, signal),
            ]);
            return {
                created: paginate(buildProfileEvent(createdEvents), params.createdPage ?? 1, pageSize),
                attending: paginate(buildProfileEvent(attendingEvents), params.createdPage ?? 1, pageSize),
                finished: paginate(buildProfileEvent(finishedEvents), params.createdPage ?? 1, pageSize),
            }
        },
        placeholderData: keepPreviousData,
        enabled: scenario === "normal",
    });

    if (scenario === "loading") {
        return {
            created: paginate(getProfileEventsMock(scenario).created, params.createdPage ?? 1, pageSize),
            attending: paginate(getProfileEventsMock(scenario).attending, params.attendingPage ?? 1, pageSize),
            finished: paginate(getProfileEventsMock(scenario).finished, params.finishedPage ?? 1, pageSize),            
            isLoading: true,
            isError: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }
    if (scenario === "error") {
        return {
            created: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).created, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),
            attending: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).attending, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),
            finished: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).finished, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),
            isLoading: false,
            isError: true,
            refetch: () => undefined,
            isFetching: false,
        };
    }
    if (scenario === "empty") {
        return {
            created: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).created, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),
            attending: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).attending, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),
            finished: USE_MOCK_FALLBACK ? paginate(getProfileEventsMock(scenario).finished, params.createdPage ?? 1, pageSize) : paginate([], params.createdPage ?? 1, pageSize),            
            isLoading: false,
            isError: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }
    return {
        created: query.data?.created ?? paginate([], 1, 1),
        attending: query.data?.attending ?? paginate([], 1, 1),
        finished: query.data?.finished ?? paginate([], 1, 1),
        isLoading: query.isLoading,
        isError: query.isError,
        isFetching: query.isFetching,
        refetch: query.refetch
    }
};

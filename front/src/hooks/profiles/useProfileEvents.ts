import type { PageResult } from "@/types/pagination";
import type { ProfileEvent } from "@/types/event";
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
    const pageSize = params.size ?? 6;

    const query = useQuery({
        queryKey: ["profileEvents", profileId, params],
        queryFn: async ({ signal }) => {
            let resolvedId = profileId;
            if (!resolvedId || resolvedId === "me") {
                resolvedId = getUserId().toString();
            }
            const [createdEvents, attendingEvents, finishedEvents] = await Promise.all([
                //TODO do proper search
                fetchEvents({ creatorId: Number(resolvedId), page: params.createdPage, size: params.size }, signal),
                fetchEvents({ attendedBy: Number(resolvedId), past: false, page: params.attendingPage, size: params.size }, signal),
                fetchEvents({ creatorId: Number(resolvedId), past: true, page: params.finishedPage, size: params.size }, signal),
            ]);
            return {
                created: paginate(await buildProfileEvent(createdEvents, signal), params.createdPage ?? 1, pageSize),
                attending: paginate(await buildProfileEvent(attendingEvents, signal), params.createdPage ?? 1, pageSize),
                finished: paginate(await buildProfileEvent(finishedEvents, signal), params.createdPage ?? 1, pageSize),
            };
        },
        placeholderData: keepPreviousData,
    });
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

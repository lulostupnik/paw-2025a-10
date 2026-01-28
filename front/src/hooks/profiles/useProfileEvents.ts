import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";
import type { ProfileEvent } from "@/types/event";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { buildProfileEvent, fetchEvents } from "@/lib/api/events";
import { getUserId } from "@/lib/auth/auth";
import { getTodayIsoDate } from "@/lib/utils/date";

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

export const useProfileEvents = (profileId: string, params: ProfileEventsParams = {}): ProfileEventsResult => {
    const query = useQuery({
        queryKey: ["profileEvents", profileId, params],
        queryFn: async ({ signal }) => {
            let resolvedId = profileId;
            if (!resolvedId || resolvedId === "me") {
                resolvedId = getUserId().toString();
            }
            const today = getTodayIsoDate();
            const [createdEvents, attendingEvents, finishedEvents] = await Promise.all([
                fetchEvents({ creatorId: Number(resolvedId), page: params.createdPage, size: params.size }, signal),
                fetchEvents({ attendedBy: Number(resolvedId), afterDate: today, page: params.attendingPage, size: params.size }, signal),
                fetchEvents({ creatorId: Number(resolvedId), beforeDate: today, page: params.finishedPage, size: params.size }, signal),
            ]);
            return {
                created: mapPageList(createdEvents, await buildProfileEvent(createdEvents.content, signal)),
                attending: mapPageList(attendingEvents, await buildProfileEvent(attendingEvents.content, signal)),
                finished: mapPageList(finishedEvents, await buildProfileEvent(finishedEvents.content, signal))
            };
        },
        placeholderData: keepPreviousData,
    });
    return {
        created: query.data?.created ?? emptyPage(),
        attending: query.data?.attending ?? emptyPage(),
        finished: query.data?.finished ?? emptyPage(),
        isLoading: query.isLoading,
        isError: query.isError,
        isFetching: query.isFetching,
        refetch: query.refetch
    }
};

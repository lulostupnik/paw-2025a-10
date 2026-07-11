import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildEventDetail, getEventById } from "@/lib/api/events";
import type { EventDetail } from "@/types/event";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

interface EventDetailParams {
    eventId?: string;
}

export const useEventDetailData = ({ eventId }: EventDetailParams = {}) => {
    const queryClient = useQueryClient();
    const query = useQuery({
        queryKey: ["eventDetail", eventId],
        queryFn: async ({ signal }) => {
            if (!eventId) {
                throw new Error("missing-event-id");
            }
            const event = await getEventById(eventId, signal);
            return buildEventDetail(event, signal, queryClient);
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(eventId),
    });

    return {
        data: (query.data ?? null) as EventDetail | null,
        isLoading: query.isLoading,
        isError: query.isError,
        isFetching: query.isFetching,
    };
};

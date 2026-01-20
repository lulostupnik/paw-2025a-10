import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { buildEventDetail, getEventById } from "@/lib/api/events";
import { type EventDetail, type EventDetailScenario, getEventDetailMock } from "@/mocks/eventDetail.mock";

interface EventDetailParams {
    scenario?: EventDetailScenario;
    eventId?: string;
}

export const useEventDetailData = ({ scenario = "normal", eventId }: EventDetailParams = {}) => {
    const USE_MOCK_FALLBACK = true;
    const query = useQuery({
        queryKey: ["eventDetail", eventId],
        queryFn: async ({ signal }) => {
            if (!eventId) {
                throw new Error("missing-event-id");
            }
            const event = await getEventById(eventId, signal);
            return buildEventDetail(event, signal);
        },
        placeholderData: keepPreviousData,
        enabled: Boolean(eventId) && scenario === "normal",
    });

    const data = useMemo<EventDetail>(() => {
        if (scenario === "loading") {
            return getEventDetailMock("normal");
        }
        if (scenario === "empty") {
            return getEventDetailMock("empty");
        }
        if (scenario === "error") {
            return USE_MOCK_FALLBACK ? getEventDetailMock("normal") : getEventDetailMock("empty");
        }
        return query.data ?? (USE_MOCK_FALLBACK ? getEventDetailMock("normal") : getEventDetailMock("empty"));
    }, [scenario, query.data]);

    return {
        data,
        isLoading: scenario === "loading" || query.isLoading,
        isError: scenario === "error" || query.isError,
        isFetching: query.isFetching,
    };
};

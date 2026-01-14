import { useMemo } from "react";
import { type EventDetail, type EventDetailScenario, getEventDetailMock } from "@/mocks/eventDetail.mock";

interface EventDetailParams {
    scenario?: EventDetailScenario;
    eventId?: string;
}

export const useEventDetailData = ({ scenario = "normal", eventId }: EventDetailParams = {}) => {
    // TODO: GET /api/events/{eventId}
    // TODO: expected response shape: EventDetail
    const USE_MOCKS = true;
    const data = useMemo<EventDetail>(() => {
        if (USE_MOCKS) {
            return getEventDetailMock(scenario);
        }
        return getEventDetailMock(scenario);
    }, [scenario, eventId]);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

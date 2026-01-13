import { useMemo } from "react";
import { type EventDetail, type EventDetailScenario, getEventDetailMock } from "@/mocks/eventDetail.mock";

interface EventDetailParams {
    scenario?: EventDetailScenario;
}

export const useEventDetailData = ({ scenario = "normal" }: EventDetailParams = {}) => {
    // TODO: replace mock event detail with API fetch + caching + error handling.
    const data = useMemo<EventDetail>(() => getEventDetailMock(scenario), [scenario]);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

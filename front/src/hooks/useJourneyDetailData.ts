import { useMemo } from "react";
import { type JourneyDetail, type JourneyDetailScenario, getJourneyDetailMock } from "@/mocks/journeyDetail.mock";

interface JourneyDetailParams {
    scenario?: JourneyDetailScenario;
    journeyId?: string;
}

export const useJourneyDetailData = ({ scenario = "normal", journeyId }: JourneyDetailParams = {}) => {
    // TODO: GET /api/journeys/{journeyId}
    // TODO: expected response shape: JourneyDetail
    const USE_MOCKS = true;
    const data = useMemo<JourneyDetail>(() => {
        if (USE_MOCKS) {
            return getJourneyDetailMock(scenario);
        }
        return getJourneyDetailMock(scenario);
    }, [scenario, journeyId]);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

import { useMemo } from "react";
import { type JourneyDetail, type JourneyDetailScenario, getJourneyDetailMock } from "@/mocks/journeyDetail.mock";

interface JourneyDetailParams {
    scenario?: JourneyDetailScenario;
}

export const useJourneyDetailData = ({ scenario = "normal" }: JourneyDetailParams = {}) => {
    // TODO: replace mock journey detail with API fetch + caching + error handling.
    const data = useMemo<JourneyDetail>(() => getJourneyDetailMock(scenario), [scenario]);

    return {
        data,
        isLoading: scenario === "loading",
        isError: scenario === "error",
    };
};

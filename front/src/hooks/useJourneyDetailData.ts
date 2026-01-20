import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { buildJourneyDetail, getJourneyById } from "@/lib/api/journeys";
import { type JourneyDetail, type JourneyDetailScenario, getJourneyDetailMock } from "@/mocks/journeys.mock";

interface JourneyDetailParams {
    scenario?: JourneyDetailScenario;
    journeyId?: string;
}

export const useJourneyDetailData = ({ scenario = "normal", journeyId }: JourneyDetailParams = {}) => {
    const activeScenario = useMemo<JourneyDetailScenario>(() => scenario, [scenario]);
    const USE_MOCK_FALLBACK = true; // Set to false to disable fallback mocks.

    const query = useQuery({
        queryKey: ["journeyDetail", journeyId],
        queryFn: async ({ signal }) => {
            if (!journeyId) {
                throw new Error("missing-journey-id");
            }
            const journey = await getJourneyById(journeyId, signal);
            return buildJourneyDetail(journey, signal);
        },
        placeholderData: keepPreviousData,
        enabled: Boolean(journeyId) && activeScenario === "normal",
    });

    if (activeScenario === "loading") {
        return {
            data: getJourneyDetailMock("normal"),
            isLoading: true,
            isError: false,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }

    if (activeScenario === "error") {
        return {
            data: USE_MOCK_FALLBACK ? getJourneyDetailMock("normal") : getJourneyDetailMock("empty"),
            isLoading: false,
            isError: true,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }

    if (activeScenario === "empty") {
        return {
            data: getJourneyDetailMock("empty"),
            isLoading: false,
            isError: false,
            isNotFound: false,
            refetch: () => undefined,
            isFetching: false,
        };
    }

    const status = (query.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data: query.data ?? (USE_MOCK_FALLBACK ? getJourneyDetailMock("normal") : getJourneyDetailMock("empty")),
        isLoading: query.isLoading,
        isError: query.isError && !isNotFound,
        isNotFound,
        refetch: query.refetch,
        isFetching: query.isFetching,
    };
};

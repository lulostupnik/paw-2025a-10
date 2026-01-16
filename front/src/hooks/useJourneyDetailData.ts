import { useEffect, useMemo, useState } from "react";
import { buildJourneyDetail, getJourneyById } from "@/lib/api/journeys";
import { type JourneyDetail, type JourneyDetailScenario, getJourneyDetailMock } from "@/mocks/journeys.mock";

interface JourneyDetailParams {
    scenario?: JourneyDetailScenario;
    journeyId?: string;
}

export const useJourneyDetailData = ({ scenario = "normal", journeyId }: JourneyDetailParams = {}) => {
    const [data, setData] = useState<JourneyDetail>(() => getJourneyDetailMock("normal"));
    const [isLoading, setLoading] = useState(false);
    const [isError, setError] = useState(false);
    const [isNotFound, setNotFound] = useState(false);
    const activeScenario = useMemo<JourneyDetailScenario>(() => scenario, [scenario]);
    const USE_MOCK_FALLBACK = true; // Set to false to disable fallback mocks.

    useEffect(() => {
        if (activeScenario === "loading") {
            setLoading(true);
            setError(false);
            setNotFound(false);
            setData(getJourneyDetailMock("normal"));
            return;
        }

        if (activeScenario === "error") {
            setLoading(false);
            setError(true);
            setNotFound(false);
            if (USE_MOCK_FALLBACK) {
                setData(getJourneyDetailMock("normal"));
            }
            return;
        }

        if (activeScenario === "empty") {
            setLoading(false);
            setError(false);
            setNotFound(false);
            setData(getJourneyDetailMock("empty"));
            return;
        }

        if (!journeyId) {
            setError(true);
            setNotFound(false);
            setLoading(false);
            return;
        }

        const controller = new AbortController();
        setLoading(true);
        setError(false);
        setNotFound(false);

        getJourneyById(journeyId, controller.signal)
            .then((journey) => buildJourneyDetail(journey, controller.signal))
            .then((detail) => {
                if (controller.signal.aborted) {
                    return;
                }
                setData(detail);
            })
            .catch((err: { response?: { status?: number } }) => {
                if (controller.signal.aborted) {
                    return;
                }
                const status = err?.response?.status;
                if (status === 404) {
                    setNotFound(true);
                    setError(false);
                } else {
                    setError(true);
                }
                if (USE_MOCK_FALLBACK) {
                    setData(getJourneyDetailMock("normal"));
                }
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            });

        return () => controller.abort();
    }, [activeScenario, journeyId, USE_MOCK_FALLBACK]);

    return {
        data,
        isLoading,
        isError,
        isNotFound,
    };
};

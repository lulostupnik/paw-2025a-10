import { useQuery } from "@tanstack/react-query";
import { getMyJourney } from "@/lib/api/journeys";
import { isLoggedIn } from "@/lib/auth/auth";
import type { JourneySummary } from "@/types/journey";

interface UseMyJourneyResult {
    journey: JourneySummary | null;
    hasJourney: boolean;
    isLoading: boolean;
    isError: boolean;
    refetch: () => void;
}

export const useMyJourney = (): UseMyJourneyResult => {
    const enabled = isLoggedIn();
    const query = useQuery({
        queryKey: ["myJourney"],
        queryFn: ({ signal }) => getMyJourney(signal),
        enabled,
    });

    const journey = query.data ?? null;

    return {
        journey,
        hasJourney: Boolean(journey),
        isLoading: enabled ? query.isLoading : false,
        isError: enabled ? query.isError : false,
        refetch: query.refetch,
    };
};

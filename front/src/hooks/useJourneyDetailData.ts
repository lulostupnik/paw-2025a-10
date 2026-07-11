import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildJourneyDetail, getJourneyById } from "@/lib/api/journeys";
import type { JourneyDetail } from "@/types/journey";
import { DETAIL_QUERY_OPTIONS } from "@/lib/utils/queryDefaults";

interface JourneyDetailParams {
    journeyId?: string;
}

export const useJourneyDetailData = ({ journeyId }: JourneyDetailParams = {}) => {
    const queryClient = useQueryClient();
    const query = useQuery({
        queryKey: ["journeyDetail", journeyId],
        queryFn: async ({ signal }) => {
            if (!journeyId) {
                throw new Error("missing-journey-id");
            }
            const journey = await getJourneyById(journeyId, signal);
            return buildJourneyDetail(journey, signal, queryClient);
        },
        placeholderData: keepPreviousData,
        ...DETAIL_QUERY_OPTIONS,
        enabled: Boolean(journeyId),
    });

    const status = (query.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data: (query.data ?? null) as JourneyDetail | null,
        isLoading: query.isLoading,
        isError: query.isError && !isNotFound,
        isNotFound,
        refetch: query.refetch,
        isFetching: query.isFetching,
    };
};

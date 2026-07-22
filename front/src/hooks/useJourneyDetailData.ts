import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildJourneyCreator, buildJourneyDestination, getJourneyById } from "@/lib/api/journeys";
import type { JourneyCreator, JourneyDetail } from "@/types/journey";

interface JourneyDetailParams {
    journeyId?: string;
}

const parseIdFromUrl = (url?: string | null): number | null => {
    if (!url) {
        return null;
    }
    const match = url.match(/(\d+)\/?$/);
    return match ? Number(match[1]) : null;
};

export const useJourneyDetailData = ({ journeyId }: JourneyDetailParams = {}) => {
    const queryClient = useQueryClient();

    const journeyQuery = useQuery({
        queryKey: ["journeyDetail", journeyId],
        queryFn: ({ signal }) => {
            if (!journeyId) {
                throw new Error("missing-journey-id");
            }
            return getJourneyById(journeyId, signal);
        },
        placeholderData: keepPreviousData,
        enabled: Boolean(journeyId),
    });

    const journey = journeyQuery.data ?? null;
    const userUrl = journey?.links?.userUrl ?? null;
    const destinationUrl = journey?.links?.destinationUniversityUrl ?? null;

    const creatorQuery = useQuery({
        queryKey: ["journeyCreator", journeyId, userUrl],
        queryFn: ({ signal }) => buildJourneyCreator(journey!, signal, queryClient),
        enabled: Boolean(journey && userUrl),
        placeholderData: keepPreviousData,
    });

    const destinationQuery = useQuery({
        queryKey: ["journeyDestination", journeyId, destinationUrl],
        queryFn: ({ signal }) => buildJourneyDestination(journey!, signal, queryClient),
        enabled: Boolean(journey && destinationUrl),
        placeholderData: keepPreviousData,
    });

    const fallbackCreator: JourneyCreator = {
        id: parseIdFromUrl(userUrl) ?? 0,
        firstname: "",
        lastname: "",
        username: "",
        profilePictureUrl: journey?.profilePictureUrl ?? null,
        university: null,
        career: null,
    };

    const data: JourneyDetail | null = journey
        ? {
              id: journey.id,
              description: journey.description,
              startDate: journey.startDate,
              endDate: journey.endDate,
              links: journey.links ?? null,
              destinationUniversity: {
                  name: journey.destinationUniversityName ?? destinationQuery.data?.name ?? "",
                  city: destinationQuery.data?.city ?? "",
              },
              user: creatorQuery.data ?? fallbackCreator,
              interests: [],
              events: [],
              tips: [],
              comments: [],
          }
        : null;

    const status = (journeyQuery.error as { response?: { status?: number } } | undefined)?.response?.status;
    const isNotFound = status === 404;

    return {
        data,
        isLoading: journeyQuery.isLoading,
        isError: journeyQuery.isError && !isNotFound,
        isNotFound,
        refetch: journeyQuery.refetch,
        isFetching: journeyQuery.isFetching,
        creatorLoading: creatorQuery.isLoading,
        creatorError: creatorQuery.isError,
        creatorReady: Boolean(creatorQuery.data),
        destinationLoading: destinationQuery.isLoading,
        destinationError: destinationQuery.isError,
        destinationReady: Boolean(destinationQuery.data),
    };
};

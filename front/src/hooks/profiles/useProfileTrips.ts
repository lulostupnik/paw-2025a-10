import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { ProfileJourney, JourneySummary } from "@/types/journey";
import { getProfileDetail } from "@/lib/api/users";
import { getJourneyById, resolveJourneySummary } from "@/lib/api/journeys";
import { getUserId } from "@/lib/auth/auth";
import { useQueryClient } from "@tanstack/react-query";

export interface ProfileTripsParams {
    page?: number;
    size?: number;
}

interface ProfileTripsResult {
    data?: ProfileJourney;
    isLoading: boolean;
    isError: boolean;
    error: string | null;
    refetch: () => void;
}

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

const buildJourneyTitle = (journey: JourneySummary) =>
    journey.university ?? journey.city ?? `Journey #${journey.id}`;

export const useProfileTrips = (profileId: string, params: ProfileTripsParams = {}): ProfileTripsResult => {
    const queryClient = useQueryClient();
    const query = useQuery({
        queryKey: ["profileTrips", profileId, params],
        queryFn: async ({ signal }) => {
            let resolvedId = profileId;
            if (resolvedId === "me") {
                const userId = getUserId();
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                resolvedId = userId.toString();
            }
            const user = await getProfileDetail(resolvedId, signal);
            const journeyId = parseIdFromUrl(user.links?.journeyUrl);
            if (!journeyId) {
                return undefined;
            }
            const journey = await getJourneyById(journeyId, signal);
            const resolved = await resolveJourneySummary(journey, signal, queryClient);
            const item: ProfileJourney =
                {
                    id: resolved.id,
                    title: buildJourneyTitle(resolved),
                    deleted: false,
                };
            return item;
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data,
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.isError ? "Failed to load profile journeys" : null,
        refetch: query.refetch,
    };
};

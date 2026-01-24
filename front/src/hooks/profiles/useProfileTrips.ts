import { useMemo } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { PageResult } from "@/types/pagination";
import type { ProfileJourney, JourneySummary } from "@/types/journey";
import { getProfileDetail } from "@/lib/api/users";
import { getJourneyById, resolveJourneySummary } from "@/lib/api/journeys";
import { getUserId } from "@/lib/auth/auth";

export interface ProfileTripsParams {
    page?: number;
    size?: number;
}

interface ProfileTripsResult {
    data: PageResult<ProfileJourney>;
    isLoading: boolean;
    isError: boolean;
    error: string | null;
    refetch: () => void;
}

const paginate = <T,>(items: T[], page: number, pageSize: number): PageResult<T> => {
    const totalItems = items.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
    const safePage = Math.min(Math.max(page, 1), totalPages);
    const start = (safePage - 1) * pageSize;
    return {
        content: items.slice(start, start + pageSize),
        totalPages,
        currentPage: safePage,
        pageSize,
        totalItems,
    };
};

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
    const query = useQuery({
        queryKey: ["profileTrips", profileId, params],
        queryFn: async ({ signal }) => {
            const resolvedId = profileId === "me" ? String(getUserId()) : profileId;
            const user = await getProfileDetail(resolvedId, signal);
            const journeyId = parseIdFromUrl(user.journeyUrl);
            if (!journeyId) {
                return paginate([], params.page ?? 1, params.size ?? 6);
            }
            const journey = await getJourneyById(journeyId, signal);
            const resolved = await resolveJourneySummary(journey, signal);
            const items: ProfileJourney[] = [
                {
                    id: resolved.id,
                    title: buildJourneyTitle(resolved),
                    deleted: false,
                },
            ];
            return paginate(items, params.page ?? 1, params.size ?? 6);
        },
        placeholderData: keepPreviousData,
    });

    return {
        data: query.data ?? paginate([], params.page ?? 1, params.size ?? 6),
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.isError ? "Failed to load profile journeys" : null,
        refetch: query.refetch,
    };
};

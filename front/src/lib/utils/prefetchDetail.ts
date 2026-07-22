import type { QueryClient } from "@tanstack/react-query";
import { getEventById } from "@/lib/api/events";
import { getJourneyById } from "@/lib/api/journeys";
import { getProfileDetail } from "@/lib/api/users";

const normalizeDetailId = (id: string | number) => String(id);

export const prefetchProfileDetail = (queryClient: QueryClient, profileId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["profileDetail", normalizeDetailId(profileId)],
        queryFn: ({ signal }) => getProfileDetail(profileId, signal),
    });

export const prefetchJourneyDetail = (queryClient: QueryClient, journeyId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["journeyDetail", normalizeDetailId(journeyId)],
        queryFn: ({ signal }) => getJourneyById(journeyId, signal),
    });

export const prefetchEventDetail = (queryClient: QueryClient, eventId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["eventDetail", normalizeDetailId(eventId)],
        queryFn: ({ signal }) => getEventById(eventId, signal),
    });

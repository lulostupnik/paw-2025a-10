import type { QueryClient } from "@tanstack/react-query";
import { buildEventDetail, getEventById } from "@/lib/api/events";
import { buildJourneyDetail, getJourneyById } from "@/lib/api/journeys";
import { buildProfileDetail, getProfileDetail } from "@/lib/api/users";

const normalizeDetailId = (id: string | number) => String(id);

export const prefetchProfileDetail = (queryClient: QueryClient, profileId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["profileDetail", normalizeDetailId(profileId)],
        queryFn: async ({ signal }) => {
            const profile = await getProfileDetail(profileId, signal);
            return buildProfileDetail(profile, signal);
        },
    });

export const prefetchJourneyDetail = (queryClient: QueryClient, journeyId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["journeyDetail", normalizeDetailId(journeyId)],
        queryFn: async ({ signal }) => {
            const journey = await getJourneyById(journeyId, signal);
            return buildJourneyDetail(journey, signal);
        },
    });

export const prefetchEventDetail = (queryClient: QueryClient, eventId: string | number) =>
    void queryClient.prefetchQuery({
        queryKey: ["eventDetail", normalizeDetailId(eventId)],
        queryFn: async ({ signal }) => {
            const event = await getEventById(eventId, signal);
            return buildEventDetail(event, signal);
        },
    });

import type { QueryClient } from "@tanstack/react-query";

export const invalidateJourneyDetailQueries = async (queryClient: QueryClient, journeyId: string | number) => {
    const id = String(journeyId);
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["journeyDetail", id] }),
        queryClient.invalidateQueries({ queryKey: ["journeyCreator", id] }),
        queryClient.invalidateQueries({ queryKey: ["journeyDestination", id] }),
    ]);
};

export const invalidateEventDetailQueries = async (queryClient: QueryClient, eventId: string | number) => {
    const id = String(eventId);
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["eventDetail", id] }),
        queryClient.invalidateQueries({ queryKey: ["eventCreator", id] }),
        queryClient.invalidateQueries({ queryKey: ["eventCity", id] }),
        queryClient.invalidateQueries({ queryKey: ["eventRatings", id] }),
    ]);
};

export const invalidateJourneyListQueries = async (queryClient: QueryClient) => {
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["journeys"] }),
        queryClient.invalidateQueries({ queryKey: ["profileTrips"] }),
    ]);
};

export const invalidateEventListQueries = async (queryClient: QueryClient) => {
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["events"] }),
        queryClient.invalidateQueries({ queryKey: ["profileEvents"] }),
        queryClient.invalidateQueries({ queryKey: ["adminEvents"] }),
    ]);
};

const ADMIN_ENTITY_QUERY_KEYS = {
    city: { list: "adminCities", detail: "adminCityDetail" },
    career: { list: "adminCareers", detail: "adminCareerDetail" },
    interest: { list: "adminInterests", detail: "adminInterestDetail" },
    university: { list: "adminUniversities", detail: "adminUniversityDetail" },
} as const;

export type AdminEntity = keyof typeof ADMIN_ENTITY_QUERY_KEYS;

export const invalidateAdminEntityQueries = async (
    queryClient: QueryClient,
    entity: AdminEntity,
    entityId?: string | number
) => {
    const keys = ADMIN_ENTITY_QUERY_KEYS[entity];
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: [keys.list] }),
        ...(entityId != null
            ? [queryClient.invalidateQueries({ queryKey: [keys.detail, String(entityId)] })]
            : []),
    ]);
};

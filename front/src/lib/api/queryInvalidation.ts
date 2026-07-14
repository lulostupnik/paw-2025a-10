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

export const invalidateAdminEntityDetailQueries = async (
    queryClient: QueryClient,
    entity: "city" | "career" | "interest" | "university",
    entityId: string | number
) => {
    const id = String(entityId);
    const detailKeys = {
        city: ["adminCityDetail", id],
        career: ["adminCareerDetail", id],
        interest: ["adminInterestDetail", id],
        university: ["adminUniversityDetail", id],
    } as const;

    await queryClient.invalidateQueries({ queryKey: detailKeys[entity] });
};

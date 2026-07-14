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

const ADMIN_ENTITY_QUERY_KEYS = {
    city: { list: "adminCities", detail: "adminCityDetail" },
    career: { list: "adminCareers", detail: "adminCareerDetail" },
    interest: { list: "adminInterests", detail: "adminInterestDetail" },
    university: { list: "adminUniversities", detail: "adminUniversityDetail" },
} as const;

export type AdminEntity = keyof typeof ADMIN_ENTITY_QUERY_KEYS;

// Toda mutación de un catálogo (alta/edición/baja) invalida su listado; el
// detalle sólo cuando la mutación apunta a una entidad concreta.
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

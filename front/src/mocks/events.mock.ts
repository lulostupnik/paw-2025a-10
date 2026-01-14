import type { EventSummary } from "@/lib/api/events";

export type EventListScenario = "normal" | "empty" | "error" | "loading";

const EVENT_LIST: EventSummary[] = [
    {
        id: 301,
        title: "Feria de Intercambios 2025",
        description: "Una jornada para conocer oportunidades internacionales, becas y experiencias de estudiantes.",
        date: "2025-05-22",
        time: "15:00",
        address: "Av. Corrientes 1234",
        attendeesLimit: 60,
        attendeesCount: 45,
        isFull: false,
        isFuture: true,
        imageUrl: null,
    },
    {
        id: 302,
        title: "Taller de Postulacion",
        description: "Prepara tu perfil para aplicar a programas internacionales con expertos invitados.",
        date: "2025-06-03",
        time: "18:00",
        address: "Auditorio Central",
        attendeesLimit: 40,
        attendeesCount: 40,
        isFull: true,
        isFuture: true,
        imageUrl: null,
    },
];

export const getEventsMock = (scenario: EventListScenario): EventSummary[] => {
    if (scenario === "empty") {
        return [];
    }
    return EVENT_LIST;
};

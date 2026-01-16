import type { JourneyDetail, JourneySummary } from "@/types/journey";

export type JourneyListScenario = "normal" | "empty" | "error" | "loading";
export type JourneyDetailScenario = "normal" | "empty" | "error" | "loading";

const NORMAL_JOURNEYS: JourneySummary[] = [
    {
        id: 101,
        city: "Lisboa",
        country: "Portugal",
        university: "Universidad de Lisboa",
        startDate: "2025-02-10",
        endDate: "2025-06-15",
        description: "Una experiencia increible explorando nuevas culturas y compartiendo con estudiantes de todo el mundo.",
        userName: "camilar",
        profilePictureUrl: null,
    },
    {
        id: 102,
        city: "Berlin",
        country: "Alemania",
        university: "Humboldt Universitat",
        startDate: "2025-03-05",
        endDate: "2025-07-20",
        description: "Intercambio academico enfocado en tecnologia y emprendimiento.",
        userName: "lucasv",
        profilePictureUrl: null,
    },
    {
        id: 103,
        city: "Buenos Aires",
        country: "Argentina",
        university: "Universidad de Buenos Aires",
        startDate: "2025-08-01",
        endDate: "2025-12-10",
        description: "Viaje orientado a estudios de negocios y marketing internacional.",
        userName: "mariap",
        profilePictureUrl: null,
    },
    {
        id: 104,
        city: "Toronto",
        country: "Canada",
        university: "University of Toronto",
        startDate: "2025-01-15",
        endDate: "2025-05-30",
        description: "Experiencia de intercambio con enfoque en ciencias de datos.",
        userName: "julianb",
        profilePictureUrl: null,
    },
];

const NORMAL_DETAIL: JourneyDetail = {
    id: 101,
    description:
        "Una experiencia increible explorando nuevas culturas y compartiendo con estudiantes de todo el mundo.",
    startDate: "2025-02-10",
    endDate: "2025-06-15",
    destinationUniversity: {
        name: "Universidad de Lisboa",
        city: "Lisboa",
    },
    user: {
        id: 11,
        firstname: "Camila",
        lastname: "Rossi",
        username: "camilar",
        profilePictureUrl: null,
        university: { name: "Universidad Nacional" },
        career: { name: "Arquitectura" },
    },
    interests: ["Cultura", "Arquitectura", "Idiomas", "Viajes"],
    events: [
        {
            id: 501,
            title: "Welcome Exchange Night",
            description: "Encuentro de bienvenida para nuevos estudiantes.",
            city: "Lisboa",
            date: "2025-03-04",
            time: "19:30",
        },
        {
            id: 502,
            title: "City Tour",
            description: "Recorrido cultural por la ciudad.",
            city: "Lisboa",
            date: "2025-03-10",
            time: "09:00",
        },
    ],
    comments: [
        {
            id: 701,
            user: { username: "marcos" },
            message: "Gracias por compartir tu experiencia.",
            dateTime: "2025-03-01T10:15",
        },
        {
            id: 702,
            user: { username: "laura" },
            message: "Que universidad recomendarias para intercambio?",
            dateTime: "2025-03-02T12:30",
        },
    ],
};

const EMPTY_DETAIL: JourneyDetail = {
    ...NORMAL_DETAIL,
    interests: [],
    events: [],
    comments: [],
};

export const getJourneysMock = (scenario: JourneyListScenario): JourneySummary[] => {
    if (scenario === "empty") {
        return [];
    }
    return NORMAL_JOURNEYS;
};

export const getJourneyDetailMock = (scenario: JourneyDetailScenario): JourneyDetail =>
    scenario === "empty" ? EMPTY_DETAIL : NORMAL_DETAIL;

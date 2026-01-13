export type JourneyDetailScenario = "normal" | "empty" | "error" | "loading";

export interface JourneyCreator {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    profilePictureUrl?: string | null;
    university?: { name: string } | null;
    career?: { name: string } | null;
}

export interface JourneyDetail {
    id: number;
    description: string;
    startDate: string;
    endDate: string;
    destinationUniversity: {
        name: string;
        city: string;
    };
    user: JourneyCreator;
    interests: string[];
    events: JourneyEvent[];
    comments: JourneyComment[];
}

export interface JourneyEvent {
    id: number;
    title: string;
    description: string;
    city: string;
    date: string;
    time?: string | null;
    flyerImageUrl?: string | null;
}

export interface JourneyComment {
    id: number;
    user: {
        username: string;
    };
    message: string;
    dateTime: string;
}

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

export const getJourneyDetailMock = (scenario: JourneyDetailScenario): JourneyDetail =>
    scenario === "empty" ? EMPTY_DETAIL : NORMAL_DETAIL;

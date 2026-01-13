export type EventDetailScenario = "normal" | "empty" | "error" | "loading";

export interface EventCreator {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    profilePictureUrl?: string | null;
    university?: { name: string } | null;
    career?: { name: string } | null;
}

export interface EventDetail {
    id: number;
    title: string;
    description: string;
    city: { name: string };
    date: string;
    time?: string | null;
    address?: string | null;
    flyerImageUrl?: string | null;
    attendeesCount: number;
    attendeesLimit?: number | null;
    isFuture: boolean;
    user: EventCreator;
    comments: EventComment[];
    attendees: EventAttendee[];
    ratings: EventRating[];
    averageRating?: number | null;
}

export interface EventComment {
    id: number;
    user: { username: string };
    message: string;
    dateTime: string;
}

export interface EventAttendee {
    id: number;
    firstname: string;
    lastname: string;
    email: string;
    profilePictureUrl?: string | null;
}

export interface EventRating {
    id: number;
    user: { username: string };
    rating: number;
    dateTime: string;
}

const NORMAL_DETAIL: EventDetail = {
    id: 301,
    title: "Feria de Intercambios 2025",
    description:
        "Una jornada para conocer oportunidades internacionales, becas y experiencias de estudiantes.",
    city: { name: "Buenos Aires" },
    date: "2025-05-22",
    time: "15:00",
    address: "Av. Corrientes 1234",
    flyerImageUrl: null,
    attendeesCount: 45,
    attendeesLimit: 60,
    isFuture: true,
    user: {
        id: 21,
        firstname: "Sofia",
        lastname: "Luna",
        username: "sluna",
        profilePictureUrl: null,
        university: { name: "Universidad de Buenos Aires" },
        career: { name: "Comunicacion" },
    },
    comments: [
        {
            id: 801,
            user: { username: "martin" },
            message: "Ya me inscribi, gracias!",
            dateTime: "2025-05-01T09:45",
        },
        {
            id: 802,
            user: { username: "lucia" },
            message: "Habra streaming disponible?",
            dateTime: "2025-05-01T10:20",
        },
    ],
    attendees: [
        {
            id: 901,
            firstname: "Ana",
            lastname: "Perez",
            email: "ana@example.com",
        },
        {
            id: 902,
            firstname: "Jorge",
            lastname: "Diaz",
            email: "jorge@example.com",
        },
    ],
    ratings: [
        {
            id: 1001,
            user: { username: "maria" },
            rating: 4.5,
            dateTime: "2025-03-01T11:10",
        },
        {
            id: 1002,
            user: { username: "fede" },
            rating: 5,
            dateTime: "2025-03-02T14:30",
        },
    ],
    averageRating: 4.7,
};

const EMPTY_DETAIL: EventDetail = {
    ...NORMAL_DETAIL,
    comments: [],
    attendees: [],
    ratings: [],
    averageRating: null,
};

export const getEventDetailMock = (scenario: EventDetailScenario): EventDetail =>
    scenario === "empty" ? EMPTY_DETAIL : NORMAL_DETAIL;

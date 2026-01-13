export type AdminDashboardScenario = "normal" | "empty" | "error" | "loading";
export type AdminDashboardTab =
    | "journeys"
    | "users"
    | "events"
    | "universities"
    | "interests"
    | "cities"
    | "careers"
    | "reports";

export interface AdminJourney {
    id: number;
    user: {
        username: string;
    };
    destinationUniversity: {
        name: string;
        city: string;
    };
    startDate: string;
    endDate: string;
}

export interface AdminUser {
    id: number;
    firstname: string;
    email: string;
    university: string;
    blocked: boolean;
}

export interface AdminEvent {
    id: number;
    title: string;
    user: {
        username: string;
    };
    city: string;
    date: string;
    attendeesCount: number;
    attendeesLimit?: number | null;
}

export interface AdminUniversity {
    id: number;
    name: string;
    abbreviation: string;
    city: string;
}

export interface AdminInterest {
    id: number;
    name: string;
}

export interface AdminCity {
    id: number;
    name: string;
    country: string;
}

export interface AdminCareer {
    id: number;
    name: string;
}

export interface AdminReport {
    id: number;
    reportedUser: {
        username: string;
    };
    reportingUser: {
        username: string;
    };
    description: string;
    reason: string;
    status: string;
}

export interface AdminDashboardMockData {
    journeys: AdminJourney[];
    users: AdminUser[];
    events: AdminEvent[];
    universities: AdminUniversity[];
    interests: AdminInterest[];
    cities: AdminCity[];
    careers: AdminCareer[];
    reports: AdminReport[];
}

const NORMAL_DATA: AdminDashboardMockData = {
    journeys: [
        {
            id: 101,
            user: { username: "lmartinez" },
            destinationUniversity: { name: "Universidad de Madrid", city: "Madrid" },
            startDate: "2025-03-12",
            endDate: "2025-07-12",
        },
        {
            id: 102,
            user: { username: "sdelgado" },
            destinationUniversity: { name: "Universite de Lyon", city: "Lyon" },
            startDate: "2025-04-01",
            endDate: "2025-09-01",
        },
        {
            id: 103,
            user: { username: "mfernandez" },
            destinationUniversity: { name: "University of Toronto", city: "Toronto" },
            startDate: "2025-02-05",
            endDate: "2025-05-30",
        },
    ],
    users: [
        {
            id: 201,
            firstname: "Lucia",
            email: "lucia@example.com",
            university: "Universidad de Buenos Aires",
            blocked: false,
        },
        {
            id: 202,
            firstname: "Mateo",
            email: "mateo@example.com",
            university: "Universidad Nacional",
            blocked: true,
        },
        {
            id: 203,
            firstname: "Valeria",
            email: "valeria@example.com",
            university: "Universidad de Montevideo",
            blocked: false,
        },
    ],
    events: [
        {
            id: 301,
            title: "Session Info Erasmus",
            user: { username: "admin" },
            city: "Barcelona",
            date: "2025-05-22",
            attendeesCount: 48,
            attendeesLimit: 60,
        },
        {
            id: 302,
            title: "Meetup Becas 2025",
            user: { username: "mlinares" },
            city: "Cordoba",
            date: "2025-06-10",
            attendeesCount: 120,
            attendeesLimit: null,
        },
        {
            id: 303,
            title: "Info Session Exchange",
            user: { username: "admin" },
            city: "Rosario",
            date: "2025-07-01",
            attendeesCount: 12,
            attendeesLimit: 40,
        },
    ],
    universities: [
        {
            id: 401,
            name: "University of Kyoto",
            abbreviation: "UKY",
            city: "Kyoto",
        },
        {
            id: 402,
            name: "Universidad de Chile",
            abbreviation: "UCH",
            city: "Santiago",
        },
    ],
    interests: [
        { id: 501, name: "Arquitectura" },
        { id: 502, name: "Tecnologia" },
        { id: 503, name: "Idiomas" },
    ],
    cities: [
        { id: 601, name: "Lisboa", country: "Portugal" },
        { id: 602, name: "Berlin", country: "Alemania" },
    ],
    careers: [
        { id: 701, name: "Ingenieria" },
        { id: 702, name: "Medicina" },
    ],
    reports: [
        {
            id: 801,
            reportedUser: { username: "tlopez" },
            reportingUser: { username: "nramirez" },
            description: "Contenido inapropiado en el evento.",
            reason: "INAPPROPRIATE_CONTENT",
            status: "PENDING",
        },
        {
            id: 802,
            reportedUser: { username: "jrios" },
            reportingUser: { username: "admin" },
            description: "Spam en comentarios.",
            reason: "SPAM",
            status: "UNDER_REVIEW",
        },
        {
            id: 803,
            reportedUser: { username: "mvera" },
            reportingUser: { username: "admin" },
            description: "Discurso de odio.",
            reason: "HATE_SPEECH",
            status: "RESOLVED",
        },
    ],
};

const EMPTY_DATA: AdminDashboardMockData = {
    journeys: [],
    users: [],
    events: [],
    universities: [],
    interests: [],
    cities: [],
    careers: [],
    reports: [],
};

export const getAdminDashboardMock = (scenario: AdminDashboardScenario): AdminDashboardMockData => {
    if (scenario === "empty") {
        return EMPTY_DATA;
    }

    return NORMAL_DATA;
};

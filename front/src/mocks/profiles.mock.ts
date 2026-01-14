import type { ProfileDetail, ProfileEventsPayload, ProfileInterest, ProfileSummary } from "@/types/profile";
import type { ProfileEvent } from "@/types/event";
import type { ProfileJourney } from "@/types/journey";

export type ProfileScenario = "normal" | "empty" | "error" | "loading";

const PROFILE_JOURNEY: ProfileJourney = {
    id: 702,
    title: "Intercambio en Valencia",
    deleted: false,
};

const PROFILE_INTERESTS: ProfileInterest[] = [
    { id: 1, name: "Historia" },
    { id: 2, name: "Tecnologia" },
    { id: 3, name: "Artes" },
    { id: 4, name: "Fotografia" },
    { id: 5, name: "Idiomas" },
    { id: 6, name: "Viajes" },
];

const PROFILE_UNIVERSITIES = [
    { id: 1, name: "Universidad de Buenos Aires" },
    { id: 2, name: "Universidad Nacional de La Plata" },
    { id: 3, name: "Universidad Nacional de Cordoba" },
    { id: 4, name: "Universidad Nacional de Rosario" },
];

const PROFILE_CAREERS = [
    { id: 1, name: "Relaciones Internacionales" },
    { id: 2, name: "Ingenieria" },
    { id: 3, name: "Comunicacion" },
    { id: 4, name: "Diseno" },
];

const CREATED_EVENTS: ProfileEvent[] = [
    {
        id: 301,
        title: "Feria de Intercambios 2025",
        description: "Una jornada para conocer oportunidades internacionales, becas y experiencias de estudiantes.",
        date: "2025-05-22",
        city: { name: "Buenos Aires" },
        flyerImageUrl: null,
        attendeesLimit: 60,
        attendeesCount: 45,
        isFull: false,
        user: {
            id: 1,
            username: "username",
            firstname: "Valentina",
            lastname: "Rios",
        },
    },
    {
        id: 302,
        title: "Taller de Postulacion",
        description: "Prepara tu perfil para aplicar a programas internacionales con expertos invitados.",
        date: "2025-06-03",
        city: { name: "Cordoba" },
        flyerImageUrl: null,
        attendeesLimit: 40,
        attendeesCount: 40,
        isFull: true,
        user: {
            id: 1,
            username: "username",
            firstname: "Valentina",
            lastname: "Rios",
        },
    },
];

const ATTENDING_EVENTS: ProfileEvent[] = [
    {
        id: 401,
        title: "Meetup Erasmus",
        description: "Conoce a estudiantes que participan en programas Erasmus.",
        date: "2025-05-30",
        city: { name: "Madrid" },
        flyerImageUrl: null,
        attendeesLimit: 80,
        attendeesCount: 76,
        isFull: false,
        user: {
            id: 21,
            username: "sluna",
            firstname: "Sofia",
            lastname: "Luna",
        },
    },
];

const FINISHED_EVENTS: ProfileEvent[] = [
    {
        id: 501,
        title: "Charla Becas 2024",
        description: "Resumen de oportunidades y tips para becas internacionales.",
        date: "2024-11-10",
        city: { name: "Rosario" },
        flyerImageUrl: null,
        attendeesLimit: 50,
        attendeesCount: 50,
        isFull: true,
        user: {
            id: 34,
            username: "marco",
            firstname: "Marco",
            lastname: "Suarez",
        },
    },
];

const MY_PROFILE: ProfileDetail = {
    id: 1,
    firstname: "Valentina",
    lastname: "Rios",
    username: "username",
    profilePictureUrl: null,
    email: "valentina.rios@example.com",
    university: { name: "Universidad de Buenos Aires" },
    career: { name: "Relaciones Internacionales" },
    isMine: true,
    journey: PROFILE_JOURNEY,
    interests: PROFILE_INTERESTS,
    ratingStats: {
        averageCreatedEventsRating: 4.6,
        averageAttendedEventsRating: 4.2,
    },
};

const OTHER_PROFILE: ProfileDetail = {
    id: 24,
    firstname: "Lucia",
    lastname: "Morales",
    username: "lumo",
    profilePictureUrl: null,
    email: "lucia.morales@example.com",
    university: { name: "Universidad Nacional de La Plata" },
    career: { name: "Ingenieria" },
    isMine: false,
    journey: {
        id: 910,
        title: "Viaje a Lisboa",
        deleted: false,
    },
    interests: PROFILE_INTERESTS.slice(0, 3),
    ratingStats: {
        averageCreatedEventsRating: 4.8,
        averageAttendedEventsRating: null,
    },
};

const EMPTY_PROFILE: ProfileDetail = {
    ...OTHER_PROFILE,
    interests: [],
    journey: null,
    ratingStats: {
        averageCreatedEventsRating: null,
        averageAttendedEventsRating: null,
    },
};

const EMPTY_EVENTS: ProfileEventsPayload = {
    created: [],
    attending: [],
    finished: [],
};

const NORMAL_EVENTS: ProfileEventsPayload = {
    created: CREATED_EVENTS,
    attending: ATTENDING_EVENTS,
    finished: FINISHED_EVENTS,
};

const EMPTY_INTERESTS: ProfileInterest[] = [];

export const getProfileDetailMock = (profileId: string, scenario: ProfileScenario): ProfileDetail => {
    if (scenario === "empty") {
        return profileId === "me" || profileId === "1" ? { ...EMPTY_PROFILE, isMine: true } : EMPTY_PROFILE;
    }
    if (profileId === "me" || profileId === "1") {
        return MY_PROFILE;
    }
    return OTHER_PROFILE;
};

export const getProfileEventsMock = (scenario: ProfileScenario): ProfileEventsPayload =>
    scenario === "empty" ? EMPTY_EVENTS : NORMAL_EVENTS;

export const getProfileInterestsMock = (scenario: ProfileScenario): ProfileInterest[] =>
    scenario === "empty" ? EMPTY_INTERESTS : PROFILE_INTERESTS;

export const getProfilesListMock = (scenario: ProfileScenario): ProfileSummary[] => {
    if (scenario === "empty") {
        return [];
    }
    const toSummary = (profile: ProfileDetail): ProfileSummary => ({
        id: profile.id,
        firstname: profile.firstname,
        lastname: profile.lastname,
        username: profile.username,
        profilePictureUrl: profile.profilePictureUrl,
        university: profile.university ?? null,
        career: profile.career ?? null,
    });
    return [MY_PROFILE, OTHER_PROFILE].map(toSummary);
};

export const getProfileUniversitiesMock = () => PROFILE_UNIVERSITIES;

export const getProfileCareersMock = () => PROFILE_CAREERS;

export type AdminDetailScenario = "normal" | "empty" | "error" | "loading";

export interface AdminUserDetail {
    id: number;
    firstname: string;
    lastname: string;
    username: string;
    email: string;
    university?: {
        name: string;
    } | null;
    career?: {
        name: string;
    } | null;
    locale: string;
    profilePictureUrl?: string | null;
    blocked: boolean;
}

export interface AdminUniversityDetail {
    id: number;
    name: string;
    abbreviation: string;
    city: {
        name: string;
    };
}

export interface AdminInterestDetail {
    id: number;
    name: string;
}

export interface AdminCityDetail {
    id: number;
    name: string;
    country: string;
}

export interface AdminCareerDetail {
    id: number;
    name: string;
}

const USER_NORMAL: AdminUserDetail = {
    id: 101,
    firstname: "Lucia",
    lastname: "Martinez",
    username: "luciam",
    email: "lucia@example.com",
    university: { name: "Universidad de Buenos Aires" },
    career: { name: "Ingenieria" },
    locale: "es",
    profilePictureUrl: null,
    blocked: false,
};

const UNIVERSITY_NORMAL: AdminUniversityDetail = {
    id: 201,
    name: "Universidad de Valencia",
    abbreviation: "UV",
    city: { name: "Valencia" },
};

const INTEREST_NORMAL: AdminInterestDetail = {
    id: 301,
    name: "Tecnologia",
};

const CITY_NORMAL: AdminCityDetail = {
    id: 401,
    name: "Lisboa",
    country: "Portugal",
};

const CAREER_NORMAL: AdminCareerDetail = {
    id: 501,
    name: "Medicina",
};

export const getAdminUserDetailMock = (scenario: AdminDetailScenario): AdminUserDetail =>
    scenario === "empty" ? { ...USER_NORMAL, firstname: "", lastname: "", username: "", email: "" } : USER_NORMAL;

export const getAdminUniversityDetailMock = (scenario: AdminDetailScenario): AdminUniversityDetail =>
    scenario === "empty" ? { ...UNIVERSITY_NORMAL, name: "", abbreviation: "", city: { name: "" } } : UNIVERSITY_NORMAL;

export const getAdminInterestDetailMock = (scenario: AdminDetailScenario): AdminInterestDetail =>
    scenario === "empty" ? { ...INTEREST_NORMAL, name: "" } : INTEREST_NORMAL;

export const getAdminCityDetailMock = (scenario: AdminDetailScenario): AdminCityDetail =>
    scenario === "empty" ? { ...CITY_NORMAL, name: "", country: "" } : CITY_NORMAL;

export const getAdminCareerDetailMock = (scenario: AdminDetailScenario): AdminCareerDetail =>
    scenario === "empty" ? { ...CAREER_NORMAL, name: "" } : CAREER_NORMAL;

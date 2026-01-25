export type AdminDashboardTab =
    | "journeys"
    | "users"
    | "events"
    | "universities"
    | "interests"
    | "cities"
    | "careers"
    | "reports";

export interface PagedResult<T> {
    content: T[];
    totalPages: number;
    currentPage: number;
    pageSize: number;
    totalItems: number;
}

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
    locale?: string | null;
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

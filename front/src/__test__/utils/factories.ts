import { BASE_URL } from "./utils";

export interface MockEventDto {
    id: number;
    title: string;
    description?: string | null;
    date?: string | null;
    time?: string | null;
    address?: string | null;
    attendeesLimit?: number | null;
    attendeesCount?: number | null;
    rating?: number | null;
    links?: {
        selfUrl?: string | null;
        creatorUrl?: string | null;
        cityUrl?: string | null;
        flyerUrl?: string | null;
        responsesUrl?: string | null;
        attendancesUrl?: string | null;
        ratingsUrl?: string | null;
        statisticsUrl?: string | null;
    } | null;
}

export function createMockEvent(overrides: Partial<MockEventDto> = {}): MockEventDto {
    return {
        id: 1,
        title: "Test Event",
        description: "A test event description",
        date: "2030-06-15",
        time: "18:00",
        address: "123 Test Street",
        attendeesLimit: 50,
        attendeesCount: 10,
        rating: 4.5,
        links: {
            selfUrl: `${BASE_URL}/events/1`,
            creatorUrl: `${BASE_URL}/users/1`,
            cityUrl: `${BASE_URL}/cities/1`,
            flyerUrl: `${BASE_URL}/events/1/flyer`,
            responsesUrl: `${BASE_URL}/events/1/responses`,
            attendancesUrl: `${BASE_URL}/events/1/attendances`,
            ratingsUrl: `${BASE_URL}/events/1/ratings`,
            statisticsUrl: `${BASE_URL}/events/1/statistics`,
        },
        ...overrides,
    };
}

export interface MockJourneyDto {
    id: number;
    description: string;
    startDate: string;
    endDate: string;
    city?: string | null;
    country?: string | null;
    university?: string | null;
    userName?: string | null;
    profilePictureUrl?: string | null;
    links?: {
        selfUrl?: string | null;
        userUrl?: string | null;
        destinationUniversityUrl?: string | null;
    } | null;
}

export function createMockJourney(overrides: Partial<MockJourneyDto> = {}): MockJourneyDto {
    return {
        id: 1,
        description: "Test journey description",
        startDate: "2026-07-01",
        endDate: "2026-12-01",
        city: "Buenos Aires",
        country: "Argentina",
        university: "MIT",
        userName: "testuser",
        profilePictureUrl: null,
        links: {
            selfUrl: `${BASE_URL}/journeys/1`,
            userUrl: `${BASE_URL}/users/1`,
            destinationUniversityUrl: `${BASE_URL}/universities/1`,
        },
        ...overrides,
    };
}

export interface MockUserDto {
    id: number;
    username: string;
    email?: string | null;
    firstname: string | null;
    lastname: string | null;
    active?: boolean | null;
    links?: {
        selfUrl?: string | null;
        profilePictureUrl?: string | null;
        universityUrl?: string | null;
        careerUrl?: string | null;
        journeyUrl?: string | null;
    } | null;
}

export function createMockUser(overrides: Partial<MockUserDto> = {}): MockUserDto {
    return {
        id: 1,
        username: "testuser",
        email: "test@example.com",
        firstname: "Test",
        lastname: "User",
        active: true,
        links: {
            selfUrl: `${BASE_URL}/users/1`,
            profilePictureUrl: `${BASE_URL}/users/1/profilePicture`,
            universityUrl: `${BASE_URL}/universities/1`,
            careerUrl: `${BASE_URL}/careers/1`,
            journeyUrl: `${BASE_URL}/journeys/1`,
        },
        ...overrides,
    };
}

export interface MockReportDto {
    id: number;
    description?: string | null;
    reason: string;
    status: string;
    createdAt?: string | null;
    updatedAt?: string | null;
    links?: {
        selfUrl?: string | null;
        reportedUserUrl?: string | null;
        reportingUserUrl?: string | null;
        targetUrl?: string | null;
    } | null;
}

export function createMockReport(overrides: Partial<MockReportDto> = {}): MockReportDto {
    return {
        id: 1,
        description: "Test report description",
        reason: "SPAM",
        status: "PENDING",
        createdAt: "2026-01-15T10:00:00Z",
        updatedAt: null,
        links: {
            selfUrl: `${BASE_URL}/reports/1`,
            reportedUserUrl: `${BASE_URL}/users/2`,
            reportingUserUrl: `${BASE_URL}/users/1`,
            targetUrl: null,
        },
        ...overrides,
    };
}

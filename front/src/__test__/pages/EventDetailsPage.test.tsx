import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import EventDetailsPage from "@/pages/events/EventDetailsPage";

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string) => key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
    useTranslate: () => (key: string) => key,
    I18nProvider: ({ children }: { children: React.ReactNode }) => children,
}));

const mockUseEventDetailData = vi.fn();
vi.mock("@/hooks/useEventDetailData", () => ({
    useEventDetailData: (...args: unknown[]) => mockUseEventDetailData(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => null,
    getUsername: () => "user",
    isAdmin: () => false,
    isLoggedIn: () => false,
}));

vi.mock("@/lib/utils/navigationStack", () => ({
    pushToNavigationStack: vi.fn(),
    popFromNavigationStack: vi.fn().mockReturnValue(null),
}));

vi.mock("@/lib/api/events", () => ({
    attendEvent: vi.fn(),
    createEventRating: vi.fn(),
    createEventResponse: vi.fn(),
    deleteEventRating: vi.fn(),
    getEventAttendance: vi.fn().mockRejectedValue({ response: { status: 404 } }),
    getEventStatistics: vi.fn().mockResolvedValue({}),
    listEventAttendees: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 6, totalElements: 0, next: null, prev: null, first: null, last: null }),
    listEventResponses: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null }),
    unattendEvent: vi.fn(),
    updateEventRating: vi.fn(),
}));

vi.mock("@/lib/api/journeys", () => ({
    getUserByUrl: vi.fn(),
}));

function renderPage(eventId = "1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/events/:id", element: <EventDetailsPage /> }],
        { initialEntries: [`/events/${eventId}`] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("EventDetailsPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("should render event detail with title and description", () => {
        mockUseEventDetailData.mockReturnValue({
            data: {
                id: 1,
                title: "Test Event",
                description: "A test event",
                city: { name: "Buenos Aires" },
                date: "2026-06-15",
                time: "18:00",
                address: "123 Test St",
                flyerImageUrl: null,
                attendeesCount: 10,
                attendeesLimit: 50,
                isFuture: true,
                user: {
                    id: 1,
                    firstname: "Test",
                    lastname: "User",
                    username: "testuser",
                    profilePictureUrl: null,
                    university: null,
                    career: null,
                },
                comments: [],
                attendees: [],
                ratings: [],
                averageRating: null,
            },
            isLoading: false,
            isError: false,
            isFetching: false,
        });

        renderPage();
        expect(screen.getByText("Test Event")).toBeInTheDocument();
        expect(screen.getByText("A test event")).toBeInTheDocument();
    });

    it("should show creator card with user info", () => {
        mockUseEventDetailData.mockReturnValue({
            data: {
                id: 1,
                title: "Test Event",
                description: "desc",
                city: { name: "Buenos Aires" },
                date: "2026-06-15",
                time: null,
                address: null,
                flyerImageUrl: null,
                attendeesCount: 0,
                attendeesLimit: null,
                isFuture: true,
                user: {
                    id: 1,
                    firstname: "Test",
                    lastname: "Creator",
                    username: "testcreator",
                    profilePictureUrl: null,
                    university: { name: "MIT" },
                    career: { name: "CS" },
                },
                comments: [],
                attendees: [],
                ratings: [],
                averageRating: null,
            },
            isLoading: false,
            isError: false,
            isFetching: false,
        });

        renderPage();
        expect(screen.getByText("Test Creator")).toBeInTheDocument();
    });

    it("should show content tabs", () => {
        mockUseEventDetailData.mockReturnValue({
            data: {
                id: 1,
                title: "Test Event",
                description: "desc",
                city: { name: "Buenos Aires" },
                date: "2026-06-15",
                time: null,
                address: null,
                flyerImageUrl: null,
                attendeesCount: 0,
                attendeesLimit: null,
                isFuture: true,
                user: { id: 1, firstname: "T", lastname: "U", username: "tu", profilePictureUrl: null, university: null, career: null },
                comments: [],
                attendees: [],
                ratings: [],
                averageRating: null,
            },
            isLoading: false,
            isError: false,
            isFetching: false,
        });

        renderPage();
        expect(screen.getByText("event.details")).toBeInTheDocument();
        expect(screen.getByText("event.chat")).toBeInTheDocument();
    });

    it("should show loading state", () => {
        mockUseEventDetailData.mockReturnValue({
            data: null,
            isLoading: true,
            isError: false,
            isFetching: true,
        });

        renderPage();
        expect(screen.queryByText("Test Event")).not.toBeInTheDocument();
    });
});

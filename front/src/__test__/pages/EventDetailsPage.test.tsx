import { describe, it, expect, vi, beforeEach } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
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
let mockUserId: number | null = null;
const mockListEventAttendees = vi.fn();
const mockListEventResponses = vi.fn();

vi.mock("@/hooks/useEventDetailData", () => ({
    useEventDetailData: (...args: unknown[]) => mockUseEventDetailData(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => mockUserId,
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
    listEventAttendees: (...args: unknown[]) => mockListEventAttendees(...args),
    listEventResponses: (...args: unknown[]) => mockListEventResponses(...args),
    unattendEvent: vi.fn(),
    updateEventRating: vi.fn(),
}));

vi.mock("@/lib/api/journeys", () => ({
    getUserByUrl: vi.fn(),
}));

function renderPage(initialEntry = "/events/1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/events/:id", element: <EventDetailsPage /> }],
        { initialEntries: [initialEntry] },
    );
    const rendered = render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
    return { router, ...rendered };
}

describe("EventDetailsPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUserId = null;
        mockListEventAttendees.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 6, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockListEventResponses.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null });
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
            creatorLoading: false,
            creatorError: false,
            creatorReady: true,
            cityLoading: false,
            ratingsLoading: false,
            ratingsError: false,
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
            creatorLoading: false,
            creatorError: false,
            creatorReady: true,
            cityLoading: false,
            ratingsLoading: false,
            ratingsError: false,
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
            creatorLoading: false,
            creatorError: false,
            creatorReady: true,
            cityLoading: false,
            ratingsLoading: false,
            ratingsError: false,
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

    it("should initialize chat state from search params", async () => {
        mockListEventResponses.mockResolvedValue({
            content: [],
            totalPages: 0,
            currentPage: 3,
            pageSize: 4,
            totalElements: 0,
            next: null,
            prev: null,
            first: null,
            last: null,
        });
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
                user: { id: 2, firstname: "T", lastname: "U", username: "tu", profilePictureUrl: null, university: null, career: null },
                comments: [],
                attendees: [],
                ratings: [],
                averageRating: null,
            },
            isLoading: false,
            isError: false,
            isFetching: false,
        });

        renderPage("/events/1?tab=chat&commentsPage=3");

        await waitFor(() => {
            expect(mockListEventResponses).toHaveBeenCalledWith(1, { page: 3, size: 4 }, expect.anything());
        });
        expect(screen.getByRole("button", { name: /event.chat/i })).toHaveClass("active");
    });

    it("should initialize attendees page from search params", async () => {
        mockUserId = 1;
        mockListEventAttendees.mockResolvedValue({
            content: [],
            totalPages: 0,
            currentPage: 2,
            pageSize: 6,
            totalElements: 0,
            next: null,
            prev: null,
            first: null,
            last: null,
        });
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

        renderPage("/events/1?attendeesPage=2");

        await waitFor(() => {
            expect(mockListEventAttendees).toHaveBeenCalledWith(1, { page: 2, size: 6 }, expect.anything(), expect.anything());
        });
    });

    it("should write tab state into the url", () => {
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
                user: { id: 2, firstname: "T", lastname: "U", username: "tu", profilePictureUrl: null, university: null, career: null },
                comments: [],
                attendees: [],
                ratings: [],
                averageRating: null,
            },
            isLoading: false,
            isError: false,
            isFetching: false,
        });

        const { router } = renderPage();

        fireEvent.click(screen.getByRole("button", { name: /event.chat/i }));

        expect(router.state.location.search).toBe("?tab=chat");
    });
});

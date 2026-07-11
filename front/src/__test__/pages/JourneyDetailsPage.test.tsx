import { describe, it, expect, vi, beforeEach } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import JourneyDetailsPage from "@/pages/journeys/JourneyDetailsPage";

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

const mockUseJourneyDetailData = vi.fn();
const mockGetJourneyResponses = vi.fn();
const mockListJourneyTips = vi.fn();
const mockFetchEvents = vi.fn();
const mockGetUserInterests = vi.fn();

vi.mock("@/hooks/useJourneyDetailData", () => ({
    useJourneyDetailData: (...args: unknown[]) => mockUseJourneyDetailData(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => null,
    getUsername: () => null,
    isAdmin: () => false,
    isLoggedIn: () => false,
}));

vi.mock("@/lib/utils/navigationStack", () => ({
    pushToNavigationStack: vi.fn(),
    popFromNavigationStack: vi.fn().mockReturnValue(null),
}));

vi.mock("@/lib/api/journeys", () => ({
    createJourneyResponse: vi.fn(),
    getCityByUrl: vi.fn(),
    getJourneyResponses: (...args: unknown[]) => mockGetJourneyResponses(...args),
    getUserByUrl: vi.fn(),
    listJourneyTips: (...args: unknown[]) => mockListJourneyTips(...args),
}));

vi.mock("@/lib/api/events", () => ({
    fetchEvents: (...args: unknown[]) => mockFetchEvents(...args),
}));

vi.mock("@/lib/api/users", () => ({
    getUserInterests: (...args: unknown[]) => mockGetUserInterests(...args),
}));

function renderPage(initialEntry = "/journeys/1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/journeys/:id", element: <JourneyDetailsPage /> }],
        { initialEntries: [initialEntry] },
    );
    const rendered = render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
    return { router, ...rendered };
}

const baseMockData = {
    id: 1,
    description: "An amazing exchange journey",
    startDate: "2026-07-01",
    endDate: "2026-12-01",
    links: null,
    destinationUniversity: { name: "MIT", city: "Boston" },
    user: {
        id: 1,
        firstname: "Test",
        lastname: "Traveler",
        username: "testtraveler",
        profilePictureUrl: null,
        university: { name: "UBA" },
        career: { name: "CS" },
    },
};

const baseResult = {
    isLoading: false,
    isError: false,
    isNotFound: false,
    isFetching: false,
    refetch: vi.fn(),
    creatorLoading: false,
    creatorError: false,
    creatorReady: true,
    destinationLoading: false,
    destinationError: false,
    destinationReady: true,
};

describe("JourneyDetailsPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockGetJourneyResponses.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockListJourneyTips.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockFetchEvents.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 6, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockGetUserInterests.mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 10, totalElements: 0, next: null, prev: null, first: null, last: null });
    });

    it("should render journey detail with description and destination", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        renderPage();
        expect(screen.getByText("An amazing exchange journey")).toBeInTheDocument();
        expect(screen.getByText(/MIT/)).toBeInTheDocument();
    });

    it("should show creator card with username", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        renderPage();
        expect(screen.getByText("@testtraveler")).toBeInTheDocument();
    });

    it("should link the creator card to the public profile page", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        const { container } = renderPage();
        const creatorLink = container.querySelector(".profile-card-link");

        expect(creatorLink).toHaveAttribute("href", "/profiles/1/info");
    });

    it("should show section headers for content areas", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        renderPage();
        expect(screen.getByText("journey.detail.interests")).toBeInTheDocument();
        expect(screen.getByText("journey.detail.tips")).toBeInTheDocument();
        expect(screen.getByText("journey.detail.responses")).toBeInTheDocument();
    });

    it("should show back navigation", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        renderPage();
        expect(screen.getByText("journey.detail.back.to.list")).toBeInTheDocument();
    });

    it("should show loading state", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: null,
            isLoading: true,
            isError: false,
            isNotFound: false,
            isFetching: true,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.queryByText("An amazing exchange journey")).not.toBeInTheDocument();
    });

    it("should initialize page and tab state from search params", async () => {
        mockGetUserInterests.mockResolvedValue({ content: [], totalPages: 0, currentPage: 2, pageSize: 8, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockListJourneyTips.mockResolvedValue({ content: [], totalPages: 0, currentPage: 2, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockGetJourneyResponses.mockResolvedValue({ content: [], totalPages: 0, currentPage: 4, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockFetchEvents.mockResolvedValue({ content: [], totalPages: 0, currentPage: 3, pageSize: 6, totalElements: 0, next: null, prev: null, first: null, last: null });
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        renderPage("/journeys/1?interestsPage=2&eventsTab=attending&attendingEventsPage=3&tipsPage=2&commentsPage=4");

        await waitFor(() => {
            expect(mockGetUserInterests).toHaveBeenCalledWith(1, { page: 2, size: 8 }, expect.anything());
            expect(mockListJourneyTips).toHaveBeenCalledWith(1, { page: 2, size: 4 }, expect.anything());
            expect(mockGetJourneyResponses).toHaveBeenCalledWith(1, { page: 4, size: 4 }, expect.anything());
            expect(mockFetchEvents).toHaveBeenCalledWith(
                expect.objectContaining({ attendedBy: 1, page: 3, size: 6 }),
                expect.anything()
            );
        });
        expect(screen.getByRole("button", { name: /journey.events.attending/i })).toHaveClass("active");
    });

    it("should write events subtab state into the url", () => {
        mockUseJourneyDetailData.mockReturnValue({
            data: baseMockData,
            ...baseResult,
        });

        const { router } = renderPage();

        fireEvent.click(screen.getByRole("button", { name: /journey.events.attending/i }));

        expect(router.state.location.search).toBe("?eventsTab=attending");
    });
});

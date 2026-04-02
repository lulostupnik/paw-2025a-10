import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
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
vi.mock("@/hooks/useJourneyDetailData", () => ({
    useJourneyDetailData: (...args: unknown[]) => mockUseJourneyDetailData(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => null,
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
    getJourneyResponses: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null }),
    getUserByUrl: vi.fn(),
    listJourneyTips: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 4, totalElements: 0, next: null, prev: null, first: null, last: null }),
}));

vi.mock("@/lib/api/events", () => ({
    fetchEvents: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 6, totalElements: 0, next: null, prev: null, first: null, last: null }),
}));

vi.mock("@/lib/api/users", () => ({
    getUserInterests: vi.fn().mockResolvedValue({ content: [], totalPages: 0, currentPage: 1, pageSize: 10, totalElements: 0, next: null, prev: null, first: null, last: null }),
}));

function renderPage(journeyId = "1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/journeys/:id", element: <JourneyDetailsPage /> }],
        { initialEntries: [`/journeys/${journeyId}`] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
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
};

describe("JourneyDetailsPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
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
});

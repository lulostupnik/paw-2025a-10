import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import JourneysListPage from "@/pages/journeys/JourneysPage";
import { emptyPage } from "@/types/pagination";

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

const mockUseJourneys = vi.fn();
vi.mock("@/hooks/useJourneys", () => ({
    useJourneys: (...args: unknown[]) => mockUseJourneys(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    isLoggedIn: () => false,
    getUserId: () => null,
}));

vi.mock("@/hooks/profiles/useProfileDetail", () => ({
    useProfileDetail: () => ({ data: null, isLoading: false, isError: false }),
}));

vi.mock("@/hooks/useListingFilters", () => ({
    useUrlSyncedListingFilters: () => ({
        filters: {},
        applyFilters: vi.fn(),
        resetFilters: vi.fn(),
    }),
}));

function renderPage() {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/journeys", element: <JourneysListPage /> }],
        { initialEntries: ["/journeys"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("JourneysPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("should render journeys listing with journeys", () => {
        mockUseJourneys.mockReturnValue({
            journeys: {
                ...emptyPage(),
                content: [
                    { id: 1, description: "Journey 1", startDate: "2026-07-01", endDate: "2026-12-01", university: "MIT", city: "Boston", userName: "user1", profilePictureUrl: null },
                    { id: 2, description: "Journey 2", startDate: "2026-08-01", endDate: "2026-11-01", university: "Stanford", city: "Palo Alto", userName: "user2", profilePictureUrl: null },
                ],
                totalElements: 2,
            },
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("MIT")).toBeInTheDocument();
        expect(screen.getByText("Stanford")).toBeInTheDocument();
    });

    it("should show empty state when no journeys", () => {
        mockUseJourneys.mockReturnValue({
            journeys: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("journey.no.journeys")).toBeInTheDocument();
    });

    it("should render page title", () => {
        mockUseJourneys.mockReturnValue({
            journeys: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("journeys.page.title")).toBeInTheDocument();
    });

    it("should show loading skeleton when loading", () => {
        mockUseJourneys.mockReturnValue({
            journeys: emptyPage(),
            loading: true,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.queryByText("MIT")).not.toBeInTheDocument();
    });
});

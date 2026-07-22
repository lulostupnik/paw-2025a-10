import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import ExplorePage from "@/pages/explore/ExplorePage";
import { emptyPage } from "@/types/pagination";

const { mockUseEvents, mockGetUserId } = vi.hoisted(() => ({
    mockUseEvents: vi.fn(),
    mockGetUserId: vi.fn(),
}));

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

vi.mock("@/hooks/useEvents", () => ({
    useEvents: (...args: unknown[]) => mockUseEvents(...args),
}));

vi.mock("@/hooks/useJourneys", () => ({
    useJourneys: () => ({
        journeys: emptyPage(),
        loading: false,
        error: null,
        refetch: vi.fn(),
    }),
}));

vi.mock("@/hooks/profiles/useProfileDetail", () => ({
    useProfileDetail: () => ({ data: null, isLoading: false, isError: false }),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => mockGetUserId(),
    isLoggedIn: () => false,
}));

function renderPage() {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/explore", element: <ExplorePage /> }],
        { initialEntries: ["/explore"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("ExplorePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockGetUserId.mockReturnValue(null);
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });
    });

    it("should render explore page title", () => {
        renderPage();
        expect(screen.getByText("nav.explore")).toBeInTheDocument();
    });

    it("should show sections for events and journeys", () => {
        renderPage();
        expect(screen.getAllByText("dashboard.recommended.events").length).toBeGreaterThan(0);
        expect(screen.getAllByText("dashboard.recommended.journeys").length).toBeGreaterThan(0);
    });

    it("should request top events when the user is anonymous", () => {
        renderPage();

        expect(mockUseEvents).toHaveBeenCalledWith({
            page: 1,
            size: 6,
            top: true,
        });
    });

    it("should request recommended events when the user is logged in", () => {
        mockGetUserId.mockReturnValue(7);

        renderPage();

        expect(mockUseEvents).toHaveBeenCalledWith({
            page: 1,
            size: 6,
            recommendedForUser: 7,
        });
    });
});

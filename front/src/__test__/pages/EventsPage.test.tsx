import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import EventsListPage from "@/pages/events/EventsPage";
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

const mockUseEvents = vi.fn();
vi.mock("@/hooks/useEvents", () => ({
    useEvents: (...args: unknown[]) => mockUseEvents(...args),
}));

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

vi.mock("@/lib/auth/auth", () => ({
    isLoggedIn: () => false,
    getUserId: () => null,
    isAdmin: () => false,
}));

vi.mock("@/lib/utils/date", () => ({
    getTodayIsoDate: () => "2026-04-01",
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
        [{ path: "/events", element: <EventsListPage /> }],
        { initialEntries: ["/events"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("EventsPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("should render events listing with events", () => {
        mockUseEvents.mockReturnValue({
            events: {
                ...emptyPage(),
                content: [
                    { id: 1, title: "Event 1", date: "2026-06-15", flyerUrl: null, isFuture: true },
                    { id: 2, title: "Event 2", date: "2026-07-20", flyerUrl: null, isFuture: true },
                ],
                totalElements: 2,
            },
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("Event 1")).toBeInTheDocument();
        expect(screen.getByText("Event 2")).toBeInTheDocument();
    });

    it("should show loading skeleton when loading", () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: true,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.queryByText("Event 1")).not.toBeInTheDocument();
    });

    it("should show empty state when no events", () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("events.list.empty")).toBeInTheDocument();
    });

    it("should render page title", () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("events.page.title")).toBeInTheDocument();
    });
});

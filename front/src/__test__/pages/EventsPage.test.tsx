import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
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

function renderPage(initialEntry = "/events") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/events", element: <EventsListPage /> }],
        { initialEntries: [initialEntry] },
    );
    const result = render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
    return { ...result, router };
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

    it("should reset page when changing tab", async () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });
        const user = userEvent.setup();
        const { router } = renderPage("/events?page=8&sort=event-date-desc");

        await user.click(screen.getByRole("tab", { name: "events.tabs.upcoming" }));

        const params = new URLSearchParams(router.state.location.search);
        expect(params.get("page")).toBeNull();
        expect(params.get("tab")).toBe("upcoming");
        expect(params.get("sort")).toBe("event-date-desc");
    });

    it("should reset page when changing sort", async () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });
        const user = userEvent.setup();
        const { router } = renderPage("/events?page=8&tab=upcoming");

        await user.click(screen.getByRole("button", { name: "listing.sort" }));
        await user.click(screen.getByRole("menuitemradio", { name: "event.sort.rating.desc" }));

        const params = new URLSearchParams(router.state.location.search);
        expect(params.get("page")).toBeNull();
        expect(params.get("sort")).toBe("event-rating-desc");
        expect(params.get("tab")).toBe("upcoming");
    });

    it("should fall back to all when tab query param is invalid", () => {
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });

        renderPage("/events?tab=weird");
        expect(screen.getByRole("tab", { name: "events.tabs.all" })).toHaveAttribute("aria-selected", "true");
    });
});

import { describe, it, expect, vi, beforeEach } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import EventDeletePage from "@/pages/events/EventDeletePage";

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string) => key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
}));

const mockUseEventDetailData = vi.fn();
const mockDeleteEvent = vi.fn();
let mockUserId: number | null = null;

vi.mock("@/hooks/useEventDetailData", () => ({
    useEventDetailData: (...args: unknown[]) => mockUseEventDetailData(...args),
}));

vi.mock("@/lib/auth/auth", () => ({
    getUserId: () => mockUserId,
    isAdmin: () => false,
}));

vi.mock("@/lib/api/events", () => ({
    deleteEvent: (...args: unknown[]) => mockDeleteEvent(...args),
}));

vi.mock("@/components/ui/ToastProvider", () => ({
    useToast: () => ({ showToast: vi.fn() }),
}));

vi.mock("@/lib/utils/navigationStack", () => ({
    popFromNavigationStack: vi.fn().mockReturnValue(null),
}));

const ownedEvent = {
    id: 1,
    title: "Test Event",
    date: "2030-06-15",
    time: "18:00",
    attendeesCount: 10,
    city: { id: 1, name: "Milano" },
    user: { id: 7 },
};

const renderPage = (queryClient: QueryClient) => {
    const router = createMemoryRouter(
        [{ path: "/events/:id/delete", element: <EventDeletePage /> }],
        { initialEntries: ["/events/1/delete"] },
    );
    render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
    return router;
};

beforeEach(() => {
    vi.clearAllMocks();
    mockUserId = 7;
    mockDeleteEvent.mockResolvedValue(undefined);
    mockUseEventDetailData.mockReturnValue({ data: ownedEvent, isLoading: false, isError: false, isNotFound: false });
});

describe("EventDeletePage", () => {
    it("invalidates the cached listings so the deleted event stops showing up", async () => {
        const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false, gcTime: Infinity } } });
        queryClient.setQueryData(["events", { page: 1 }], { content: [] });
        queryClient.setQueryData(["profileEvents", "7", { createdPage: 1 }], { content: [] });

        const router = renderPage(queryClient);
        fireEvent.click(screen.getByRole("button", { name: "event.delete" }));

        await waitFor(() => expect(router.state.location.pathname).toBe("/events"));
        expect(mockDeleteEvent).toHaveBeenCalledWith(1, undefined);
        expect(queryClient.getQueryState(["events", { page: 1 }])?.isInvalidated).toBe(true);
        expect(queryClient.getQueryState(["profileEvents", "7", { createdPage: 1 }])?.isInvalidated).toBe(true);
    });

    it("keeps the listings cached when the delete fails", async () => {
        mockDeleteEvent.mockRejectedValue(new Error("boom"));
        const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false, gcTime: Infinity } } });
        queryClient.setQueryData(["events", { page: 1 }], { content: [] });

        const router = renderPage(queryClient);
        fireEvent.click(screen.getByRole("button", { name: "event.delete" }));

        await waitFor(() => expect(mockDeleteEvent).toHaveBeenCalled());
        expect(router.state.location.pathname).toBe("/events/1/delete");
        expect(queryClient.getQueryState(["events", { page: 1 }])?.isInvalidated).toBe(false);
    });

    it("shows not found message when event does not exist", () => {
        mockUseEventDetailData.mockReturnValue({ data: null, isLoading: false, isError: false, isNotFound: true });
        const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });

        renderPage(queryClient);

        expect(screen.getByText("event.not.found.title")).toBeInTheDocument();
    });
});

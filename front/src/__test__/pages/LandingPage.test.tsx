import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import LandingPage from "@/pages/landing/LandingPage";
import { emptyPage } from "@/types/pagination";

const { mockUseEvents } = vi.hoisted(() => ({
    mockUseEvents: vi.fn(),
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

vi.mock("@/hooks/useAuthGate", () => ({
    useAuthGate: () => ({ open: false, close: vi.fn(), runOrPrompt: vi.fn() }),
}));

function renderPage() {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/", element: <LandingPage /> }],
        { initialEntries: ["/"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("LandingPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUseEvents.mockReturnValue({
            events: emptyPage(),
            loading: false,
            error: null,
            refetch: vi.fn(),
        });
    });

    it("should render landing page content", () => {
        renderPage();
        expect(screen.getByText("landing.hero.title")).toBeInTheDocument();
        expect(screen.getByText("landing.hero.subtitle")).toBeInTheDocument();
    });

    it("should show feature sections", () => {
        renderPage();
        expect(screen.getByText("landing.feature1.title")).toBeInTheDocument();
        expect(screen.getByText("landing.feature2.title")).toBeInTheDocument();
    });

    it("should show call to action buttons", () => {
        renderPage();
        expect(screen.getByText("landing.cta.button")).toBeInTheDocument();
    });

    it("should request top events for featured events", () => {
        renderPage();

        expect(mockUseEvents).toHaveBeenCalledWith({
            page: 1,
            size: 3,
            top: true,
        });
    });
});

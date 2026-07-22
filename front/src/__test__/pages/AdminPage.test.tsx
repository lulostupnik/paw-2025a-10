import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AdminPage from "@/pages/admin/AdminPage";
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

vi.mock("@/lib/auth/auth", () => ({
    isAdmin: () => true,
    isLoggedIn: () => true,
    getUserId: () => 1,
}));

const emptyResult = () => ({
    data: emptyPage(),
    isLoading: false,
    isError: false,
    refetch: vi.fn(),
});

vi.mock("@/hooks/admin/useAdminTabData", () => ({
    useAdminJourneys: () => emptyResult(),
    useAdminUsers: () => emptyResult(),
    useAdminEvents: () => emptyResult(),
    useAdminUniversities: () => emptyResult(),
    useAdminInterests: () => emptyResult(),
    useAdminCities: () => emptyResult(),
    useAdminCareers: () => emptyResult(),
}));

vi.mock("@/hooks/useReports", () => ({
    useReports: () => ({
        data: emptyPage(),
        isLoading: false,
        isError: false,
    }),
}));

function renderPage(tab = "journeys") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/admin/:tab?", element: <AdminPage /> }],
        { initialEntries: [`/admin/${tab}`] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("AdminPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("should render admin dashboard with tabs", () => {
        renderPage();
        expect(screen.getByText("admin.dashboard.heading")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.journeys")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.users")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.events")).toBeInTheDocument();
    });

    it("should show all admin tab options", () => {
        renderPage();
        expect(screen.getByText("admin.tab.universities")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.interests")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.cities")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.careers")).toBeInTheDocument();
        expect(screen.getByText("admin.tab.reports")).toBeInTheDocument();
    });
});

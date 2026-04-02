import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import ProfileDetailPage from "@/pages/profiles/ProfileDetail";
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

const mockUseProfileDetail = vi.fn();
vi.mock("@/hooks/profiles/useProfileDetail", () => ({
    useProfileDetail: (...args: unknown[]) => mockUseProfileDetail(...args),
}));

vi.mock("@/hooks/profiles/useProfileEvents", () => ({
    useProfileEvents: () => ({
        created: emptyPage(),
        attending: emptyPage(),
        finished: emptyPage(),
        isLoading: false,
        isError: false,
        isFetching: false,
        refetch: vi.fn(),
    }),
}));

vi.mock("@/hooks/profiles/useProfileInterests", () => ({
    useProfileInterests: () => ({
        data: emptyPage(),
        isLoading: false,
        isError: false,
        isNotFound: false,
        isFetching: false,
        refetch: vi.fn(),
    }),
}));

vi.mock("@/lib/utils/internalPath", () => ({
    sanitizeInternalPath: (v: unknown) => v ?? null,
}));

function renderPage(profileId = "1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/profiles/:profileId/:tab?", element: <ProfileDetailPage /> }],
        { initialEntries: [`/profiles/${profileId}`] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("ProfileDetail", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        sessionStorage.clear();
    });

    it("should render profile with user info", () => {
        mockUseProfileDetail.mockReturnValue({
            data: {
                id: 1,
                firstname: "Test",
                lastname: "User",
                username: "testuser",
                email: "test@example.com",
                links: { profilePictureUrl: null, universityUrl: null, careerUrl: null },
                ratingStats: { averageCreatedEventsRating: 4.5, averageAttendedEventsRating: 4.0 },
                university: { name: "MIT" },
                career: { name: "CS" },
                isMine: false,
            },
            isLoading: false,
            isError: false,
            isNotFound: false,
            isFetching: false,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("@testuser")).toBeInTheDocument();
    });

    it("should show loading state", () => {
        mockUseProfileDetail.mockReturnValue({
            data: null,
            isLoading: true,
            isError: false,
            isNotFound: false,
            isFetching: true,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.queryByText("testuser")).not.toBeInTheDocument();
    });

    it("should show profile tabs", () => {
        mockUseProfileDetail.mockReturnValue({
            data: {
                id: 1,
                firstname: "Test",
                lastname: "User",
                username: "testuser",
                email: null,
                links: null,
                ratingStats: { averageCreatedEventsRating: null, averageAttendedEventsRating: null },
                university: null,
                career: null,
                isMine: false,
            },
            isLoading: false,
            isError: false,
            isNotFound: false,
            isFetching: false,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("profile.tab.info")).toBeInTheDocument();
        expect(screen.getByText("profile.tab.events")).toBeInTheDocument();
        expect(screen.getByText("profile.tab.interests")).toBeInTheDocument();
    });

    it("should show university and career info", () => {
        mockUseProfileDetail.mockReturnValue({
            data: {
                id: 1,
                firstname: "Test",
                lastname: "User",
                username: "testuser",
                email: null,
                links: null,
                ratingStats: { averageCreatedEventsRating: null, averageAttendedEventsRating: null },
                university: { name: "MIT" },
                career: { name: "Computer Science" },
                isMine: false,
            },
            isLoading: false,
            isError: false,
            isNotFound: false,
            isFetching: false,
            refetch: vi.fn(),
        });

        renderPage();
        expect(screen.getByText("MIT")).toBeInTheDocument();
        expect(screen.getByText("Computer Science")).toBeInTheDocument();
    });
});

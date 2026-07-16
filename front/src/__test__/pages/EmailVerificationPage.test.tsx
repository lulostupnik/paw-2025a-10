import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import EmailVerificationPage from "@/pages/auth/EmailVerificationPage";

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

const mockVerifyEmailToken = vi.fn();
vi.mock("@/lib/api/auth", () => ({
    verifyEmailToken: (...args: unknown[]) => mockVerifyEmailToken(...args),
}));

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
    const actual = await vi.importActual("react-router-dom");
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

function renderEmailVerificationPage(path = "/validate?token=abc123&userId=7&email=test%40example.com") {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
    });
    const router = createMemoryRouter(
        [{ path: "/validate", element: <EmailVerificationPage /> }],
        { initialEntries: [path] },
    );

    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("EmailVerificationPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("redirects authenticated users to explore after a successful verification", async () => {
        mockVerifyEmailToken.mockResolvedValue(undefined);

        renderEmailVerificationPage();

        await waitFor(() => {
            expect(mockVerifyEmailToken).toHaveBeenCalledWith(
                { userId: "7", email: "test@example.com", token: "abc123" },
            );
            expect(mockNavigate).toHaveBeenCalledWith("/explore", { replace: true });
        });
    });
});

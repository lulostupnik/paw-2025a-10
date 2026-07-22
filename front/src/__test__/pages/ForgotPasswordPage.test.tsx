import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import ForgotPasswordPage from "@/pages/auth/ForgotPasswordPage";

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

const mockRequestPasswordReset = vi.fn();
vi.mock("@/lib/api/auth", () => ({
    requestPasswordReset: (...args: unknown[]) => mockRequestPasswordReset(...args),
}));

function renderPage() {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [{ path: "/forgot-password", element: <ForgotPasswordPage /> }],
        { initialEntries: ["/forgot-password"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("ForgotPasswordPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        sessionStorage.clear();
    });

    it("should render form elements", () => {
        renderPage();
        expect(screen.getByRole("heading", { name: "forgotpassword.title" })).toBeInTheDocument();
        expect(screen.getByPlaceholderText("forgotpassword.email.placeholder")).toBeInTheDocument();
        expect(screen.getByText("forgotpassword.submit")).toBeInTheDocument();
    });

    it("should show validation error for empty email", async () => {
        const user = userEvent.setup();
        renderPage();

        await user.click(screen.getByText("forgotpassword.submit"));

        expect(screen.getByText("forgotpassword.error.email.required")).toBeInTheDocument();
    });

    it("should show validation error for invalid email", async () => {
        const user = userEvent.setup();
        renderPage();

        await user.type(screen.getByPlaceholderText("forgotpassword.email.placeholder"), "invalid");
        await user.click(screen.getByText("forgotpassword.submit"));

        expect(screen.getByText("forgotpassword.error.email.invalid")).toBeInTheDocument();
    });

    it("should call requestPasswordReset on valid submit", async () => {
        mockRequestPasswordReset.mockResolvedValue(undefined);
        const user = userEvent.setup();
        renderPage();

        await user.type(screen.getByPlaceholderText("forgotpassword.email.placeholder"), "test@example.com");
        await user.click(screen.getByText("forgotpassword.submit"));

        expect(mockRequestPasswordReset).toHaveBeenCalledWith({ email: "test@example.com" });
    });

    it("should show success state after successful submit", async () => {
        mockRequestPasswordReset.mockResolvedValue(undefined);
        const user = userEvent.setup();
        renderPage();

        await user.type(screen.getByPlaceholderText("forgotpassword.email.placeholder"), "test@example.com");
        await user.click(screen.getByText("forgotpassword.submit"));

        expect(await screen.findByText("forgotpassword.email.instructions")).toBeInTheDocument();
    });

    it("should have link to login page", () => {
        renderPage();
        expect(screen.getByText("forgotpassword.login")).toBeInTheDocument();
    });
});

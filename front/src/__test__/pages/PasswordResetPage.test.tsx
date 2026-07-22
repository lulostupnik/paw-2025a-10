import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import PasswordResetPage from "@/pages/auth/PasswordResetPage";

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string, opts?: { defaultValue?: string }) => opts?.defaultValue ?? key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
    useTranslate: () => (key: string) => key,
    I18nProvider: ({ children }: { children: React.ReactNode }) => children,
}));

const mockResetPasswordWithToken = vi.fn();
vi.mock("@/lib/api/auth", () => ({
    resetPasswordWithToken: (...args: unknown[]) => mockResetPasswordWithToken(...args),
}));

function renderPage(token = "valid-token", userId = "1") {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const router = createMemoryRouter(
        [
            { path: "/password/reset", element: <PasswordResetPage /> },
            { path: "/password/reset/confirmation", element: <div>Confirmation</div> },
        ],
        { initialEntries: [`/password/reset?token=${token}&userId=${userId}`] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("PasswordResetPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        sessionStorage.clear();
    });

    it("should render password reset form with token and userId", () => {
        renderPage();
        expect(screen.getByRole("heading", { name: "profile.edit.password" })).toBeInTheDocument();
        expect(screen.getAllByPlaceholderText("••••••••").length).toBeGreaterThan(0);
    });

    it("should show invalid state when token is missing", () => {
        renderPage("", "1");
        expect(screen.getByText("invalidtoken.title")).toBeInTheDocument();
    });

    it("should show invalid state when userId is missing", () => {
        renderPage("valid-token", "");
        expect(screen.getByText("invalidtoken.title")).toBeInTheDocument();
    });

    it("should show password strength indicator", () => {
        renderPage();
        expect(screen.getByText("register.password.strength.label")).toBeInTheDocument();
    });

    it("should show validation error for empty password on submit", async () => {
        const user = userEvent.setup();
        renderPage();

        const submitButton = screen.getByText("profile.edit.password.save");
        await user.click(submitButton);

        expect(screen.getByText("register.validation.password.required")).toBeInTheDocument();
    });

    it("should allow typing in password fields", async () => {
        const user = userEvent.setup();
        renderPage();

        const inputs = screen.getAllByPlaceholderText("••••••••");
        await user.type(inputs[0], "NewPassword1");
        expect(inputs[0]).toHaveValue("NewPassword1");
    });

    it("should show invalid state when backend returns access denied", async () => {
        mockResetPasswordWithToken.mockRejectedValue({
            isAxiosError: true,
            response: { status: 403, data: { message: "You don't have permission to access this resource." } },
        });
        const user = userEvent.setup();
        renderPage();

        const inputs = screen.getAllByPlaceholderText("••••••••");
        await user.type(inputs[0], "NewPassword1");
        await user.type(inputs[1], "NewPassword1");
        await user.click(screen.getByText("profile.edit.password.save"));

        expect(await screen.findByText("invalidtoken.title")).toBeInTheDocument();
    });

    it("should show blocked state when backend returns blocked message", async () => {
        mockResetPasswordWithToken.mockRejectedValue({
            isAxiosError: true,
            response: { status: 403, data: { message: "Your account has been blocked by an administrator." } },
        });
        const user = userEvent.setup();
        renderPage();

        const inputs = screen.getAllByPlaceholderText("••••••••");
        await user.type(inputs[0], "NewPassword1");
        await user.type(inputs[1], "NewPassword1");
        await user.click(screen.getByText("profile.edit.password.save"));

        expect(await screen.findByText("blocked.title")).toBeInTheDocument();
    });
});

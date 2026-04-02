import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import LoginPage from "@/pages/auth/LoginPage";

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

const mockLogin = vi.fn();
vi.mock("@/lib/api/auth", () => ({
    login: (...args: unknown[]) => mockLogin(...args),
}));

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
    const actual = await vi.importActual("react-router-dom");
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

function renderLoginPage() {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
    });
    const router = createMemoryRouter(
        [{ path: "/login", element: <LoginPage /> }],
        { initialEntries: ["/login"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("LoginPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should render login form elements", () => {
        renderLoginPage();
        expect(screen.getByText("login.title")).toBeInTheDocument();
        expect(screen.getByPlaceholderText("login.email.placeholder")).toBeInTheDocument();
        expect(screen.getByPlaceholderText("login.passwordPlaceholder")).toBeInTheDocument();
        expect(screen.getByText("login.submit")).toBeInTheDocument();
    });

    it("should allow typing in email and password fields", async () => {
        const user = userEvent.setup();
        renderLoginPage();

        const emailInput = screen.getByPlaceholderText("login.email.placeholder");
        const passwordInput = screen.getByPlaceholderText("login.passwordPlaceholder");

        await user.type(emailInput, "test@example.com");
        await user.type(passwordInput, "password123");

        expect(emailInput).toHaveValue("test@example.com");
        expect(passwordInput).toHaveValue("password123");
    });

    it("should toggle password visibility", async () => {
        const user = userEvent.setup();
        renderLoginPage();

        const passwordInput = screen.getByPlaceholderText("login.passwordPlaceholder");
        expect(passwordInput).toHaveAttribute("type", "password");

        const toggleButton = screen.getByLabelText("password.show");
        await user.click(toggleButton);
        expect(passwordInput).toHaveAttribute("type", "text");
    });

    it("should show validation error for empty email on submit", async () => {
        const user = userEvent.setup();
        renderLoginPage();

        const submitButton = screen.getByText("login.submit");
        await user.click(submitButton);

        expect(screen.getByText("login.email.required")).toBeInTheDocument();
    });

    it("should show validation error for empty password on submit", async () => {
        const user = userEvent.setup();
        renderLoginPage();

        const emailInput = screen.getByPlaceholderText("login.email.placeholder");
        await user.type(emailInput, "test@example.com");

        const submitButton = screen.getByText("login.submit");
        await user.click(submitButton);

        expect(screen.getByText("login.password.required")).toBeInTheDocument();
    });

    it("should call login API on valid form submission", async () => {
        mockLogin.mockResolvedValue({ id: 1, username: "test", email: "test@example.com" });
        const user = userEvent.setup();
        renderLoginPage();

        const emailInput = screen.getByPlaceholderText("login.email.placeholder");
        const passwordInput = screen.getByPlaceholderText("login.passwordPlaceholder");

        await user.type(emailInput, "test@example.com");
        await user.type(passwordInput, "password123");
        await user.click(screen.getByText("login.submit"));

        expect(mockLogin).toHaveBeenCalledWith({
            email: "test@example.com",
            password: "password123",
            remember: false,
        });
    });

    it("should display error message on login failure", async () => {
        mockLogin.mockRejectedValue(new Error("auth failed"));
        const user = userEvent.setup();
        renderLoginPage();

        await user.type(screen.getByPlaceholderText("login.email.placeholder"), "test@example.com");
        await user.type(screen.getByPlaceholderText("login.passwordPlaceholder"), "wrong");
        await user.click(screen.getByText("login.submit"));

        expect(await screen.findByText("login.error.description")).toBeInTheDocument();
    });

    it("should have link to register page", () => {
        renderLoginPage();
        expect(screen.getByText("login.register")).toBeInTheDocument();
    });

    it("should have link to forgot password page", () => {
        renderLoginPage();
        expect(screen.getByText("login.forgot_password")).toBeInTheDocument();
    });

    it("should have remember me checkbox", () => {
        renderLoginPage();
        expect(screen.getByText("remember_me")).toBeInTheDocument();
    });
});

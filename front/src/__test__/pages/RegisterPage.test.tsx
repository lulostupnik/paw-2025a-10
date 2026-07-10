import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import RegisterPage from "@/pages/auth/RegisterPage";

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

const mockRegister = vi.fn();
const mockSearchInterests = vi.fn().mockResolvedValue([
    { id: 1, name: "Hiking" },
    { id: 2, name: "Photography" },
]);
vi.mock("@/hooks/useRegister", () => ({
    useRegister: () => ({
        register: mockRegister,
        loading: false,
        error: null,
        success: false,
        nextPath: "/",
        reset: vi.fn(),
    }),
}));

vi.mock("@/lib/api/catalog", () => ({
    searchCareers: vi.fn().mockResolvedValue([]),
    searchUniversities: vi.fn().mockResolvedValue([]),
    searchInterests: (...args: unknown[]) => mockSearchInterests(...args),
}));

function renderRegisterPage() {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
    });
    const router = createMemoryRouter(
        [{ path: "/register", element: <RegisterPage /> }],
        { initialEntries: ["/register"] },
    );
    return render(
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>,
    );
}

describe("RegisterPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should render registration form elements", () => {
        renderRegisterPage();
        expect(screen.getByText("register.title")).toBeInTheDocument();
        expect(screen.getByText("register.email")).toBeInTheDocument();
        expect(screen.getByText("register.username")).toBeInTheDocument();
        expect(screen.getByText("register.password")).toBeInTheDocument();
        expect(screen.getByText("register.confirmPassword")).toBeInTheDocument();
        expect(screen.getByText("register.firstName")).toBeInTheDocument();
        expect(screen.getByText("register.lastName")).toBeInTheDocument();
        expect(screen.getByText("register.submit")).toBeInTheDocument();
    });

    it("should allow typing in text fields", async () => {
        const user = userEvent.setup();
        renderRegisterPage();

        const emailInput = screen.getByPlaceholderText("correo@ejemplo.com");
        await user.type(emailInput, "test@example.com");
        expect(emailInput).toHaveValue("test@example.com");
    });

    it("should show validation errors on empty submit", async () => {
        const user = userEvent.setup();
        renderRegisterPage();

        await user.click(screen.getByText("register.submit"));

        expect(screen.getByText("register.validation.email.required")).toBeInTheDocument();
        expect(screen.getByText("register.validation.username.required")).toBeInTheDocument();
    });

    it("should show password strength indicator", async () => {
        userEvent.setup();
        renderRegisterPage();

        expect(screen.getByText("register.password.strength.label")).toBeInTheDocument();
    });

    it("should have link to login page", () => {
        renderRegisterPage();
        expect(screen.getByText("register.login")).toBeInTheDocument();
    });

    it("should toggle password visibility", async () => {
        const user = userEvent.setup();
        renderRegisterPage();

        const showButtons = screen.getAllByLabelText("register.password.show");
        expect(showButtons.length).toBeGreaterThan(0);

        await user.click(showButtons[0]);
        expect(screen.getAllByLabelText("register.password.hide").length).toBeGreaterThan(0);
    });

    it("should keep searching interests after selecting one", async () => {
        const user = userEvent.setup();
        renderRegisterPage();

        const interestInput = screen.getByPlaceholderText("register.interestsPlaceholder");
        await user.click(interestInput);

        const hikingOption = await screen.findByRole("button", { name: "Hiking" });
        await user.click(hikingOption);

        mockSearchInterests.mockClear();

        await user.type(interestInput, "p");

        await screen.findByRole("button", { name: "Photography" });
        expect(mockSearchInterests).toHaveBeenCalledWith("p", expect.anything());
    });
});

import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { server } from "../setup/setup";
import { BASE_URL } from "../utils/utils";
import CityCreatePage from "@/pages/cities/CityCreatePage";

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

const mockIsAdmin = vi.fn(() => true);
// Sólo se sustituye isAdmin: el resto del módulo lo usa el interceptor de axios (getAuthToken,
// getRefreshToken, setAuthTokens, logout) y mockearlo entero deja al apiClient sin esas funciones.
vi.mock("@/lib/auth/auth", async () => {
    const actual = await vi.importActual<typeof import("@/lib/auth/auth")>("@/lib/auth/auth");
    return { ...actual, isAdmin: () => mockIsAdmin() };
});

const mockShowToast = vi.fn();
vi.mock("@/components/ui/ToastProvider", () => ({
    useToast: () => ({ showToast: mockShowToast }),
    ToastProvider: ({ children }: { children: React.ReactNode }) => children,
}));

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
    const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
    return { ...actual, useNavigate: () => mockNavigate };
});

const renderPage = () => {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false, gcTime: 0, staleTime: 0 } },
    });
    return render(
        <QueryClientProvider client={queryClient}>
            <MemoryRouter>
                <CityCreatePage />
            </MemoryRouter>
        </QueryClientProvider>,
    );
};

describe("CityCreatePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockIsAdmin.mockReturnValue(true);
    });

    it("bloquea el acceso a quien no es admin", () => {
        mockIsAdmin.mockReturnValue(false);

        renderPage();

        expect(screen.queryByLabelText("createCity.name")).not.toBeInTheDocument();
    });

    it("crea la ciudad resolviendo el país elegido a su id y navega al detalle", async () => {
        const user = userEvent.setup();
        let body: unknown;
        server.use(
            http.post(`${BASE_URL}/cities`, async ({ request }) => {
                body = await request.json();
                return HttpResponse.json({ id: 42, name: "Córdoba" }, { status: 201 });
            }),
        );

        renderPage();
        await screen.findByLabelText("createCity.name");

        await user.type(screen.getByLabelText("createCity.name"), "Córdoba");
        await user.click(screen.getByLabelText("createCity.country"));
        await user.click(await screen.findByRole("option", { name: "Argentina" }));
        await user.click(screen.getByRole("button", { name: "createCity.submit" }));

        // El form muestra el nombre del país, pero el body referencia al recurso por id.
        await waitFor(() => expect(body).toEqual({ name: "Córdoba", countryId: 1 }));
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/cities/42", { replace: true }));
        expect(mockShowToast).toHaveBeenCalledWith("admin.toast.created", { variant: "success" });
    });

    it("no envía nada si falta el nombre", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/cities`, () => {
                called = true;
                return HttpResponse.json({ id: 1, name: "x" }, { status: 201 });
            }),
        );

        renderPage();
        await screen.findByLabelText("createCity.country");

        await user.click(screen.getByLabelText("createCity.country"));
        await user.click(await screen.findByRole("option", { name: "Argentina" }));
        await user.click(screen.getByRole("button", { name: "createCity.submit" }));

        expect(called).toBe(false);
        expect(await screen.findByText("NotNull.createCity.name")).toBeInTheDocument();
    });

    it("rechaza un país que no está en el catálogo, sin llamar a la API", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/cities`, () => {
                called = true;
                return HttpResponse.json({ id: 1, name: "x" }, { status: 201 });
            }),
        );

        renderPage();
        await screen.findByLabelText("createCity.name");

        await user.type(screen.getByLabelText("createCity.name"), "Springfield");
        await user.type(screen.getByLabelText("createCity.country"), "Narnia");
        await user.click(screen.getByRole("button", { name: "createCity.submit" }));

        expect(called).toBe(false);
        expect(await screen.findByText("ExistingCountry.createCityForm.country")).toBeInTheDocument();
    });

    it("mapea el error de campo que devuelve la API al input correspondiente", async () => {
        const user = userEvent.setup();
        server.use(
            http.post(`${BASE_URL}/cities`, () =>
                HttpResponse.json(
                    { status: 409, message: "conflict", errors: [{ field: "name", message: "Ya existe una ciudad con ese nombre" }] },
                    { status: 409 },
                ),
            ),
        );

        renderPage();
        await screen.findByLabelText("createCity.name");

        await user.type(screen.getByLabelText("createCity.name"), "Buenos Aires");
        await user.click(screen.getByLabelText("createCity.country"));
        await user.click(await screen.findByRole("option", { name: "Argentina" }));
        await user.click(screen.getByRole("button", { name: "createCity.submit" }));

        expect(await screen.findByText("Ya existe una ciudad con ese nombre")).toBeInTheDocument();
    });

    it("filtra el autocomplete de países por lo tipeado", async () => {
        const user = userEvent.setup();
        renderPage();
        await screen.findByLabelText("createCity.country");

        await user.type(screen.getByLabelText("createCity.country"), "Arg");

        expect(await screen.findByRole("option", { name: "Argentina" })).toBeInTheDocument();
        expect(screen.queryByRole("option", { name: "USA" })).not.toBeInTheDocument();
    });
});

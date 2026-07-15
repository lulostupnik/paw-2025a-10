import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { server } from "../setup/setup";
import { BASE_URL } from "../utils/utils";
import { createMockUser } from "../utils/factories";
import JourneyCreatePage from "@/pages/journeys/JourneyCreatePage";

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

// Sólo se sustituye getUserId: el resto del módulo lo usa el interceptor de axios
// (getAuthToken, getRefreshToken, setAuthTokens, logout) y mockearlo entero deja
// al apiClient sin esas funciones.
vi.mock("@/lib/auth/auth", async () => {
    const actual = await vi.importActual<typeof import("@/lib/auth/auth")>("@/lib/auth/auth");
    return { ...actual, getUserId: () => 1 };
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
                <JourneyCreatePage />
            </MemoryRouter>
        </QueryClientProvider>,
    );
};

// La página sólo muestra el form si el usuario todavía no tiene un journey: el mock por
// defecto trae journeyUrl poblado y dispararía el redirect a /journeys/1/update.
const profileWithoutJourney = http.get(`${BASE_URL}/users/1`, () =>
    HttpResponse.json(
        createMockUser({ links: { ...createMockUser().links, journeyUrl: null } }),
        { headers: { "Content-Type": "application/vnd.gotogether.user.v1+json" } },
    ),
);

// Los labels llevan un "*" de campo requerido, así que se buscan por regex y no por texto exacto.
const startDateInput = () => screen.getByLabelText(/journey\.create\.startDate/);
const endDateInput = () => screen.getByLabelText(/journey\.create\.endDate/);
// El panel del autocomplete comparte el aria-label con el input: se acota al input.
const destinationInput = () => screen.getByLabelText(/journey\.create\.destination\.label/, { selector: "input" });
const descriptionInput = () => screen.getByLabelText(/journey\.create\.description\.label/);
const submitButton = () => screen.getByRole("button", { name: "journey.create.submit" });

const pickDestination = async (user: ReturnType<typeof userEvent.setup>, name: string) => {
    await user.click(destinationInput());
    await user.click(await screen.findByRole("option", { name }));
};

const renderForm = async () => {
    renderPage();
    await screen.findByLabelText(/journey\.create\.description\.label/);
};

describe("JourneyCreatePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        server.use(profileWithoutJourney);
    });

    it("crea el journey resolviendo la universidad elegida a su id, navega al detalle y avisa con un toast", async () => {
        const user = userEvent.setup();
        let body: unknown;
        server.use(
            http.post(`${BASE_URL}/journeys`, async ({ request }) => {
                body = await request.json();
                return HttpResponse.json({ id: 33, description: "Intercambio" }, { status: 201 });
            }),
        );

        await renderForm();

        await user.type(startDateInput(), "2030-05-01");
        await user.type(endDateInput(), "2030-06-01");
        await pickDestination(user, "MIT");
        await user.type(descriptionInput(), "  Intercambio de un semestre  ");

        await user.click(submitButton());

        // El form muestra el NOMBRE de la universidad, pero el body la referencia por id.
        await waitFor(() =>
            expect(body).toEqual({
                destinationUniversityId: 1,
                startDate: "2030-05-01",
                endDate: "2030-06-01",
                description: "Intercambio de un semestre",
            }),
        );
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/journeys/33", { replace: true }));
        expect(mockShowToast).toHaveBeenCalledWith("journey.toast.created", { variant: "success" });
    });

    it("no envía nada si falta la descripción", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/journeys`, () => {
                called = true;
                return HttpResponse.json({ id: 1, description: "x" }, { status: 201 });
            }),
        );

        await renderForm();

        await user.type(startDateInput(), "2030-05-01");
        await user.type(endDateInput(), "2030-06-01");
        await pickDestination(user, "MIT");

        await user.click(submitButton());

        expect(await screen.findByText("journey.create.validation.description")).toBeInTheDocument();
        expect(called).toBe(false);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("no envía nada si falta el destino", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/journeys`, () => {
                called = true;
                return HttpResponse.json({ id: 1, description: "x" }, { status: 201 });
            }),
        );

        await renderForm();

        await user.type(startDateInput(), "2030-05-01");
        await user.type(endDateInput(), "2030-06-01");
        await user.type(descriptionInput(), "Intercambio de un semestre");

        await user.click(submitButton());

        expect(await screen.findByText("journey.create.validation.destination")).toBeInTheDocument();
        expect(called).toBe(false);
    });

    it("rechaza un rango donde la vuelta no es posterior a la ida, sin llamar a la API", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/journeys`, () => {
                called = true;
                return HttpResponse.json({ id: 1, description: "x" }, { status: 201 });
            }),
        );

        await renderForm();

        await user.type(startDateInput(), "2030-06-01");
        await user.type(endDateInput(), "2030-05-01");
        await pickDestination(user, "MIT");
        await user.type(descriptionInput(), "Intercambio de un semestre");

        await user.click(submitButton());

        expect(await screen.findByText("journey.create.validation.range")).toBeInTheDocument();
        expect(called).toBe(false);
    });

    it("rechaza una fecha de ida en el pasado, sin llamar a la API", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/journeys`, () => {
                called = true;
                return HttpResponse.json({ id: 1, description: "x" }, { status: 201 });
            }),
        );

        await renderForm();

        await user.type(startDateInput(), "2020-01-01");
        await user.type(endDateInput(), "2030-06-01");
        await pickDestination(user, "MIT");
        await user.type(descriptionInput(), "Intercambio de un semestre");

        await user.click(submitButton());

        expect(await screen.findByText("FutureDate.createJourneyForm.startDate")).toBeInTheDocument();
        expect(called).toBe(false);
    });

    it("mapea el error de campo que devuelve la API al input correspondiente", async () => {
        const user = userEvent.setup();
        server.use(
            http.post(`${BASE_URL}/journeys`, () =>
                HttpResponse.json(
                    {
                        status: 400,
                        message: "validation failed",
                        errors: [{ field: "destinationUniversityId", message: "La universidad no existe" }],
                    },
                    { status: 400 },
                ),
            ),
        );

        await renderForm();

        await user.type(startDateInput(), "2030-05-01");
        await user.type(endDateInput(), "2030-06-01");
        await pickDestination(user, "MIT");
        await user.type(descriptionInput(), "Intercambio de un semestre");

        await user.click(submitButton());

        // API_FIELD_TO_FORM_FIELD mapea "destinationUniversityId" → el input de destino del form.
        expect(await screen.findByText("La universidad no existe")).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});

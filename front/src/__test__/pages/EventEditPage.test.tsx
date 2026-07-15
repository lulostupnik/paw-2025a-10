import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { server } from "../setup/setup";
import { BASE_URL } from "../utils/utils";
import EventEditPage from "@/pages/events/EventEditPage";

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

// El evento del mock lo creó el usuario 1 (creatorUrl → /users/1).
const OWNER_ID = 1;
const mockUserId = vi.fn<() => number | null>(() => OWNER_ID);
// Sólo se sustituye getUserId: el resto del módulo lo usa el interceptor de axios (getAuthToken,
// getRefreshToken, setAuthTokens, logout) y mockearlo entero deja al apiClient sin esas funciones.
vi.mock("@/lib/auth/auth", async () => {
    const actual = await vi.importActual<typeof import("@/lib/auth/auth")>("@/lib/auth/auth");
    return { ...actual, getUserId: () => mockUserId() };
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
            <MemoryRouter initialEntries={["/events/1/edit"]}>
                <Routes>
                    <Route path="/events/:id/edit" element={<EventEditPage />} />
                </Routes>
            </MemoryRouter>
        </QueryClientProvider>,
    );
};

describe("EventEditPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUserId.mockReturnValue(OWNER_ID);
    });

    it("si el evento se actualizó pero falla la subida del flyer, navega igual en vez de invitar a reintentar el update", async () => {
        const user = userEvent.setup({ pointerEventsCheck: 0 });
        let patches = 0;
        server.use(
            http.patch(`${BASE_URL}/events/1`, () => {
                patches += 1;
                return HttpResponse.json(
                    { id: 1, title: "Test Event" },
                    { headers: { "Content-Type": "application/vnd.gotogether.event.v1+json" } },
                );
            }),
            http.put(`${BASE_URL}/events/1/flyer`, () => new HttpResponse(null, { status: 500 })),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.upload(
            screen.getByLabelText(/event\.edit\.flyer\.label/),
            new File(["flyer-bytes"], "flyer.png", { type: "image/png" }),
        );
        await user.click(screen.getByRole("button", { name: "event.edit" }));

        // Los datos ya se guardaron: quedarse en el form haría reintentar un update ya aplicado.
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/events/1"));
        expect(mockShowToast).toHaveBeenCalledWith("event.toast.updatedWithoutFlyer", { variant: "info" });
        expect(patches).toBe(1);
    });

    it("precarga el formulario con los datos del evento y guarda los cambios", async () => {
        const user = userEvent.setup();
        let body: unknown;
        server.use(
            http.patch(`${BASE_URL}/events/1`, async ({ request }) => {
                body = await request.json();
                return HttpResponse.json(
                    { id: 1, title: "Test Event" },
                    { headers: { "Content-Type": "application/vnd.gotogether.event.v1+json" } },
                );
            }),
        );

        renderPage();

        // El formulario arranca sembrado con lo que devolvió la API.
        const nameField = await screen.findByLabelText(/event\.create\.name\.label/);
        expect(nameField).toHaveValue("Test Event");
        expect(screen.getByPlaceholderText("event.create.city.placeholder")).toHaveValue("Buenos Aires");
        expect(screen.getByLabelText(/event\.create\.date\.label/)).toHaveValue("2030-06-15");
        expect(screen.getByLabelText(/event\.create\.time\.label/)).toHaveValue("18:00");
        expect(screen.getByLabelText(/event\.create\.description\.label/)).toHaveValue("A test event description");
        expect(screen.getByLabelText(/event\.create\.address\.label/)).toHaveValue("123 Test Street");
        expect(screen.getByLabelText(/event\.create\.limit\.label/)).toHaveValue("50");

        await user.clear(nameField);
        await user.type(nameField, "Renamed Event");
        await user.click(screen.getByRole("button", { name: "event.edit" }));

        // El form muestra el nombre de la ciudad, pero el body referencia al recurso por id.
        await waitFor(() =>
            expect(body).toEqual({
                cityId: 1,
                date: "2030-06-15",
                description: "A test event description",
                title: "Renamed Event",
                time: "18:00",
                address: "123 Test Street",
                attendeesLimit: 50,
            }),
        );
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/events/1"));
        expect(mockShowToast).toHaveBeenCalledWith("event.toast.updated", { variant: "success" });
    });

    it("no le muestra el formulario a quien no es el organizador", async () => {
        mockUserId.mockReturnValue(99);

        renderPage();

        expect(await screen.findByText("error.403.title")).toBeInTheDocument();
        expect(screen.queryByLabelText(/event\.create\.name\.label/)).not.toBeInTheDocument();
    });

    it("no envía nada si se borra el nombre", async () => {
        const user = userEvent.setup();
        let called = false;
        server.use(
            http.patch(`${BASE_URL}/events/1`, () => {
                called = true;
                return HttpResponse.json({ id: 1 }, { headers: { "Content-Type": "application/vnd.gotogether.event.v1+json" } });
            }),
        );

        renderPage();
        const nameField = await screen.findByLabelText(/event\.create\.name\.label/);

        await user.clear(nameField);
        await user.click(screen.getByRole("button", { name: "event.edit" }));

        expect(await screen.findByText("event.create.validation.name")).toBeInTheDocument();
        expect(called).toBe(false);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("mapea el error de campo que devuelve la API al input correspondiente", async () => {
        const user = userEvent.setup();
        server.use(
            http.patch(`${BASE_URL}/events/1`, () =>
                HttpResponse.json(
                    {
                        status: 400,
                        message: "bad request",
                        errors: [
                            { field: "title", message: "El título es demasiado largo" },
                            { field: "attendeesLimit", message: "El límite supera el máximo" },
                        ],
                    },
                    { status: 400 },
                ),
            ),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.click(screen.getByRole("button", { name: "event.edit" }));

        // title → name y attendeesLimit → participantLimit, según API_FIELD_TO_FORM_FIELD.
        expect(await screen.findByText("El título es demasiado largo")).toBeInTheDocument();
        expect(screen.getByText("El límite supera el máximo")).toBeInTheDocument();
        expect(screen.getByLabelText(/event\.create\.name\.label/)).toHaveAttribute("aria-invalid", "true");
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});

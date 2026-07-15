import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { server } from "../setup/setup";
import { BASE_URL } from "../utils/utils";
import EventCreatePage from "@/pages/events/EventCreatePage";

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
                <EventCreatePage />
            </MemoryRouter>
        </QueryClientProvider>,
    );
};

// El input del flyer está oculto (display:none) y sólo se abre a través del dropzone,
// así que se sube el archivo directo al input sin el chequeo de pointer-events.
const setupUser = () => userEvent.setup({ pointerEventsCheck: 0 });

const flyerFile = () => new File(["flyer-bytes"], "flyer.png", { type: "image/png" });

const nameInput = () => screen.getByLabelText(/event\.create\.name\.label/);
const cityInput = () => screen.getByLabelText(/event\.create\.city\.label/, { selector: "input" });
const dateInput = () => screen.getByLabelText(/event\.create\.date\.label/);
const timeInput = () => screen.getByLabelText(/event\.create\.time\.label/);
const descriptionInput = () => screen.getByLabelText(/event\.create\.description\.label/);
const addressInput = () => screen.getByLabelText(/event\.create\.address\.label/);
const limitInput = () => screen.getByLabelText(/event\.create\.limit\.label/);
const flyerInput = () => screen.getByLabelText(/event\.create\.flyer\.label/);
const submitButton = () => screen.getByRole("button", { name: "event.create.submit" });

const pickCity = async (user: ReturnType<typeof setupUser>, name: string) => {
    await user.click(cityInput());
    await user.click(await screen.findByRole("option", { name }));
};

// El PUT del flyer se pide con responseType arraybuffer: devolvemos bytes, no JSON.
const flyerHandler = (eventId: number) =>
    http.put(`${BASE_URL}/events/${eventId}/flyer`, () =>
        HttpResponse.arrayBuffer(new Uint8Array([137, 80, 78, 71]).buffer, {
            headers: { "Content-Type": "image/png" },
        }),
    );

describe("EventCreatePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("crea el evento resolviendo la ciudad elegida a su id, navega al detalle y avisa con un toast", async () => {
        const user = setupUser();
        let body: unknown;
        server.use(
            http.post(`${BASE_URL}/events`, async ({ request }) => {
                body = await request.json();
                return HttpResponse.json({ id: 7, title: "Asado en el parque" }, { status: 201 });
            }),
            flyerHandler(7),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.type(nameInput(), "Asado en el parque");
        await pickCity(user, "Buenos Aires");
        await user.type(dateInput(), "2030-05-20");
        await user.type(timeInput(), "18:30");
        await user.type(descriptionInput(), "Nos juntamos a comer");
        await user.type(addressInput(), "Av. Siempre Viva 742");
        await user.type(limitInput(), "25");
        await user.upload(flyerInput(), flyerFile());

        await user.click(submitButton());

        // El form muestra el NOMBRE de la ciudad, pero el body la referencia por id.
        await waitFor(() =>
            expect(body).toEqual({
                cityId: 1,
                date: "2030-05-20",
                description: "Nos juntamos a comer",
                title: "Asado en el parque",
                time: "18:30",
                address: "Av. Siempre Viva 742",
                attendeesLimit: 25,
            }),
        );
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/events/7", { replace: true }));
        expect(mockShowToast).toHaveBeenCalledWith("event.toast.created", { variant: "success" });
    });

    it("si el evento se creó pero falla la subida del flyer, navega igual en vez de invitar a reintentar el alta", async () => {
        const user = setupUser();
        let creates = 0;
        server.use(
            http.post(`${BASE_URL}/events`, () => {
                creates += 1;
                return HttpResponse.json({ id: 7, title: "Asado en el parque" }, { status: 201 });
            }),
            http.put(`${BASE_URL}/events/7/flyer`, () => new HttpResponse(null, { status: 500 })),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.type(nameInput(), "Asado en el parque");
        await pickCity(user, "Buenos Aires");
        await user.type(dateInput(), "2030-05-20");
        await user.type(timeInput(), "18:30");
        await user.type(descriptionInput(), "Nos juntamos a comer");
        await user.type(limitInput(), "25");
        await user.upload(flyerInput(), flyerFile());

        await user.click(submitButton());

        // El evento ya existe: quedarse en el form haría que el usuario reintente y lo duplique.
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/events/7", { replace: true }));
        expect(mockShowToast).toHaveBeenCalledWith("event.toast.createdWithoutFlyer", { variant: "info" });
        expect(creates).toBe(1);
    });

    it("manda time y attendeesLimit en null cuando el evento es de todo el día y sin cupo", async () => {
        const user = setupUser();
        let body: unknown;
        server.use(
            http.post(`${BASE_URL}/events`, async ({ request }) => {
                body = await request.json();
                return HttpResponse.json({ id: 9, title: "Feria" }, { status: 201 });
            }),
            flyerHandler(9),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.type(nameInput(), "Feria");
        await pickCity(user, "Boston");
        await user.type(dateInput(), "2030-06-01");
        await user.type(descriptionInput(), "Feria de artesanos");
        await user.click(screen.getByLabelText("event.create.allDay"));
        await user.click(screen.getByLabelText("event.create.unlimited"));
        await user.upload(flyerInput(), flyerFile());

        await user.click(submitButton());

        await waitFor(() =>
            expect(body).toEqual({
                cityId: 2,
                date: "2030-06-01",
                description: "Feria de artesanos",
                title: "Feria",
                time: null,
                address: null,
                attendeesLimit: null,
            }),
        );
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith("/events/9", { replace: true }));
    });

    it("no envía nada si falta el nombre", async () => {
        const user = setupUser();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/events`, () => {
                called = true;
                return HttpResponse.json({ id: 1, title: "x" }, { status: 201 });
            }),
            flyerHandler(1),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await pickCity(user, "Buenos Aires");
        await user.type(dateInput(), "2030-05-20");
        await user.type(timeInput(), "18:30");
        await user.type(descriptionInput(), "Nos juntamos a comer");
        await user.type(limitInput(), "25");
        await user.upload(flyerInput(), flyerFile());

        await user.click(submitButton());

        expect(await screen.findByText("event.create.validation.name")).toBeInTheDocument();
        expect(called).toBe(false);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("no envía nada si falta el flyer, que es obligatorio", async () => {
        const user = setupUser();
        let called = false;
        server.use(
            http.post(`${BASE_URL}/events`, () => {
                called = true;
                return HttpResponse.json({ id: 1, title: "x" }, { status: 201 });
            }),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.type(nameInput(), "Asado en el parque");
        await pickCity(user, "Buenos Aires");
        await user.type(dateInput(), "2030-05-20");
        await user.type(timeInput(), "18:30");
        await user.type(descriptionInput(), "Nos juntamos a comer");
        await user.type(limitInput(), "25");

        await user.click(submitButton());

        expect(await screen.findByText("event.create.validation.flyerMissing")).toBeInTheDocument();
        expect(called).toBe(false);
    });

    it("mapea el error de campo que devuelve la API al input correspondiente", async () => {
        const user = setupUser();
        server.use(
            http.post(`${BASE_URL}/events`, () =>
                HttpResponse.json(
                    {
                        status: 400,
                        message: "validation failed",
                        errors: [{ field: "title", message: "Ya existe un evento con ese título" }],
                    },
                    { status: 400 },
                ),
            ),
        );

        renderPage();
        await screen.findByLabelText(/event\.create\.name\.label/);

        await user.type(nameInput(), "Asado en el parque");
        await pickCity(user, "Buenos Aires");
        await user.type(dateInput(), "2030-05-20");
        await user.type(timeInput(), "18:30");
        await user.type(descriptionInput(), "Nos juntamos a comer");
        await user.type(limitInput(), "25");
        await user.upload(flyerInput(), flyerFile());

        await user.click(submitButton());

        const message = await screen.findByText("Ya existe un evento con ese título");
        expect(message).toBeInTheDocument();
        expect(nameInput()).toHaveAttribute("aria-invalid", "true");
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});

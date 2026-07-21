import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { server } from "../setup/setup";
import { BASE_URL } from "../utils/utils";
import JourneyEditPage from "@/pages/journeys/JourneyEditPage";

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

const AUTHOR_ID = 1;
const mockGetUserId = vi.fn<() => number | null>(() => AUTHOR_ID);
vi.mock("@/lib/auth/auth", async () => {
    const actual = await vi.importActual<typeof import("@/lib/auth/auth")>("@/lib/auth/auth");
    return { ...actual, getUserId: () => mockGetUserId() };
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

const JOURNEY_ID = 7;

const isoDateIn = (days: number) => {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return date.toISOString().slice(0, 10);
};

const START_DATE = isoDateIn(30);
const END_DATE = isoDateIn(120);

const journeyPayload = (overrides: Record<string, unknown> = {}) => ({
    id: JOURNEY_ID,
    description: "Un intercambio increíble",
    startDate: START_DATE,
    endDate: END_DATE,
    destinationUniversityName: "MIT",
    links: {
        selfUrl: `${BASE_URL}/journeys/${JOURNEY_ID}`,
        userUrl: `${BASE_URL}/users/${AUTHOR_ID}`,
        destinationUniversityUrl: `${BASE_URL}/universities/1`,
    },
    ...overrides,
});

const mockJourneyDetail = (overrides: Record<string, unknown> = {}) => {
    server.use(
        http.get(`${BASE_URL}/journeys/${JOURNEY_ID}`, () =>
            HttpResponse.json(journeyPayload(overrides), {
                headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" },
            }),
        ),
    );
};

const renderPage = () => {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false, gcTime: 0, staleTime: 0 } },
    });
    return render(
        <QueryClientProvider client={queryClient}>
            <MemoryRouter initialEntries={[`/journeys/${JOURNEY_ID}/edit`]}>
                <Routes>
                    <Route path="/journeys/:id/edit" element={<JourneyEditPage />} />
                </Routes>
            </MemoryRouter>
        </QueryClientProvider>,
    );
};

const startDateInput = () => screen.getByLabelText(/journey\.create\.startDate/);
const endDateInput = () => screen.getByLabelText(/journey\.create\.endDate/);
const destinationInput = () => screen.getByLabelText(/journey\.create\.destination\.label/, { selector: "input" });
const descriptionInput = () => screen.getByLabelText(/journey\.create\.description\.label/);
const submitButton = () => screen.getByRole("button", { name: "journey.edit.submit" });

describe("JourneyEditPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockGetUserId.mockReturnValue(AUTHOR_ID);
    });

    it("precarga el formulario con los datos del viaje y guarda los cambios referenciando el destino por id", async () => {
        const user = userEvent.setup();
        let body: unknown;
        let method: string | undefined;
        mockJourneyDetail();
        server.use(
            http.patch(`${BASE_URL}/journeys/${JOURNEY_ID}`, async ({ request }) => {
                method = request.method;
                body = await request.json();
                return HttpResponse.json(journeyPayload(), {
                    headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" },
                });
            }),
        );

        renderPage();

        expect(await screen.findByDisplayValue("Un intercambio increíble")).toBeInTheDocument();
        expect(startDateInput()).toHaveValue(START_DATE);
        expect(endDateInput()).toHaveValue(END_DATE);
        expect(destinationInput()).toHaveValue("MIT");

        await user.clear(descriptionInput());
        await user.type(descriptionInput(), "Descripción editada");
        await user.click(submitButton());

        await waitFor(() =>
            expect(body).toEqual({
                destinationUniversityId: 1,
                startDate: START_DATE,
                endDate: END_DATE,
                description: "Descripción editada",
            }),
        );
        expect(method).toBe("PATCH");
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith(`/journeys/${JOURNEY_ID}`));
        expect(mockShowToast).toHaveBeenCalledWith("journey.toast.updated", { variant: "success" });
    });

    it("no muestra el formulario a quien no es el autor del viaje", async () => {
        mockGetUserId.mockReturnValue(999);
        mockJourneyDetail();

        renderPage();

        expect(await screen.findByText("error.403.title")).toBeInTheDocument();
        expect(screen.queryByRole("button", { name: "journey.edit.submit" })).not.toBeInTheDocument();
        expect(screen.queryByLabelText(/journey\.create\.description\.label/)).not.toBeInTheDocument();
    });

    it("no envía nada si falta la descripción", async () => {
        const user = userEvent.setup();
        let called = false;
        mockJourneyDetail();
        server.use(
            http.patch(`${BASE_URL}/journeys/${JOURNEY_ID}`, () => {
                called = true;
                return HttpResponse.json(journeyPayload(), {
                    headers: { "Content-Type": "application/vnd.gotogether.journey.v1+json" },
                });
            }),
        );

        renderPage();
        await screen.findByDisplayValue("Un intercambio increíble");

        await user.clear(descriptionInput());
        await user.click(submitButton());

        expect(await screen.findByText("journey.create.validation.description")).toBeInTheDocument();
        expect(called).toBe(false);
    });

    it("mapea el error de campo que devuelve la API al input correspondiente", async () => {
        const user = userEvent.setup();
        mockJourneyDetail();
        server.use(
            http.patch(`${BASE_URL}/journeys/${JOURNEY_ID}`, () =>
                HttpResponse.json(
                    {
                        status: 400,
                        message: "bad request",
                        errors: [{ field: "destinationUniversityId", message: "La universidad no existe" }],
                    },
                    { status: 400 },
                ),
            ),
        );

        const { container } = renderPage();
        await screen.findByDisplayValue("Un intercambio increíble");

        await user.click(submitButton());

        const fieldError = await screen.findByText("La universidad no existe");
        const destinationField = container.querySelector(".create-autocomplete");
        expect(destinationField).toContainElement(fieldError);
        expect(fieldError).toHaveClass("form-field__text--error");
    });
});

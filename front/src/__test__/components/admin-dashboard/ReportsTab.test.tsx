import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import ReportsTab from "@/components/admin-dashboard/ReportsTab";
import type { ReportListItem } from "@/lib/api/reports";
import type { PageResult } from "@/types/pagination";

const navigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => ({
    ...(await importOriginal<typeof import("react-router-dom")>()),
    useNavigate: () => navigate,
}));

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({ t: (key: string) => key, locale: "es" as const, setLocale: vi.fn(), availableLocales: ["en", "es"] as const }),
    useTranslate: () => (key: string) => key,
    I18nProvider: ({ children }: { children: React.ReactNode }) => children,
}));

const user = (id: number, username: string, blocked = false) => ({
    id,
    username,
    firstname: "N",
    lastname: "A",
    email: null,
    blocked,
});

const report = (over: Partial<ReportListItem> = {}): ReportListItem => ({
    id: 1,
    description: "contenido inapropiado",
    reason: "SPAM",
    status: "PENDING",
    reportedUser: user(1, "acusado"),
    reportingUser: user(2, "denunciante"),
    createdAt: null,
    contentType: "event",
    ...over,
}) as ReportListItem;

const page = (content: ReportListItem[]): PageResult<ReportListItem> => ({
    content,
    totalElements: content.length,
    next: null,
    prev: null,
    first: null,
    last: null,
    totalPages: 1,
    currentPage: 1,
    pageSize: 10,
});

const renderTab = (props: Partial<React.ComponentProps<typeof ReportsTab>> = {}) =>
    render(
        <MemoryRouter>
            <ReportsTab
                data={page([report()])}
                searchValue=""
                onSearchChange={vi.fn()}
                onSearchSubmit={vi.fn()}
                onPageChange={vi.fn()}
                isLoading={false}
                isError={false}
                {...props}
            />
        </MemoryRouter>
    );

describe("ReportsTab", () => {
    beforeEach(() => vi.clearAllMocks());

    it("muestra al acusado y al denunciante", () => {
        renderTab();

        expect(screen.getByText("@acusado")).toBeInTheDocument();
        expect(screen.getByText("@denunciante")).toBeInTheDocument();
    });

    it("marca al usuario reportado que está bloqueado", () => {
        renderTab({ data: page([report({ reportedUser: user(1, "acusado", true) })]) });

        expect(screen.getByText("user.status.blocked")).toBeInTheDocument();
    });

    it("no marca como bloqueado al que no lo está", () => {
        renderTab();

        expect(screen.queryByText("user.status.blocked")).not.toBeInTheDocument();
    });

    it("traduce cada estado del reporte", () => {
        const estados: Array<[ReportListItem["status"], string]> = [
            ["PENDING", "report.status.pending"],
            ["UNDER_REVIEW", "report.status.under_review"],
            ["RESOLVED", "report.status.resolved"],
            ["DISMISSED", "report.status.dismissed"],
        ];
        for (const [status, key] of estados) {
            const { unmount } = renderTab({ data: page([report({ status })]) });
            expect(screen.getByText(key)).toBeInTheDocument();
            unmount();
        }
    });

    it("muestra el estado crudo si es uno desconocido, en vez de romper", () => {
        renderTab({ data: page([report({ status: "ALGO_NUEVO" as ReportListItem["status"] })]) });

        expect(screen.getByText("ALGO_NUEVO")).toBeInTheDocument();
    });

    it("traduce el tipo de contenido reportado", () => {
        renderTab({ data: page([report({ contentType: "journeyResponse" })]) });

        expect(screen.getByText("report.type.journey.comment")).toBeInTheDocument();
    });

    it("avisa cuando el reporte no trae descripción", () => {
        renderTab({ data: page([report({ description: "   " })]) });

        expect(screen.getByText("report.no.additional.details")).toBeInTheDocument();
    });

    it("normaliza la descripción pegada y los espacios de más", () => {
        renderTab({ data: page([report({ description: "EventoFiesta   con    espacios" })]) });

        expect(screen.getByText("Evento Fiesta con espacios")).toBeInTheDocument();
    });

    it("el click en la fila abre el detalle del reporte", async () => {
        const selectedReport = report({ id: 42 });
        renderTab({ data: page([selectedReport]) });

        await userEvent.click(screen.getByText("@acusado"));

        expect(navigate).toHaveBeenCalledWith("/reports/42", {
            state: {
                from: "/",
                report: selectedReport,
            },
        });
    });

    it("muestra carga y error", () => {
        const { unmount } = renderTab({ isLoading: true, data: page([]) });
        expect(screen.getByText("admin.dashboard.loading")).toBeInTheDocument();
        unmount();

        renderTab({ isError: true, data: page([]) });
        expect(screen.getByText("admin.dashboard.error")).toBeInTheDocument();
    });

    it("muestra el vacío sólo cuando no está cargando ni falló", () => {
        renderTab({ data: page([]) });

        expect(screen.getByText("admin.no.results")).toBeInTheDocument();
    });
});

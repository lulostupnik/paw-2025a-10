import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import UsersTab from "@/components/admin-dashboard/UsersTab";
import { EMPTY_ADMIN_USER_FILTERS, type AdminUser } from "@/types/admin";
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

const updateUserBlocked = vi.fn();
const invalidateUserViewQueries = vi.fn();
vi.mock("@/lib/api/users", () => ({
    updateUserBlocked: (...args: unknown[]) => updateUserBlocked(...args),
    invalidateUserViewQueries: (...args: unknown[]) => invalidateUserViewQueries(...args),
}));
vi.mock("@/lib/api/catalog", () => ({
    searchCareers: vi.fn().mockResolvedValue([]),
    searchInterests: vi.fn().mockResolvedValue([]),
    searchUniversities: vi.fn().mockResolvedValue([]),
}));

const ACTIVO: AdminUser = { id: 1, firstname: "Ana", email: "ana@t.com", university: "ITBA", blocked: false };
const BLOQUEADO: AdminUser = { id: 2, firstname: "Beto", email: "beto@t.com", university: "UBA", blocked: true };

const page = (content: AdminUser[]): PageResult<AdminUser> => ({
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

const onRefresh = vi.fn();

const renderTab = (props: Partial<React.ComponentProps<typeof UsersTab>> = {}) =>
    render(
        <QueryClientProvider client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}>
            <MemoryRouter>
                <UsersTab
                    data={page([ACTIVO, BLOQUEADO])}
                    searchValue=""
                    onSearchChange={vi.fn()}
                    onSearchSubmit={vi.fn()}
                    onPageChange={vi.fn()}
                    isLoading={false}
                    isError={false}
                    blockIconSrc="/block.svg"
                    unblockIconSrc="/unblock.svg"
                    onRefresh={onRefresh}
                    filters={EMPTY_ADMIN_USER_FILTERS}
                    onApplyFilters={vi.fn()}
                    onResetFilters={vi.fn()}
                    {...props}
                />
            </MemoryRouter>
        </QueryClientProvider>
    );

describe("UsersTab", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        updateUserBlocked.mockResolvedValue(undefined);
        invalidateUserViewQueries.mockResolvedValue(undefined);
    });

    it("lista los usuarios que recibe", () => {
        renderTab();

        expect(screen.getByText("ana@t.com")).toBeInTheDocument();
        expect(screen.getByText("beto@t.com")).toBeInTheDocument();
    });

    it("ofrece bloquear al activo y desbloquear al bloqueado", () => {
        renderTab();

        expect(screen.getByRole("button", { name: "user.block" })).toBeInTheDocument();
        expect(screen.getByRole("button", { name: "user.unblock" })).toBeInTheDocument();
    });

    it("no bloquea sin confirmar: primero abre el modal", async () => {
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.block" }));

        expect(screen.getByRole("dialog")).toBeInTheDocument();
        expect(updateUserBlocked).not.toHaveBeenCalled();
    });

    it("bloquea al confirmar y refresca la lista", async () => {
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.block" }));
        await userEvent.click(screen.getByRole("button", { name: "user.block.confirm" }));

        await waitFor(() => expect(updateUserBlocked).toHaveBeenCalledWith(ACTIVO.id, true));
        expect(onRefresh).toHaveBeenCalled();
        await waitFor(() => expect(screen.queryByRole("dialog")).not.toBeInTheDocument());
    });

    it("desbloquea con blocked=false", async () => {
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.unblock" }));
        await userEvent.click(screen.getByRole("button", { name: "user.unblock.confirm" }));

        await waitFor(() => expect(updateUserBlocked).toHaveBeenCalledWith(BLOQUEADO.id, false));
    });

    it("cancelar cierra el modal sin tocar al usuario", async () => {
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.block" }));
        await userEvent.click(screen.getByRole("button", { name: "user.block.cancel" }));

        expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
        expect(updateUserBlocked).not.toHaveBeenCalled();
    });

    it("si la API falla, muestra el error y deja el modal abierto", async () => {
        vi.spyOn(console, "error").mockImplementation(() => {});
        updateUserBlocked.mockRejectedValue(new Error("boom"));
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.block" }));
        await userEvent.click(screen.getByRole("button", { name: "user.block.confirm" }));

        await waitFor(() => expect(screen.getByRole("dialog")).toBeInTheDocument());
        expect(onRefresh).not.toHaveBeenCalled();
    });

    it("el click en la fila navega al usuario", async () => {
        renderTab();

        await userEvent.click(screen.getByText("ana@t.com"));

        expect(navigate).toHaveBeenCalledWith(`/users/${ACTIVO.id}`);
    });

    it("el botón de bloquear no dispara la navegación de la fila", async () => {
        renderTab();

        await userEvent.click(screen.getByRole("button", { name: "user.block" }));

        expect(navigate).not.toHaveBeenCalled();
    });

    it("muestra el estado de carga", () => {
        renderTab({ isLoading: true, data: page([]) });

        expect(screen.getByText("admin.dashboard.loading")).toBeInTheDocument();
    });

    it("muestra el estado de error", () => {
        renderTab({ isError: true, data: page([]) });

        expect(screen.getByText("admin.dashboard.error")).toBeInTheDocument();
    });

    it("muestra el vacío sólo cuando no está cargando ni falló", () => {
        renderTab({ data: page([]) });
        expect(screen.getByText("admin.no.results")).toBeInTheDocument();
    });

    it("no muestra el vacío mientras carga", () => {
        renderTab({ isLoading: true, data: page([]) });

        expect(screen.queryByText("admin.no.results")).not.toBeInTheDocument();
    });
});

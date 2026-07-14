import { describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import RequireAuth from "@/app/guards/RequireAuth";

const { mockIsLoggedIn, mockIsAdmin } = vi.hoisted(() => ({
    mockIsLoggedIn: vi.fn(),
    mockIsAdmin: vi.fn(),
}));

vi.mock("@/lib/auth/auth", () => ({
    isLoggedIn: () => mockIsLoggedIn(),
    isAdmin: () => mockIsAdmin(),
}));

function renderGuard(requireAdmin: boolean, initialPath = "/protected") {
    const router = createMemoryRouter(
        [
            {
                element: <RequireAuth requireAdmin={requireAdmin} />,
                children: [{ path: "/protected", element: <div>protected-content</div> }],
            },
            { path: "/login", element: <div>login-page</div> },
            { path: "/error/403", element: <div>forbidden-page</div> },
        ],
        { initialEntries: [initialPath] },
    );

    render(<RouterProvider router={router} />);
    return router;
}

describe("RequireAuth", () => {
    it("redirects anonymous users to login with a next param pointing back", async () => {
        mockIsLoggedIn.mockReturnValue(false);
        mockIsAdmin.mockReturnValue(false);

        const router = renderGuard(false, "/protected");

        await waitFor(() => expect(router.state.location.pathname).toBe("/login"));
        expect(router.state.location.search).toBe(`?next=${encodeURIComponent("/protected")}`);
        expect(screen.getByText("login-page")).toBeInTheDocument();
    });

    it("renders the protected content for a logged-in user", () => {
        mockIsLoggedIn.mockReturnValue(true);
        mockIsAdmin.mockReturnValue(false);

        renderGuard(false);

        expect(screen.getByText("protected-content")).toBeInTheDocument();
    });

    it("redirects a logged-in non-admin away from admin-only routes", async () => {
        mockIsLoggedIn.mockReturnValue(true);
        mockIsAdmin.mockReturnValue(false);

        const router = renderGuard(true);

        await waitFor(() => expect(router.state.location.pathname).toBe("/error/403"));
        expect(screen.getByText("forbidden-page")).toBeInTheDocument();
    });

    it("renders admin-only content for an admin user", () => {
        mockIsLoggedIn.mockReturnValue(true);
        mockIsAdmin.mockReturnValue(true);

        renderGuard(true);

        expect(screen.getByText("protected-content")).toBeInTheDocument();
    });
});

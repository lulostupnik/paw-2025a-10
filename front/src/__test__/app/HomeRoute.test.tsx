import { describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import HomeRoute from "@/app/guards/HomeRoute";

const { mockIsLoggedIn } = vi.hoisted(() => ({
    mockIsLoggedIn: vi.fn(),
}));

vi.mock("@/lib/auth/auth", () => ({
    isLoggedIn: () => mockIsLoggedIn(),
}));

vi.mock("@/pages/landing/LandingPage", () => ({
    default: () => <div>landing-page</div>,
}));

function renderRoute() {
    const router = createMemoryRouter(
        [
            { path: "/", element: <HomeRoute /> },
            { path: "/explore", element: <div>explore-page</div> },
        ],
        { initialEntries: ["/"] },
    );

    render(<RouterProvider router={router} />);
    return router;
}

describe("HomeRoute", () => {
    it("renders the landing page for anonymous users", () => {
        mockIsLoggedIn.mockReturnValue(false);

        const router = renderRoute();

        expect(screen.getByText("landing-page")).toBeInTheDocument();
        expect(router.state.location.pathname).toBe("/");
    });

    it("redirects logged users to explore", async () => {
        mockIsLoggedIn.mockReturnValue(true);

        const router = renderRoute();

        await waitFor(() => expect(router.state.location.pathname).toBe("/explore"));
        expect(screen.getByText("explore-page")).toBeInTheDocument();
    });
});

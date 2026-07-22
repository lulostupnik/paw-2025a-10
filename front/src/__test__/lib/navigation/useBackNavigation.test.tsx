import { describe, it, expect } from "vitest";
import { createMemoryRouter, RouterProvider, type InitialEntry } from "react-router-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { useBackNavigation } from "@/lib/navigation";

function Probe({ fallback, replace }: { fallback: string; replace?: boolean }) {
    const { from, goBack } = useBackNavigation();
    return (
        <div>
            <span data-testid="from">{from ?? "null"}</span>
            <button type="button" onClick={() => goBack(fallback, replace ? { replace: true } : undefined)}>
                back
            </button>
        </div>
    );
}

interface ProbeOptions {
    fallback?: string;
    replace?: boolean;
    initialIndex?: number;
}

function renderProbe(initialEntries: InitialEntry[], options: ProbeOptions = {}) {
    const fallback = options.fallback ?? "/events";
    const router = createMemoryRouter(
        [
            { path: "/prev", element: <div>prev</div> },
            { path: "/current", element: <Probe fallback={fallback} replace={options.replace} /> },
            { path: "/events", element: <div>events</div> },
        ],
        { initialEntries, initialIndex: options.initialIndex },
    );
    render(<RouterProvider router={router} />);
    return router;
}

const from = () => screen.getByTestId("from").textContent;
const clickBack = () => fireEvent.click(screen.getByText("back"));

describe("useBackNavigation", () => {
    it("returns the sanitized origin and navigates to it, preserving the query string", () => {
        const router = renderProbe([{ pathname: "/current", state: { from: "/events?page=2" } }]);

        expect(from()).toBe("/events?page=2");

        clickBack();

        expect(router.state.location.pathname).toBe("/events");
        expect(router.state.location.search).toBe("?page=2");
    });

    it("falls back when there is no origin (deep link / refresh)", () => {
        const router = renderProbe(["/current"], { fallback: "/events" });

        expect(from()).toBe("null");

        clickBack();

        expect(router.state.location.pathname).toBe("/events");
    });

    it.each(["https://evil.com", "//evil.com", "javascript:alert(1)", "\\evil"])(
        "ignores external/unsafe origins (%s) and uses the fallback",
        (unsafe) => {
            const router = renderProbe([{ pathname: "/current", state: { from: unsafe } }], { fallback: "/events" });

            expect(from()).toBe("null");

            clickBack();

            expect(router.state.location.pathname).toBe("/events");
        },
    );

    it("ignores an origin that points at the current page (self-guard)", () => {
        const router = renderProbe([{ pathname: "/current", state: { from: "/current" } }], { fallback: "/events" });

        expect(from()).toBe("null");

        clickBack();

        expect(router.state.location.pathname).toBe("/events");
    });

    it("prefers the origin over the fallback", () => {
        const router = renderProbe([{ pathname: "/current", state: { from: "/events?page=5" } }], { fallback: "/prev" });

        clickBack();

        expect(router.state.location.pathname).toBe("/events");
        expect(router.state.location.search).toBe("?page=5");
    });

    it("replaces the current entry when { replace: true } is passed", async () => {
        const router = renderProbe(["/prev", { pathname: "/current" }], {
            fallback: "/events",
            replace: true,
            initialIndex: 1,
        });

        clickBack();
        expect(router.state.location.pathname).toBe("/events");

        await router.navigate(-1);
        expect(router.state.location.pathname).toBe("/prev");
    });
});

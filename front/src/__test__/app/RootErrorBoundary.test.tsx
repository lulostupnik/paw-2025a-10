import { describe, it, expect, vi, afterEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { RootErrorBoundary } from "@app/RootErrorBoundary";

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string) => key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
}));

const Boom = () => {
    throw new Error("boom");
};

afterEach(() => {
    vi.restoreAllMocks();
});

describe("RootErrorBoundary", () => {
    it("renders its children while nothing throws", () => {
        render(
            <RootErrorBoundary>
                <p>contenido</p>
            </RootErrorBoundary>,
        );

        expect(screen.getByText("contenido")).toBeInTheDocument();
    });

    it("swaps a throwing tree for the error page instead of unmounting everything", () => {
        // React reporta el error atrapado por consola; no es una falla del test.
        vi.spyOn(console, "error").mockImplementation(() => {});

        render(
            <RootErrorBoundary>
                <Boom />
            </RootErrorBoundary>,
        );

        expect(screen.getByText("error.500.title")).toBeInTheDocument();
    });

    it("offers a way out that does not depend on the router", () => {
        vi.spyOn(console, "error").mockImplementation(() => {});

        render(
            <RootErrorBoundary>
                <Boom />
            </RootErrorBoundary>,
        );

        expect(screen.getByRole("link", { name: "error.action.home" })).toHaveAttribute("href", "/");
    });
});

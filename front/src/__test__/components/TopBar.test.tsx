import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import TopBar from "@/components/TopBar";

const { mockIsLoggedIn } = vi.hoisted(() => ({
    mockIsLoggedIn: vi.fn(),
}));

vi.mock("@/lib/auth/auth", () => ({
    getProfilePictureUrl: () => null,
    getUsername: () => "testuser",
    isAdmin: () => false,
    isLoggedIn: () => mockIsLoggedIn(),
    logout: vi.fn(),
    withProfilePictureVersion: (url: string | null) => url,
}));

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string) => key,
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
    localeLabels: { en: "English", es: "Español" },
}));

function renderTopBar() {
    return render(
        <MemoryRouter>
            <TopBar />
        </MemoryRouter>,
    );
}

describe("TopBar", () => {
    it("links the brand to landing for anonymous users", () => {
        mockIsLoggedIn.mockReturnValue(false);

        renderTopBar();

        expect(screen.getByRole("link", { name: "app.name" })).toHaveAttribute("href", "/");
    });

    it("links the brand to explore for logged users", () => {
        mockIsLoggedIn.mockReturnValue(true);

        renderTopBar();

        expect(screen.getByRole("link", { name: "app.name" })).toHaveAttribute("href", "/explore");
    });
});

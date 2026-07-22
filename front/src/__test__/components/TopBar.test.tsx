import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import TopBar from "@/components/TopBar";

const { mockIsLoggedIn } = vi.hoisted(() => ({
    mockIsLoggedIn: vi.fn(),
}));

let snapshot = {
    logged: false,
    username: "testuser",
    profilePictureUrl: null as string | null,
    admin: false,
};

const getSnapshot = () => snapshot;

vi.mock("@/lib/auth/auth", () => ({
    getAuthSessionSnapshot: () => getSnapshot(),
    logout: vi.fn(),
    subscribeAuthSession: () => () => undefined,
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
        snapshot = { ...snapshot, logged: false };

        renderTopBar();

        expect(screen.getByRole("link", { name: "app.name" })).toHaveAttribute("href", "/");
    });

    it("links the brand to explore for logged users", () => {
        mockIsLoggedIn.mockReturnValue(true);
        snapshot = { ...snapshot, logged: true };

        renderTopBar();

        expect(screen.getByRole("link", { name: "app.name" })).toHaveAttribute("href", "/explore");
    });
});

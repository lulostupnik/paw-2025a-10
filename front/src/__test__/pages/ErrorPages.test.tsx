import { describe, it, expect, vi } from "vitest";
import { render, screen } from "../setup/utils";
import NotFoundPage from "@/pages/errors/NotFoundPage";
import BadRequestPage from "@/pages/errors/BadRequestPage";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import MethodNotAllowedPage from "@/pages/errors/MethodNotAllowedPage";
import ServerErrorPage from "@/pages/errors/ServerErrorPage";
import UnsupportedMediaPage from "@/pages/errors/UnsupportedMediaPage";

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

describe("Error Pages", () => {
    it("should render 404 page with correct error code", () => {
        render(<NotFoundPage />);
        expect(screen.getByText("error.404.code")).toBeInTheDocument();
        expect(screen.getByText("error.404.title")).toBeInTheDocument();
        expect(screen.getByText("error.404.message")).toBeInTheDocument();
    });

    it("should render 400 page with correct error code", () => {
        render(<BadRequestPage />);
        expect(screen.getByText("error.400.code")).toBeInTheDocument();
        expect(screen.getByText("error.400.title")).toBeInTheDocument();
    });

    it("should render 403 page with correct error code", () => {
        render(<ForbiddenPage />);
        expect(screen.getByText("error.403.code")).toBeInTheDocument();
        expect(screen.getByText("error.403.title")).toBeInTheDocument();
    });

    it("should render 405 page with correct error code", () => {
        render(<MethodNotAllowedPage />);
        expect(screen.getByText("error.405.code")).toBeInTheDocument();
        expect(screen.getByText("error.405.title")).toBeInTheDocument();
    });

    it("should render 500 page with correct error code", () => {
        render(<ServerErrorPage />);
        expect(screen.getByText("error.500.code")).toBeInTheDocument();
        expect(screen.getByText("error.500.title")).toBeInTheDocument();
    });

    it("should render 415 page with correct error code", () => {
        render(<UnsupportedMediaPage />);
        expect(screen.getByText("error.415.code")).toBeInTheDocument();
        expect(screen.getByText("error.415.title")).toBeInTheDocument();
    });

    it("should include navigation links on error pages", () => {
        render(<NotFoundPage />);
        expect(screen.getByText("error.action.home")).toBeInTheDocument();
        expect(screen.getByText("nav.journeys")).toBeInTheDocument();
        expect(screen.getByText("nav.events")).toBeInTheDocument();
    });

    it("should include help section", () => {
        render(<NotFoundPage />);
        expect(screen.getByText("error.help.title")).toBeInTheDocument();
        expect(screen.getByText("error.help.contact")).toBeInTheDocument();
    });
});

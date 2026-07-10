import { describe, expect, it, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import JourneyCard from "@/components/journeys/JourneyCard";
import EventCard from "@/components/cards/EventCard";
import CreatorCard from "@/components/detail/CreatorCard";

const mockQueryClient = { prefetchQuery: vi.fn() };

const mockPrefetchJourneyDetail = vi.fn();
const mockPrefetchEventDetail = vi.fn();
const mockPrefetchProfileDetail = vi.fn();

vi.mock("@tanstack/react-query", () => ({
    useQueryClient: () => mockQueryClient,
}));

vi.mock("@/lib/i18n", () => ({
    useI18n: () => ({
        t: (key: string, options?: { values?: Record<string, unknown> }) => {
            if (key === "journey.destinationCityAndCountry") {
                return `${options?.values?.[0] ?? ""} - ${options?.values?.[1] ?? ""}`;
            }
            return key;
        },
        locale: "en" as const,
        setLocale: vi.fn(),
        availableLocales: ["en", "es"] as const,
    }),
}));

vi.mock("@/lib/utils/navigationStack", () => ({
    pushToNavigationStack: vi.fn(),
}));

vi.mock("@/lib/utils/prefetchDetail", () => ({
    prefetchJourneyDetail: (...args: unknown[]) => mockPrefetchJourneyDetail(...args),
    prefetchEventDetail: (...args: unknown[]) => mockPrefetchEventDetail(...args),
    prefetchProfileDetail: (...args: unknown[]) => mockPrefetchProfileDetail(...args),
}));

describe("detail prefetch", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("prefetches journey detail on hover", async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <JourneyCard
                    journey={{
                        id: 11,
                        description: "Trip",
                        startDate: "2026-01-01",
                        endDate: "2026-02-01",
                        city: "Madrid",
                        country: "Spain",
                        university: "Universidad",
                        userName: "traveler",
                        profilePictureUrl: null,
                    }}
                />
            </MemoryRouter>
        );

        await user.hover(screen.getByRole("link"));

        expect(mockPrefetchJourneyDetail).toHaveBeenCalledWith(mockQueryClient, 11);
    });

    it("prefetches event detail on hover", async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <EventCard
                    event={{
                        id: 21,
                        title: "Event",
                        description: "Talk",
                        date: "2026-03-10",
                        city: { name: "Berlin" },
                        user: { id: 3, username: "organizer", firstname: "Org", lastname: "Anizer" },
                    } as never}
                />
            </MemoryRouter>
        );

        await user.hover(screen.getByRole("link"));

        expect(mockPrefetchEventDetail).toHaveBeenCalledWith(mockQueryClient, 21);
    });

    it("prefetches profile detail on hover", async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <CreatorCard
                    creator={{
                        id: 31,
                        firstname: "Jane",
                        lastname: "Doe",
                        username: "jdoe",
                        profilePictureUrl: null,
                    }}
                />
            </MemoryRouter>
        );

        await user.hover(screen.getByRole("link"));

        expect(mockPrefetchProfileDetail).toHaveBeenCalledWith(mockQueryClient, 31);
    });
});

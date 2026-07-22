import { QueryClient } from "@tanstack/react-query";
import { beforeEach, describe, expect, it } from "vitest";
import {
    invalidateEventListQueries,
    invalidateJourneyListQueries,
} from "@/lib/api/queryInvalidation";

let queryClient: QueryClient;

const seed = (queryKey: unknown[]) => {
    queryClient.setQueryData(queryKey, { content: [] });
};

const isInvalidated = (queryKey: unknown[]) => {
    const state = queryClient.getQueryState(queryKey);
    expect(state, `la query ${JSON.stringify(queryKey)} no está en la cache`).toBeDefined();
    return state!.isInvalidated;
};

beforeEach(() => {
    queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false, gcTime: Infinity } },
    });
});

describe("invalidateEventListQueries", () => {
    it("invalidates every cached listing regardless of its filters", async () => {
        seed(["events", { search: "milano", page: 1 }]);
        seed(["events", { creatorId: 7, page: 3 }]);
        seed(["profileEvents", "116", { createdPage: 1 }]);
        seed(["adminEvents", { page: 1 }]);

        await invalidateEventListQueries(queryClient);

        expect(isInvalidated(["events", { search: "milano", page: 1 }])).toBe(true);
        expect(isInvalidated(["events", { creatorId: 7, page: 3 }])).toBe(true);
        expect(isInvalidated(["profileEvents", "116", { createdPage: 1 }])).toBe(true);
        expect(isInvalidated(["adminEvents", { page: 1 }])).toBe(true);
    });

    it("leaves unrelated listings alone", async () => {
        seed(["journeys", { page: 1 }]);

        await invalidateEventListQueries(queryClient);

        expect(isInvalidated(["journeys", { page: 1 }])).toBe(false);
    });
});

describe("invalidateJourneyListQueries", () => {
    it("invalidates every cached listing regardless of its filters", async () => {
        seed(["journeys", { search: "erasmus", page: 1 }]);
        seed(["profileTrips", "116", { page: 2 }]);

        await invalidateJourneyListQueries(queryClient);

        expect(isInvalidated(["journeys", { search: "erasmus", page: 1 }])).toBe(true);
        expect(isInvalidated(["profileTrips", "116", { page: 2 }])).toBe(true);
    });

    it("leaves unrelated listings alone", async () => {
        seed(["events", { page: 1 }]);

        await invalidateJourneyListQueries(queryClient);

        expect(isInvalidated(["events", { page: 1 }])).toBe(false);
    });
});

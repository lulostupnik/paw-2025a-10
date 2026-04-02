import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useJourneys } from "@/hooks/useJourneys";

describe("useJourneys", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should return journeys list", async () => {
        const { result } = renderHook(() => useJourneys());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(result.current.journeys.content).toHaveLength(2);
        expect(result.current.error).toBeNull();
    });

    it("should resolve journey summaries with user and university", async () => {
        const { result } = renderHook(() => useJourneys());

        await waitFor(() => expect(result.current.loading).toBe(false));

        const journey = result.current.journeys.content[0];
        expect(journey.id).toBe(1);
        expect(journey.description).toBe("Test journey description");
        expect(journey.userName).toBe("testuser");
        expect(journey.university).toBe("MIT");
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useJourneys());
        expect(result.current.loading).toBe(true);
    });

    it("should return pagination info", async () => {
        const { result } = renderHook(() => useJourneys());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(result.current.journeys.totalElements).toBe(2);
    });

    it("should provide a refetch function", async () => {
        const { result } = renderHook(() => useJourneys());

        await waitFor(() => expect(result.current.loading).toBe(false));

        expect(typeof result.current.refetch).toBe("function");
    });
});

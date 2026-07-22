import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";

describe("useJourneyDetailData", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch and build journey detail", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.data).not.toBeNull());

        expect(result.current.data).not.toBeNull();
        expect(result.current.data!.id).toBe(1);
        expect(result.current.data!.description).toBe("Test journey description");
        expect(result.current.isError).toBe(false);
    });

    it("should resolve user info", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.data).not.toBeNull());

        expect(result.current.data!.user).toBeDefined();
        expect(result.current.data!.user!.username).toBe("testuser");
    });

    it("should resolve destination university", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.data).not.toBeNull());

        expect(result.current.data!.destinationUniversity).toBeDefined();
        expect(result.current.data!.destinationUniversity!.name).toBe("MIT");
    });

    it("should not bundle comments/tips/interests (loaded separately by the page)", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.data).not.toBeNull());

        expect(result.current.data!.comments).toEqual([]);
        expect(result.current.data!.tips).toEqual([]);
        expect(result.current.data!.interests).toEqual([]);
    });

    it("should be disabled when no journeyId is provided", () => {
        const { result } = renderHook(() => useJourneyDetailData({}));
        expect(result.current.data).toBeNull();
        expect(result.current.isLoading).toBe(false);
    });

    it("should return null data while loading", () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));
        expect(result.current.data).toBeNull();
    });
});

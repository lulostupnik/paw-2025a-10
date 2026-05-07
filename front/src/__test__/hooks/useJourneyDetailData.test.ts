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

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).not.toBeNull();
        expect(result.current.data!.id).toBe(1);
        expect(result.current.data!.description).toBe("Test journey description");
        expect(result.current.isError).toBe(false);
    });

    it("should resolve user info", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.user).toBeDefined();
        expect(result.current.data!.user!.username).toBe("testuser");
    });

    it("should resolve destination university", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.destinationUniversity).toBeDefined();
        expect(result.current.data!.destinationUniversity!.name).toBe("MIT");
    });

    it("should include comments", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.comments).toHaveLength(1);
        expect(result.current.data!.comments[0].message).toBe("Nice journey!");
    });

    it("should include tips", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.tips).toHaveLength(1);
        expect(result.current.data!.tips[0].title).toBe("Tip 1");
        expect(result.current.data!.tips[0].content).toBe("Pack light");
    });

    it("should include user interests array", async () => {
        const { result } = renderHook(() => useJourneyDetailData({ journeyId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.interests).toBeDefined();
        expect(Array.isArray(result.current.data!.interests)).toBe(true);
        expect(result.current.data!.interests).toHaveLength(2);
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

import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useEventDetailData } from "@/hooks/useEventDetailData";

describe("useEventDetailData", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch and build event detail", async () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).not.toBeNull();
        expect(result.current.data!.id).toBe(1);
        expect(result.current.data!.title).toBe("Test Event");
        expect(result.current.isError).toBe(false);
    });

    it("should resolve creator user info", async () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.user).toBeDefined();
        expect(result.current.data!.user.username).toBe("testuser");
    });

    it("should resolve city info", async () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.city).toBeDefined();
        expect(result.current.data!.city.name).toBe("Buenos Aires");
    });

    it("should not bundle comments (loaded separately by the page)", async () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        // Comments are fetched by the page's own paginated query, not the detail hook.
        expect(result.current.data!.comments).toEqual([]);
    });

    it("should include ratings", async () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.ratings).toHaveLength(1);
        expect(result.current.data!.ratings[0].rating).toBe(5);
    });

    it("should be disabled when no eventId is provided", () => {
        const { result } = renderHook(() => useEventDetailData({}));
        expect(result.current.data).toBeNull();
        expect(result.current.isLoading).toBe(false);
    });

    it("should return null data while loading", () => {
        const { result } = renderHook(() => useEventDetailData({ eventId: "1" }));
        expect(result.current.data).toBeNull();
    });
});

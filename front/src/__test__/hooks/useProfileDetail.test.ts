import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";

describe("useProfileDetail", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch user profile detail", async () => {
        const { result } = renderHook(() => useProfileDetail("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).not.toBeNull();
        expect(result.current.data!.id).toBe(1);
        expect(result.current.data!.username).toBe("testuser");
        expect(result.current.isError).toBe(false);
    });

    it("should resolve university info", async () => {
        const { result } = renderHook(() => useProfileDetail("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.university).toBeDefined();
    });

    it("should resolve career info", async () => {
        const { result } = renderHook(() => useProfileDetail("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.career).toBeDefined();
    });

    it("should resolve rating stats", async () => {
        const { result } = renderHook(() => useProfileDetail("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.ratingStats).toBeDefined();
        // Rating stats load in a secondary query, so wait for them to populate.
        await waitFor(() => expect(result.current.data!.ratingStats!.averageCreatedEventsRating).toBe(4.8));
        expect(result.current.data!.ratingStats!.averageAttendedEventsRating).toBe(4.2);
    });

    it("should handle 'me' alias when logged in", async () => {
        localStorage.setItem("userId", "1");
        localStorage.setItem("authToken", "test-token");
        const { result } = renderHook(() => useProfileDetail("me"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).not.toBeNull();
        expect(result.current.data!.id).toBe(1);
    });

    it("should return null data while loading", () => {
        const { result } = renderHook(() => useProfileDetail("1"));
        expect(result.current.data).toBeNull();
    });

    it("should accept params object", async () => {
        const { result } = renderHook(() =>
            useProfileDetail({ profileId: "1", enabled: true }),
        );

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).not.toBeNull();
    });
});

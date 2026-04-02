import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfileTrips } from "@/hooks/profiles/useProfileTrips";

describe("useProfileTrips", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch user journey and resolve summary", async () => {
        const { result } = renderHook(() => useProfileTrips("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data).toBeDefined();
        expect(result.current.data!.id).toBe(1);
        expect(result.current.isError).toBe(false);
    });

    it("should build journey title from resolved summary", async () => {
        const { result } = renderHook(() => useProfileTrips("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.data!.title).toBeDefined();
        expect(typeof result.current.data!.title).toBe("string");
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useProfileTrips("1"));
        expect(result.current.isLoading).toBe(true);
    });

    it("should provide error state", async () => {
        const { result } = renderHook(() => useProfileTrips("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.error).toBeNull();
    });
});

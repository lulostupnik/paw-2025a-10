import { describe, it, expect, beforeEach } from "vitest";
import { renderHook, waitFor } from "../setup/utils";
import { useProfileEvents } from "@/hooks/profiles/useProfileEvents";

describe("useProfileEvents", () => {
    beforeEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it("should fetch created events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.created.content).toBeDefined();
        expect(Array.isArray(result.current.created.content)).toBe(true);
        expect(result.current.isError).toBe(false);
    });

    it("should fetch attending events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.attending.content).toBeDefined();
        expect(Array.isArray(result.current.attending.content)).toBe(true);
    });

    it("should fetch finished events for a profile", async () => {
        const { result } = renderHook(() => useProfileEvents("1"));

        await waitFor(() => expect(result.current.isLoading).toBe(false));

        expect(result.current.finished.content).toBeDefined();
        expect(Array.isArray(result.current.finished.content)).toBe(true);
    });

    it("should start in loading state", () => {
        const { result } = renderHook(() => useProfileEvents("1"));
        expect(result.current.isLoading).toBe(true);
    });

    it("should respect enabled param", () => {
        const { result } = renderHook(() =>
            useProfileEvents("1", { enabled: false }),
        );
        expect(result.current.isLoading).toBe(false);
        expect(result.current.created.content).toHaveLength(0);
    });
});
